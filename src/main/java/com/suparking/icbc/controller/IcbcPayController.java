package com.suparking.icbc.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.suparking.icbc.datamodule.projectInfoImpl.IcbcPayProjectInfo;
import com.suparking.icbc.pojo.APIOrderModel;
import com.suparking.icbc.pojo.APIOrderQueryModel;
import com.suparking.icbc.pojo.APIPayModel;
import com.suparking.icbc.pojo.APIRefundModel;
import com.suparking.icbc.pojo.APIRefundQueryModel;
import com.suparking.icbc.pojo.GenericResponse;
import com.suparking.icbc.pojo.abcnosense.AbcNoSenseInfo;
import com.suparking.icbc.service.ICBCServiceFunction;
import com.suparking.icbc.threadpool.NamedThreadPoolExecutor;
import com.suparking.icbc.threadtask.ThreadTask;
import com.suparking.icbc.tools.ConstantData;
import com.suparking.icbc.tools.OrderManagerTool;
import com.suparking.icbc.tools.ResponseFormat;
import com.suparking.icbc.tools.TimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *  AbcPayController 类集成了 农行 支付功能<br/>
 *  包含: 支付宝扫码付 刷卡支付 公众号小程序 支付宝服务窗<br/>
 *
 * @author yandex
 * @version v1.0
 */
public class IcbcPayController {
    public static final Logger LOGGER = LoggerFactory.getLogger(IcbcPayController.class);

    private static OrderManagerTool orderManagerTool = new OrderManagerTool();

    private static ICBCServiceFunction icbcServiceFunction = new ICBCServiceFunction();

    private static String abcTermNo = "903";

    private static String PayTypeWx = "WXPAY";
    private static String PayTypeAli = "ALIPAY";

    private static String PayTypeDi = "DIPAY";

    private static int corePoolSize = 800;
    private static int maximumPoolSize = 1000;
    private static int keepAliveSecond = 360;
    private static String poolName = "PayTools";
    private static String QueryPwd = "12345";

    /** TODO 默认初始化线程池实例 */

    private static ThreadPoolExecutor threadPoolExecutor = new NamedThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveSecond, poolName);
    /**
     * 初始化 支付库
     * @return
     */
    public static GenericResponse initPayPackage(String callType,String objectStr)
    {
        IcbcPayProjectInfo projectInfo = new IcbcPayProjectInfo();
        JSONObject retJsonObj = new JSONObject();
        try
        {
            if (callType.equals(ConstantData.CALL_FILE))
            {
                File file = new File("./payconfig/icbcpay.ini");
                if (FileUtil.exist(file))
                {
                    FileReader fileReader = new FileReader(file);
                    List<String> lines = fileReader.readLines();
                    if (lines.size()>0)
                    {
                        for (String line : lines)
                        {
                            if (line.contains("!=!"))
                            {
                                String[] tmpLine = line.split("!=!");
                                if (tmpLine[0].equals("MerchantId")) {
                                    tmpLine[1].split(",");
                                    projectInfo.setMerchantId(tmpLine[1]);
                                }
                                if (tmpLine[0].equals("ApiGwPublicKey")) {
                                    projectInfo.setApiGwPublicKey(tmpLine[1]);
                                }
                                if (tmpLine[0].equals("EncryptKey")) {
                                    projectInfo.setEncryptKey(tmpLine[1]);
                                }
                                else if (tmpLine[0].equals("MuchKey")) {
                                    projectInfo.setMuchkey(tmpLine[1]);
                                } else {
                                    continue;
                                }
                            }
                        }
                    }
                    if (!projectInfo.getMerchantId().equals("")&&!projectInfo.getMuchkey().equals(""))
                    {
                        retJsonObj.put("result_code","0");
                        retJsonObj.put("result_desc","初始化支付库成功");
                        retJsonObj.put("projectInfo",projectInfo);
                        projectInfo.setInitStatus(true);
                    }else
                    {
                        return ResponseFormat.retParam(1005, ConstantData.BUTT_ICBC, ConstantData.PAY_CENTER_INFO);
                    }
                }else
                {
                    return ResponseFormat.retParam(1003, ConstantData.BUTT_ICBC, ConstantData.PAY_CENTER_INFO);
                }
            } else if (callType.equals(ConstantData.CALL_NET))
            {
                JSONObject jsonObject = JSON.parseObject(objectStr);
                if (StringUtils.isEmpty(jsonObject.getString("merchantId")) || StringUtils.isEmpty(jsonObject.getString("muchkey")) || StringUtils.isEmpty(jsonObject.getString("apiGwPublicKey")))
                {
                    return ResponseFormat.retParam(1005, ConstantData.BUTT_ICBC, ConstantData.PAY_CENTER_INFO);
                }else
                {
                    if (jsonObject.containsKey("abcNoSenseInfo") && Optional.ofNullable(jsonObject.getJSONObject("abcNoSenseInfo")).isPresent()) {
                        AbcNoSenseInfo abcNoSenseInfo = JSONObject.toJavaObject(jsonObject.getJSONObject("abcNoSenseInfo"),AbcNoSenseInfo.class);
                        if (Optional.ofNullable(abcNoSenseInfo).isPresent()) {
                            projectInfo.setAbcNoSenseInfo(abcNoSenseInfo);
                        }else {
                            return ResponseFormat.retParam(1007, ConstantData.BUTT_ABC, ConstantData.PAY_CENTER_INFO);
                        }
                    }
                    /**
                     * 2021-01-13 增加icbc appid 配置
                     */
                    if (jsonObject.containsKey("icbcAppId") && !StringUtils.isEmpty(jsonObject.getString("icbcAppId"))) {
                        projectInfo.setIcbcAppId(jsonObject.getString("icbcAppId"));
                    } else {
                        projectInfo.setIcbcAppId("");
                    }
                    /**
                     * 2022-06-13 新增icbc api gw public key.
                     */
                    if (jsonObject.containsKey("apiGwPublicKey") && !StringUtils.isEmpty(jsonObject.getString("apiGwPublicKey"))) {
                       projectInfo.setApiGwPublicKey(jsonObject.getString("apiGwPublicKey"));
                    } else {
                        return ResponseFormat.retParam(1005, ConstantData.BUTT_ICBC, ConstantData.PAY_CENTER_INFO);
                    }

                    /**
                     * 2022-06-14 增加 entryKey
                     */
                    if (jsonObject.containsKey("encryptKey") && !StringUtils.isEmpty(jsonObject.getString("encryptKey"))) {
                        projectInfo.setEncryptKey(jsonObject.getString("encryptKey"));
                    } else {
                        return ResponseFormat.retParam(1005, ConstantData.BUTT_ICBC, ConstantData.PAY_CENTER_INFO);
                    }
                    projectInfo.setProjectNo(jsonObject.getString("projectNo"));
                    projectInfo.setMerchantId(jsonObject.getString("merchantId"));
                    projectInfo.setMuchkey(jsonObject.getString("muchkey"));
                    retJsonObj.put("result_code","0");
                    retJsonObj.put("result_desc","初始化支付库成功");
                    retJsonObj.put("projectInfo",projectInfo);
                    projectInfo.setInitStatus(true);
                }
            }else
            {
                return ResponseFormat.retParam(1006, ConstantData.BUTT_ICBC, ConstantData.PAY_CENTER_INFO);
            }

        }catch (Exception ex)
        {
            return ResponseFormat.retParam(1000, ConstantData.BUTT_ICBC, ex.getCause().toString());
        }
        return ResponseFormat.retParam(200, ConstantData.BUTT_ICBC, retJsonObj);
    }
    /**
     * 获取 初始化参数
     * @param initPayStr
     * @return
     */
    public static IcbcPayProjectInfo getProjectInfo(String initPayStr) {
        IcbcPayProjectInfo projectInfo = null;
        try{
            GenericResponse initRetJson = initPayPackage(ConstantData.CALL_NET, initPayStr);
            if(initRetJson.getStatus() == 200) {
                JSONObject result = (JSONObject) initRetJson.getResult();
                if (result.containsKey("projectInfo")){
                    projectInfo = (IcbcPayProjectInfo) result.get("projectInfo");
                    return projectInfo;
                }
            } else {
                LOGGER.error("SDK-ICBC-getProjectInfo 解析数据出错:{}",initRetJson.toString());
            }
        }catch (Exception ex){
            LOGGER.error("SDK-ICBC-getProjectInfo 发生未知错误",ex);
        }
        return projectInfo;
    }
    /**
     * TODO 刷卡支付
     * <p>reverser 方法详细说明</p>
     *
     * @param apiPayModel {@link APIPayModel} 刷卡支付所需要的参数
     * @param testFlag Boolean 测试开关
     * @return {@link GenericResponse}
     */
    public static GenericResponse pay(APIPayModel apiPayModel, boolean searchStatus,Boolean testFlag,Boolean succOrFailed,String initPayStr) {
        IcbcPayProjectInfo projectInfo = getProjectInfo(initPayStr);
        if (!Optional.ofNullable(projectInfo).isPresent() || !projectInfo.getInitStatus())
        {
            return ResponseFormat.retParam(1004,ConstantData.PAY,ConstantData.PAY_CENTER_INFO);
        }
        JSONObject retJsonObj = new JSONObject(1);
        JSONObject tempJson = new JSONObject();
        JSONObject unionPayRet = null;
        String timeExpire = "";
        try {
            int activeCount = threadPoolExecutor.getActiveCount();
            if (activeCount >= corePoolSize) {
                return ResponseFormat.retParam(1002, ConstantData.PAY, ConstantData.PAY_CENTER_INFO);
            }
            if (apiPayModel.getProjectNo() == null || apiPayModel.getProjectNo().length()!=6) {
                return ResponseFormat.retParam(10004, ConstantData.PAY, "项目编号不存在或者长度需6位");
            }
            if (apiPayModel.getTermInfo() == null || apiPayModel.getTermInfo().length()!=3) {
                return ResponseFormat.retParam(10004, ConstantData.PAY, "终端编号不存在或者长度需3位");
            }
            if (apiPayModel.getSubject() == null || apiPayModel.getSubject().equals("")) {
                return ResponseFormat.retParam(10004, ConstantData.PAY, ConstantData.PAY_CENTER_INFO);
            }
            if (apiPayModel.getProductId() == null || apiPayModel.getProductId().equals("")) {
                apiPayModel.setProductId("");
            }
            if (apiPayModel.getGoodsDesc() == null || apiPayModel.getGoodsDesc().equals("")) {
                return ResponseFormat.retParam(10004, ConstantData.PAY, ConstantData.PAY_CENTER_INFO);
            }
            if (apiPayModel.getGoodsDetail() == null || apiPayModel.getGoodsDetail().equals("")) {
                apiPayModel.setGoodsDetail("");
            }
            if (apiPayModel.getGoodsTag() == null || apiPayModel.getGoodsTag().equals("")) {
                apiPayModel.setGoodsTag("");
            }
            if (apiPayModel.getAttach() == null || apiPayModel.getAttach().equals("")) {
                apiPayModel.setAttach("");
            }
            if (apiPayModel.getAuthCode() == null || apiPayModel.getAuthCode().equals("")) {
                return ResponseFormat.retParam(10004, ConstantData.PAY, ConstantData.PAY_CENTER_INFO);
            }
            if (apiPayModel.getGoodsPrice() == null || apiPayModel.getGoodsPrice() < 0) {
                apiPayModel.setGoodsPrice(-1);
            }
            if (apiPayModel.getGoodsQuantity() == null || apiPayModel.getGoodsQuantity() < 0) {
                apiPayModel.setGoodsQuantity(-1);
            }
            if (apiPayModel.getTotalAmount()== null || apiPayModel.getTotalAmount() <= 0) {
                return ResponseFormat.retParam(10004, ConstantData.PAY, ConstantData.PAY_CENTER_INFO);
            }
            if (apiPayModel.getCreateIp() == null || apiPayModel.getCreateIp().equals("")) {
                apiPayModel.setCreateIp("127.0.0.1");
            }
            if (apiPayModel.getTimeStart() == null || apiPayModel.getTimeStart().equals("")) {
                return ResponseFormat.retParam(10004, ConstantData.PAY, ConstantData.PAY_CENTER_INFO);
            }
            if (apiPayModel.getTimeExpire() == null || apiPayModel.getTimeExpire().equals("")) {
                return ResponseFormat.retParam(10004, ConstantData.PAY, ConstantData.PAY_CENTER_INFO);
            }
            if (searchStatus) {
                if (apiPayModel.getNotifyUrl() == null || apiPayModel.getNotifyUrl().equals("")) {
                    return ResponseFormat.retParam(10004, ConstantData.PAY, ConstantData.PAY_CENTER_INFO);
                }
            } else {
                if (apiPayModel.getNotifyUrl() == null || apiPayModel.getNotifyUrl().equals("")) {
                    apiPayModel.setNotifyUrl("www.suparking.cn");
                }
            }
            if (apiPayModel.getOperatorId() == null || apiPayModel.getOperatorId().equals("")) {
                apiPayModel.setOperatorId("");
            }
            if (apiPayModel.getProjectOrderNo() == null || apiPayModel.getProjectOrderNo().equals("")) {
                apiPayModel.setProjectOrderNo("");
            }
            if (apiPayModel.getBusinessType() != '0' && apiPayModel.getBusinessType() != '1')
            {
                return ResponseFormat.retParam(10004, ConstantData.PAY, ConstantData.PAY_CENTER_INFO);
            }
            if (testFlag)
            {
                if (succOrFailed)
                {
                    retJsonObj.put("result_code","0");
                    retJsonObj.put("result_desc","刷卡支付成功");
                }else{
                    retJsonObj.put("result_code","1");
                    retJsonObj.put("result_desc","刷卡支付失败");
                }

                return ResponseFormat.retParam(200, ConstantData.PAY, retJsonObj);
            }
            JSONObject payRet = new JSONObject();
            apiPayModel.setTimeStart(apiPayModel.getTimeStart());
            apiPayModel.setTimeExpire(apiPayModel.getTimeExpire());

            // 根据用户提供的 起始 时间 转换成 支付中心的起始时间
            long timeStamp = icbcServiceFunction.getTimestamp(apiPayModel.getTimeStart(),apiPayModel.getTimeExpire());
            if(timeStamp < 0)
            {
                return ResponseFormat.retParam(10001,ConstantData.PAY,"结束时间小于开始时间");
            }
            String tempStartTime = TimeUtils.getQueryCodeDate();
            apiPayModel.setTimeStart(tempStartTime);
            apiPayModel.setTimeExpire(icbcServiceFunction.getTimeExpire(tempStartTime,timeStamp));
            timeExpire = apiPayModel.getTimeExpire();
            boolean paystatus = false;
            boolean closestatus = false;
            String orderNo ="";
            String payType = icbcServiceFunction.getCarPayType(apiPayModel.getAuthCode());
            if (!payType.equals(PayTypeWx)&&!payType.equals(PayTypeAli) && !payType.equals(PayTypeDi))
            {
                return ResponseFormat.retParam(1000, ConstantData.PAY, payType);
            }
            if (payType.equals(PayTypeWx))
            {
                orderNo = orderManagerTool.getOrderNo(apiPayModel.getProjectNo(), apiPayModel.getTermInfo(),apiPayModel.getIncreaseValue(),apiPayModel.getBusinessType(),ConstantData.ICBC_CHANNEL,ConstantData.ICBC_WX);
            } else if (payType.equals(PayTypeAli)) {

                orderNo = orderManagerTool.getOrderNo(apiPayModel.getProjectNo(), apiPayModel.getTermInfo(),apiPayModel.getIncreaseValue(),apiPayModel.getBusinessType(),ConstantData.ICBC_CHANNEL,ConstantData.ICBC_ALI);
            } else {
                orderNo = orderManagerTool.getOrderNo(apiPayModel.getProjectNo(), apiPayModel.getTermInfo(),apiPayModel.getIncreaseValue(),apiPayModel.getBusinessType(),ConstantData.ICBC_CHANNEL,ConstantData.ICBC_DI);
            }

            if(orderNo.length()>30) {
                return ResponseFormat.retParam(10018,ConstantData.PAY,ConstantData.PAY_CENTER_INFO);
            }

            payRet = icbcServiceFunction.icbcPayFunction(projectInfo, apiPayModel, orderNo, payType);

            if (payRet.containsKey("result_code") && payRet.containsKey("result_desc") && "0000".equals(retJsonObj.getString("result_code"))) {
                paystatus = true;
            }
            //下面将 返回的结果 拿到业务处使用
            //全局订单号
            retJsonObj.put("order_no", orderNo);
            retJsonObj.put("native_order_no", orderNo);
            retJsonObj.put("project_order_no", apiPayModel.getProjectOrderNo());
            retJsonObj.put("pay_type", payType);
            retJsonObj.put("platform",ConstantData.BUTT_ICBC);
            if (paystatus) {
                // TODO 判断三个对接商的成功的状态码 下单并扣款成功 最终result_code 才为0
                if ((payRet.getString("result_code")).contains("0000")) {
                    retJsonObj.put("result_code", "0");
                    tempJson.put("trade_type", payRet.get("trade_type"));
                    tempJson.put("pay_result", payRet.get("pay_result"));
                    tempJson.put("pay_info", payRet.get("pay_info"));
                    tempJson.put("time_end", payRet.get("time_end"));
                    retJsonObj.put("result_desc", tempJson.toJSONString());
                    retJsonObj.put("out_trade_no",payRet.getString("out_trade_no"));
                } else {
                    retJsonObj.put("result_code", payRet.get("result_code"));
                    retJsonObj.put("result_desc",payRet.get("result_desc"));
                }

                // TODO 根据 调用者的状态 判断是否开启线程
                if (!payRet.getString("result_code").contains("0000")) {
                    if (searchStatus) {
                        ThreadTask threadTask = new ThreadTask(retJsonObj.getString("out_trade_no"),
                                apiPayModel.getTimeExpire(), apiPayModel.getProjectOrderNo(),
                                apiPayModel.getNotifyUrl(), orderNo,
                                projectInfo);
                        threadPoolExecutor.execute(threadTask);
                    }
                } else {
                    retJsonObj.put("trade_state", ConstantData.PAY_SUCCESS);
                }
            } else {
                retJsonObj.put("result_code", payRet.getString("result_code"));
                retJsonObj.put("result_desc", payRet.getString("result_desc"));
            }
        } catch (Exception ex) {
            String errorstr = "";
            StackTraceElement[] stackTraceElements =  ex.getStackTrace();
            for (StackTraceElement s : stackTraceElements)
            {
                errorstr += "className:"+s.getClassName()+"/"+s.getFileName()+"/"+s.getMethodName()+"/"+s.getLineNumber()+"/";
            }
            return ResponseFormat.retParam(1000, ConstantData.PAY, "刷卡下单接口:Pay出现问题"+errorstr);
        }
        return ResponseFormat.retParam(200, ConstantData.PAY, retJsonObj);
    }

    /**
     * TODO 退款查询
     * <p>withdraw 方法详细说明</p>
     *
     * @param apiRefundQueryModel {@link APIRefundQueryModel} 退款查询所需要的参数
     * @param testFlag Boolean 测试开关
     * @return {@link GenericResponse}
     */
    public static GenericResponse refundquery(APIRefundQueryModel apiRefundQueryModel, Boolean testFlag, Boolean succOrFailed,String initPayStr) {
        IcbcPayProjectInfo projectInfo = getProjectInfo(initPayStr);
        if (!Optional.ofNullable(projectInfo).isPresent() || !projectInfo.getInitStatus())
        {
            return ResponseFormat.retParam(1004,ConstantData.REFUNDQUERY,ConstantData.PAY_CENTER_INFO);
        }
        JSONObject retJsonObj = new JSONObject(1);
        JSONObject refundQueryRet = new JSONObject();
        JSONObject tempJsonRet = new JSONObject();
        Integer retCode = -1;
        try {
            if (apiRefundQueryModel.getProjectNo() == null || apiRefundQueryModel.getProjectNo().equals("")) {
                apiRefundQueryModel.setProjectNo("");
            }
            if (apiRefundQueryModel.getOrderNo() == null || apiRefundQueryModel.getOrderNo().equals("")) {
                return ResponseFormat.retParam(10004, ConstantData.REFUNDQUERY, ConstantData.PAY_CENTER_INFO);
            }
            String termNo = apiRefundQueryModel.getOrderNo().substring(23,26);
            if (abcTermNo.equals(termNo)) {
                return ResponseFormat.retParam(1008, ConstantData.REFUNDQUERY, ConstantData.PAY_CENTER_INFO);
            }
            if (apiRefundQueryModel.getRefundNo() == null || apiRefundQueryModel.getRefundNo().equals("")) {
                return ResponseFormat.retParam(10004, ConstantData.REFUNDQUERY, ConstantData.PAY_CENTER_INFO);
            }
            if (apiRefundQueryModel.getProjectRefundNo() == null || apiRefundQueryModel.getProjectRefundNo().equals("")) {
                apiRefundQueryModel.setProjectRefundNo("");
            }
            if (testFlag)
            {
                if (succOrFailed)
                {
                    retJsonObj.put("result_code","0");
                    retJsonObj.put("result_desc","退款查询成功");
                }else{
                    retJsonObj.put("result_code","1");
                    retJsonObj.put("result_desc","退款查询失败");
                }
                return ResponseFormat.retParam(200, ConstantData.REFUNDQUERY, retJsonObj);
            }
            tempJsonRet.put("out_refund_no", apiRefundQueryModel.getRefundNo());
            retJsonObj.put("refund_no", apiRefundQueryModel.getRefundNo());
            retJsonObj.put("project_refund_no", apiRefundQueryModel.getProjectRefundNo());
            refundQueryRet = icbcServiceFunction.icbcRefundQueryFunction(projectInfo,tempJsonRet);
            if (!refundQueryRet.containsKey("result_code") ||
                    !refundQueryRet.containsKey("result_desc") ||
                    !refundQueryRet.getString("result_code").contains("0000") ||
                    !refundQueryRet.getString("result_desc").contains("成功"))
            {
                retJsonObj.put("result_code", refundQueryRet.get("result_code"));
                retJsonObj.put("result_desc", refundQueryRet.get("result_desc"));
                retJsonObj.put("trade_state", ConstantData.REFUND_FAILED);
            }else
            {
                retJsonObj.put("result_code", "0");
                retJsonObj.put("result_desc", tempJsonRet.toJSONString());
                retJsonObj.put("trade_state", ConstantData.REFUND_SUCCESS);
            }
        } catch (Exception ex) {
            return ResponseFormat.retParam(1000, ConstantData.REFUNDQUERY, "退款查询接口:refundQuery出现问题");
        }
        return ResponseFormat.retParam(200, ConstantData.REFUNDQUERY, retJsonObj);
    }

    /**
     * TODO 退款
     * <p>withdraw 方法详细说明</p>
     *
     * @param apiRefundModel {@link APIRefundModel} 退款所需要的参数
     * @param testFlag Boolean 测试开关
     * @return {@link GenericResponse}
     */
    public static GenericResponse refund(APIRefundModel apiRefundModel, Boolean testFlag,Boolean succOrFailed,String initPayStr) {
        IcbcPayProjectInfo projectInfo = getProjectInfo(initPayStr);
        if (!Optional.ofNullable(projectInfo).isPresent() || !projectInfo.getInitStatus())
        {
            return ResponseFormat.retParam(1004,ConstantData.REFUND,ConstantData.PAY_CENTER_INFO);
        }
        JSONObject retJsonObj = new JSONObject(1);
        JSONObject refundRet = new JSONObject();
        JSONObject abcRefundRet = new JSONObject();
        String out_refund_no = "";
        boolean refundstatus = false;
        try {
            if (apiRefundModel.getProjectNo() == null || apiRefundModel.getProjectNo().equals("")) {
                apiRefundModel.setProjectNo("");
            }
            if (apiRefundModel.getOrderNo() == null || apiRefundModel.getOrderNo().equals("")) {
                return ResponseFormat.retParam(10004, ConstantData.REFUND, ConstantData.PAY_CENTER_INFO);
            }
            /** 2020-07-09 增加 无感支付判断*/
            String termNo = apiRefundModel.getOrderNo().substring(23,26);
            if (termNo.equals(abcTermNo))  {
                if (StringUtils.isEmpty(apiRefundModel.getPayParkingId())) {
                    return ResponseFormat.retParam(10004, ConstantData.REFUND, ConstantData.PAY_CENTER_INFO);
                }
                if (!Optional.ofNullable(projectInfo.getAbcNoSenseInfo()).isPresent() || !Optional.ofNullable(projectInfo.getAbcNoSenseInfo().getPartnerId()).isPresent()) {
                    return ResponseFormat.retParam(1007, ConstantData.REFUND, ConstantData.PAY_CENTER_INFO);
                }
            }
            if (apiRefundModel.getProjectOrderNo() == null || apiRefundModel.getProjectOrderNo().equals("")) {
                apiRefundModel.setProjectOrderNo("");
            }

            if (apiRefundModel.getTermInfo() == null || apiRefundModel.getTermInfo().equals("")) {
                return ResponseFormat.retParam(10004, ConstantData.REFUND, ConstantData.PAY_CENTER_INFO);
            }
            if (apiRefundModel.getProjectRefundNo() == null || apiRefundModel.getProjectRefundNo().equals("")) {
                apiRefundModel.setProjectRefundNo("");
            }
            if (apiRefundModel.getTotalAmount() < 0 || apiRefundModel.getRefundAmount() < 0)
            {
                return ResponseFormat.retParam(10004, ConstantData.REFUND, ConstantData.PAY_CENTER_INFO);
            }
            if (apiRefundModel.getTotalAmount() < apiRefundModel.getRefundAmount()) {
                return ResponseFormat.retParam(20009, ConstantData.REFUND, ConstantData.PAY_CENTER_INFO);
            }
            if (apiRefundModel.getNotifyUrl() == null || apiRefundModel.getNotifyUrl().equals("")) {
                apiRefundModel.setNotifyUrl("");
            }
            if (apiRefundModel.getOperatorId() == null || apiRefundModel.getOperatorId().equals("")) {
                return ResponseFormat.retParam(10004, ConstantData.REFUND, ConstantData.PAY_CENTER_INFO);
            }
            if (apiRefundModel.getTimeCreate() == null) {
                return ResponseFormat.retParam(10004, ConstantData.REFUND, ConstantData.PAY_CENTER_INFO);
            }
            if (apiRefundModel.getBusinessType() != 'A' && apiRefundModel.getBusinessType() != 'B')
            {
                return ResponseFormat.retParam(10004, ConstantData.REFUND, ConstantData.PAY_CENTER_INFO);
            }
            if (testFlag)
            {
                if (succOrFailed)
                {
                    retJsonObj.put("result_code","0");
                    retJsonObj.put("result_desc","退款成功");
                }else{
                    retJsonObj.put("result_code","1");
                    retJsonObj.put("result_desc","退款失败");
                }
                return ResponseFormat.retParam(200, ConstantData.REFUND, retJsonObj);
            }
            //进行 退款调用
            char payType = apiRefundModel.getOrderNo().charAt(apiRefundModel.getOrderNo().length()-1);
            JSONObject tempRefundJson = new JSONObject();
            if (!termNo.equals(abcTermNo)) {
                termNo = apiRefundModel.getTermInfo();
            }

            out_refund_no = orderManagerTool.getOrderNo(apiRefundModel.getProjectNo(), termNo,apiRefundModel.getIncreaseValue(),apiRefundModel.getBusinessType(), ConstantData.ICBC_CHANNEL,payType);
            if (termNo.equals(abcTermNo)) {
                abcRefundRet = icbcServiceFunction.abcNoSenseRefundFunction(projectInfo,apiRefundModel);
                if (!abcRefundRet.containsKey("result_code") ||
                        !abcRefundRet.containsKey("result_desc") ||
                        !((String) abcRefundRet.get("result_code")).contains("0000") ||
                        !((String) abcRefundRet.get("result_desc")).contains("成功"))
                {
                    retJsonObj.put("result_code", abcRefundRet.get("result_code"));
                    retJsonObj.put("result_desc", abcRefundRet.get("result_desc"));
                } else {
                    retJsonObj.put("result_code", "0");
                    retJsonObj.put("result_desc","退费成功");
                }
                retJsonObj.put("seqNo",abcRefundRet.getString("seqNo"));
                retJsonObj.put("orgSeqNo",apiRefundModel.getPayParkingId());
                retJsonObj.put("order_no", apiRefundModel.getOrderNo());
                retJsonObj.put("refund_no", out_refund_no);
            } else {
                tempRefundJson.put("out_trade_no", apiRefundModel.getOrderNo());
                tempRefundJson.put("out_refund_no", out_refund_no);
                tempRefundJson.put("total_fee", apiRefundModel.getRefundAmount());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
                tempRefundJson.put("ori_pay_time", simpleDateFormat.format(apiRefundModel.getTimeCreate()));
                refundRet = icbcServiceFunction.icbcRefundFunction(projectInfo,apiRefundModel,tempRefundJson);
                if (!refundRet.containsKey("result_code") ||
                        !refundRet.containsKey("result_desc") ||
                        !((String) refundRet.get("result_code")).contains("0000") ||
                        !((String) refundRet.get("result_desc")).contains("成功"))
                {
                    retJsonObj.put("result_code", refundRet.get("result_code"));
                    retJsonObj.put("result_desc", refundRet.get("result_desc"));
                }
                else
                {
                    refundstatus = true;
                }
                if (refundstatus) {
                    // 如果申请退
                    JSONObject result = new JSONObject();
                    retJsonObj.put("result_code", "0");
                    result.put("out_trade_no", refundRet.getString("out_trade_no"));
                    result.put("out_refund_no", out_refund_no);
                    result.put("refund_id",refundRet.get("refund_id"));
                    result.put("refund_fee", refundRet.get("refund_fee"));

                    retJsonObj.put("order_no", apiRefundModel.getOrderNo());
                    retJsonObj.put("project_order_no", apiRefundModel.getProjectOrderNo());
                    retJsonObj.put("refund_no", out_refund_no);
                    retJsonObj.put("project_refund_no", apiRefundModel.getProjectRefundNo());
                    retJsonObj.put("result_desc", result.toJSONString());

                    //如果异步通知地址不为空,那么就调动异步通知
                    if ((null != apiRefundModel.getNotifyUrl()) && (!apiRefundModel.getNotifyUrl().equals(""))) {
                        // 如果异步通知 地址 有那么就发送异步通知
                        JSONObject notifyJson = new JSONObject();
                        notifyJson.put("status", 200);
                        notifyJson.put("message", "执行成功");
                        JSONObject tempJson = new JSONObject();
                        tempJson.put("order_no", out_refund_no);
                        tempJson.put("project_order_no", apiRefundModel.getProjectOrderNo());
                        tempJson.put("trade_time", TimeUtils.getQueryCodeDate());
                        JSONObject tempNotifyJson = new JSONObject();
                        tempNotifyJson.put("result_code", "0");
                        tempNotifyJson.put("result_desc", tempJson.toJSONString());
                        tempNotifyJson.put("order_no", apiRefundModel.getOrderNo());
                        tempNotifyJson.put("project_order_no", apiRefundModel.getProjectOrderNo());
                        tempNotifyJson.put("refund_no", out_refund_no);
                        tempNotifyJson.put("project_refund_no", apiRefundModel.getProjectRefundNo());
                        notifyJson.put("result", tempNotifyJson);

                        icbcServiceFunction.Notify(apiRefundModel.getNotifyUrl(), notifyJson.toJSONString());
                    }
                }
            }
        } catch (Exception ex) {
            return ResponseFormat.retParam(1000, ConstantData.REFUND, "退款接口:Refund出现问题");
        }
        return ResponseFormat.retParam(200, ConstantData.REFUND, retJsonObj);
    }


    /**
     * TODO 订单查询
     * <p>withdraw 方法详细说明</p>
     *
     * @param apiOrderQueryModel {@link APIOrderQueryModel} 订单查询所需要的参数
     * @param testFlag Boolean 测试开关
     * @return {@link GenericResponse}
     */
    public static GenericResponse orderquery(APIOrderQueryModel apiOrderQueryModel,Boolean testFlag,Boolean succOrFailed,String initPayStr) {
        IcbcPayProjectInfo projectInfo = getProjectInfo(initPayStr);
        if (!Optional.ofNullable(projectInfo).isPresent() || !projectInfo.getInitStatus())
        {
            return ResponseFormat.retParam(1004,ConstantData.ORDERQUERY,ConstantData.PAY_CENTER_INFO);
        }
        JSONObject retJsonObj = new JSONObject(1);
        try {
            if (apiOrderQueryModel.getProjectNo() == null || apiOrderQueryModel.getProjectNo().equals("")) {
                apiOrderQueryModel.setProjectNo("");
            }
            if (apiOrderQueryModel.getOrderNo() == null || apiOrderQueryModel.getOrderNo().equals("")) {
                return ResponseFormat.retParam(10004, ConstantData.ORDERQUERY, ConstantData.PAY_CENTER_INFO);
            }
            if (apiOrderQueryModel.getProjectOrderNo() == null || apiOrderQueryModel.getProjectOrderNo().equals("")) {
                apiOrderQueryModel.setProjectOrderNo("");
            }
            /** 开启测试模式 */
            if (testFlag)
            {
                if (succOrFailed)
                {
                    retJsonObj.put("result_code","0");
                    retJsonObj.put("result_desc","查询订单成功");
                }else{
                    retJsonObj.put("result_code","1");
                    retJsonObj.put("result_desc","查询订单失败");
                }
                return ResponseFormat.retParam(200, ConstantData.ORDERQUERY, retJsonObj);
            }
            String trade_state = "";
            String orderNo = apiOrderQueryModel.getOrderNo();
            retJsonObj.put("result_code", "0");
            // TODO 通过查询第三方支付平台关于订单支付状态
            JSONObject tradeRet = icbcServiceFunction.icbcTradeQueryFunction(projectInfo,orderNo);
            if (!tradeRet.containsKey("result_code") ||
                    !tradeRet.containsKey("result_desc") ||
                    !tradeRet.getString("result_code").contains("0000") ||
                    !tradeRet.getString("result_desc").contains("成功"))
            {
                retJsonObj.put("result_code", tradeRet.get("result_code"));
                retJsonObj.put("result_desc", tradeRet.get("result_desc"));
            }
            else
            {
                // 订单查询返回,根据返回的状态类型,进行提示
                trade_state = "";
                trade_state = (String) tradeRet.getJSONObject("response").get("tradeStatus");
                if (trade_state.equals(ConstantData.PAY_SUCCESS))
                {
                    retJsonObj.put("result_code","0");
                }else if (trade_state.equals(ConstantData.PAY_NOTPAY))
                {
                    retJsonObj.put("result_code","AA");
                }
                else
                {
                    retJsonObj.put("result_code","AB");
                    retJsonObj.put("result_desc",trade_state);
                }

            }
            // TODO 根据状态码判断,去掉支付方式返回
            retJsonObj.put("trade_state", trade_state);
            retJsonObj.put("order_no", apiOrderQueryModel.getOrderNo());
            retJsonObj.put("project_order_no", apiOrderQueryModel.getProjectOrderNo());
        } catch (Exception ex) {
            return ResponseFormat.retParam(1000, ConstantData.ORDERQUERY, "订单查询接口:orderQuery出现问题");
        }
        return ResponseFormat.retParam(200, ConstantData.ORDERQUERY, retJsonObj);
    }

    /**
     * TODO 下单
     * <p>withdraw 方法详细说明</p>
     * @param apiOrderModel {@link APIOrderModel} 下单所需要的参数
     * @param searchStatus {@link Boolean} 是否需要查询
     * @param testFlag Boolean 测试开关
     * @return {@link GenericResponse}
     */
    public static GenericResponse order(APIOrderModel apiOrderModel, Boolean searchStatus,Boolean testFlag,Boolean succOrFailed,String initPayStr) {
        IcbcPayProjectInfo projectInfo = getProjectInfo(initPayStr);
        if (!Optional.ofNullable(projectInfo).isPresent() || !projectInfo.getInitStatus())
        {
            return ResponseFormat.retParam(1004,ConstantData.ORDER,ConstantData.PAY_CENTER_INFO);
        }
        JSONObject retJsonObj = new JSONObject(1);
        String trade_type = "";
        try {
            int activeCount = threadPoolExecutor.getActiveCount();
            if (activeCount >= corePoolSize) {
                return ResponseFormat.retParam(1002, ConstantData.ORDER, ConstantData.PAY_CENTER_INFO);
            }
            if (apiOrderModel.getProjectNo() == null || apiOrderModel.getProjectNo().length()!=6) {
                return ResponseFormat.retParam(10004, ConstantData.ORDER, "项目编号不存在或者长度需6位");
            }
            if (apiOrderModel.getTermInfo() == null || apiOrderModel.getTermInfo().length()!=3) {
                return ResponseFormat.retParam(10004, ConstantData.ORDER, "终端编号不存在或者长度需3位");
            }
            if (apiOrderModel.getSubject() == null || apiOrderModel.getSubject().equals("")) {
                return ResponseFormat.retParam(10004, ConstantData.ORDER, ConstantData.PAY_CENTER_INFO);
            }
            if (apiOrderModel.getProductId() == null || apiOrderModel.getProductId().equals("")) {
                apiOrderModel.setProductId("");
            }
            if (apiOrderModel.getGoodsDesc() == null || apiOrderModel.getGoodsDesc().equals("")) {
                return ResponseFormat.retParam(10004, ConstantData.ORDER, ConstantData.PAY_CENTER_INFO);
            }

            if (apiOrderModel.getGoodsDetail() == null || apiOrderModel.getGoodsDetail().equals("")) {
                apiOrderModel.setGoodsDetail("");
            }
            if (apiOrderModel.getGoodsTag() == null || apiOrderModel.getGoodsTag().equals("")) {
                apiOrderModel.setGoodsTag("");
            }
            if (apiOrderModel.getAttach() == null || apiOrderModel.getAttach().equals("")) {
                apiOrderModel.setAttach("");
            }
            if (apiOrderModel.getGoodsPrice() == null)
            {
                apiOrderModel.setGoodsPrice(-1);
            }

            // TODO GoodsPrice不做类型判断
            if (apiOrderModel.getTotalAmount() < 0) {
                return ResponseFormat.retParam(10004, ConstantData.ORDER, ConstantData.PAY_CENTER_INFO);
            }
            if (apiOrderModel.getCreateIp() == null || apiOrderModel.getCreateIp().equals("")) {
                apiOrderModel.setCreateIp("127.0.0.1");
            }
            if (apiOrderModel.getTimeStart() == null || apiOrderModel.getTimeStart().equals("")) {
                return ResponseFormat.retParam(10004, ConstantData.ORDER, ConstantData.PAY_CENTER_INFO);
            }
            if (apiOrderModel.getTimeExpire() == null || apiOrderModel.getTimeExpire().equals("")) {
                return ResponseFormat.retParam(10004, ConstantData.ORDER, ConstantData.PAY_CENTER_INFO);
            }
            if (searchStatus) {
                if (apiOrderModel.getNotifyUrl() == null || apiOrderModel.getNotifyUrl().equals("")) {
                    return ResponseFormat.retParam(10004, ConstantData.ORDER, ConstantData.PAY_CENTER_INFO);
                }
            } else {
                if (apiOrderModel.getNotifyUrl() == null || apiOrderModel.getNotifyUrl().equals("")) {
                    apiOrderModel.setNotifyUrl("");
                }
            }

            if (apiOrderModel.getOperatorId() == null || apiOrderModel.getOperatorId().equals("")) {
                apiOrderModel.setOperatorId("");
            }
            if (apiOrderModel.getProjectOrderNo() == null || apiOrderModel.getProjectOrderNo().equals("")) {
                return ResponseFormat.retParam(10004, ConstantData.ORDER, ConstantData.PAY_CENTER_INFO);
            } else {
                if (apiOrderModel.getProjectOrderNo().length() > 20) {
                    return ResponseFormat.retParam(10005, ConstantData.ORDER, ConstantData.PAY_CENTER_INFO);
                }
            }
            if (apiOrderModel.getBusinessType() != '0' && apiOrderModel.getBusinessType() != '1')
            {
                return ResponseFormat.retParam(10004, ConstantData.ORDER, ConstantData.PAY_CENTER_INFO);
            }
            if (apiOrderModel.getAppid() == null || apiOrderModel.getAppid().equals("")) {
                apiOrderModel.setAppid("");
            }
            if (apiOrderModel.getSubopenid() == null || apiOrderModel.getSubopenid().equals("")) {
                apiOrderModel.setSubopenid("");
            }
            if (apiOrderModel.getMappid() == null || apiOrderModel.getMappid().equals("")) {
                apiOrderModel.setMappid("");
            }
            if (apiOrderModel.getTradetype() == null || apiOrderModel.getTradetype().equals("")) {
                return ResponseFormat.retParam(10015, ConstantData.ORDER, ConstantData.PAY_CENTER_INFO);
            } else {
                if (apiOrderModel.getTradetype().compareTo(ConstantData.COMMONTRADETYPE) == 0) {
                    if (apiOrderModel.getProductId().length() <= 0) {
                        return ResponseFormat.retParam(10012, ConstantData.ORDER, ConstantData.PAY_CENTER_INFO);
                    }
                }
                trade_type = apiOrderModel.getTradetype();
                if (trade_type.compareTo(ConstantData.WETCHATOFFICAL) == 0 ||
                        trade_type.compareTo(ConstantData.WETCHATMINI) == 0 ||
                        trade_type.compareTo(ConstantData.COMMONTRADETYPE) == 0 ||
                        trade_type.compareTo(ConstantData.APPTRADETYPE) == 0 ||
                        trade_type.compareTo(ConstantData.ALIJSPAY) == 0 ) {

                    if (trade_type.compareTo(ConstantData.WETCHATOFFICAL) == 0 || trade_type.compareTo(ConstantData.WETCHATMINI) == 0) {
                        if (apiOrderModel.getSubopenid().length() != 28 || (apiOrderModel.getAppid().length() != 18)) {
                            return ResponseFormat.retParam(10009, ConstantData.ORDER, ConstantData.PAY_CENTER_INFO);
                        }
                    }
                    // 判断支付宝服务窗支付所需要的字段数据
                    if (trade_type.compareTo(ConstantData.ALIJSPAY) == 0)
                    {
                        if ((apiOrderModel.getBuyerId() == null || apiOrderModel.getBuyerId().length() <=0 ))
                        {
                            return ResponseFormat.retParam(10016, ConstantData.ORDER, ConstantData.PAY_CENTER_INFO);
                        }

                    }
                    // 目前 提供的 公众号 小程序支付
                    if (trade_type.compareTo(ConstantData.APPTRADETYPE) == 0) {
                        if (apiOrderModel.getMappid().length() <= 0) {
                            return ResponseFormat.retParam(10010, ConstantData.ORDER, ConstantData.PAY_CENTER_INFO);
                        }
                    }
                } else {
                    return ResponseFormat.retParam(10008, ConstantData.ORDER, ConstantData.PAY_CENTER_INFO);
                }
            }
            if (testFlag)
            {
                if (succOrFailed)
                {
                    retJsonObj.put("result_code","0");
                    retJsonObj.put("result_desc","统一下单成功");
                }else{
                    retJsonObj.put("result_code","1");
                    retJsonObj.put("result_desc","统一下单失败");
                }
                return ResponseFormat.retParam(200, ConstantData.ORDER, retJsonObj);
            }
            /**
             * 根据支付平台信息,和下单方式进行下单
             * 组织下单所需数据,进行下单操作,目前Native 下单默认是微信支付宝同时下单
             */
            String timeExpire = "";
            String globalOrder = "";

            // 根据用户提供的 起始 时间 转换成 支付中心的起始时间
            long timeStamp = icbcServiceFunction.getTimestamp(apiOrderModel.getTimeStart(), apiOrderModel.getTimeExpire());
            if (timeStamp < 0) {
                return ResponseFormat.retParam(10001, ConstantData.ORDER, "结束时间小于开始时间");
            }
            timeExpire = apiOrderModel.getTimeExpire();


            /** 根据使用者下单方式进行调用不同的下单接口 */
            JSONObject orderRet = new JSONObject();
            if (trade_type.equals(ConstantData.WETCHATOFFICAL) || trade_type.equals(ConstantData.WETCHATMINI) || trade_type.equals(ConstantData.ALIJSPAY))
            {
                if (trade_type.equals(ConstantData.WETCHATOFFICAL) || trade_type.equals(ConstantData.WETCHATMINI)) {
                    globalOrder = orderManagerTool.getOrderNo(apiOrderModel.getProjectNo(), apiOrderModel.getTermInfo(),apiOrderModel.getIncreaseValue(),apiOrderModel.getBusinessType(),ConstantData.ICBC_CHANNEL,ConstantData.ICBC_WX);
                } else {
                    globalOrder = orderManagerTool.getOrderNo(apiOrderModel.getProjectNo(), apiOrderModel.getTermInfo(),apiOrderModel.getIncreaseValue(),apiOrderModel.getBusinessType(),ConstantData.ICBC_CHANNEL,ConstantData.ICBC_ALI);
                }
                orderRet = icbcServiceFunction.icbcOrderFunction(projectInfo,apiOrderModel,globalOrder, trade_type);
            }else if (trade_type.equals(ConstantData.COMMONTRADETYPE))
            {
                globalOrder = orderManagerTool.getOrderNo(apiOrderModel.getProjectNo(), apiOrderModel.getTermInfo(),apiOrderModel.getIncreaseValue(),apiOrderModel.getBusinessType(),ConstantData.ICBC_CHANNEL,ConstantData.ICBC_ALI);
                Integer temptimestamp = Math.toIntExact(timeStamp / 1000);
                apiOrderModel.setTimeExpire(temptimestamp.toString());
                // 普通的二维码扫码支付 只支持支付宝扫码,微信取消
                orderRet = icbcServiceFunction.icbcNativeOrderFunction(projectInfo,apiOrderModel,globalOrder);
            }else if (trade_type.equals(ConstantData.APPTRADETYPE) || trade_type.equals(ConstantData.UNIONJSPAY))
            {
                return ResponseFormat.retParam(20001,ConstantData.ORDER,ConstantData.PAY_CENTER_INFO);
            }
            if (trade_type.equals(ConstantData.WETCHATMINI) ||
                    trade_type.equals(ConstantData.WETCHATOFFICAL) ||
                    trade_type.equals(ConstantData.ALIJSPAY))
            {
                if (!orderRet.containsKey("result_code") ||
                        !orderRet.containsKey("result_desc") ||
                        !orderRet.getString("result_code").contains("0000") ||
                        !orderRet.getString("result_desc").contains("成功"))
                {
                    retJsonObj.put("result_code", orderRet.get("result_code"));
                    retJsonObj.put("result_desc", orderRet.get("result_desc"));
                }else
                {
                    retJsonObj.put("result_code", "0");
                    retJsonObj.put("result_desc", orderRet.get("result_desc"));
                    retJsonObj.put("payInfo", orderRet.get("payInfo"));
                    retJsonObj.put("out_trade_no",orderRet.get("out_trade_no"));
                }
            }else
            {
                if (!orderRet.containsKey("result_code") ||
                        !orderRet.containsKey("result_desc") ||
                        !orderRet.getString("result_code").contains("0000") ||
                        !orderRet.getString("result_desc").contains("成功"))
                {
                    retJsonObj.put("result_code", orderRet.get("result_code"));
                    retJsonObj.put("result_desc", orderRet.get("result_desc"));
                }
                else{
                    if (trade_type.equals(ConstantData.COMMONTRADETYPE))
                    {
                        retJsonObj.put("code_url",orderRet.getString("code_url"));
                    }
                    retJsonObj.put("out_trade_no",orderRet.get("out_trade_no"));
                }

            }
            retJsonObj.put("project_order_no", apiOrderModel.getProjectOrderNo());
            retJsonObj.put("platform",ConstantData.BUTT_ICBC);
            if (searchStatus) {
                // TODO 聚合支付 不需要 事先 启动状态查询
                ThreadTask threadTask = new ThreadTask(retJsonObj.getString("out_trade_no"),
                        timeExpire, apiOrderModel.getProjectOrderNo(), apiOrderModel.getNotifyUrl(), globalOrder, projectInfo);
                threadPoolExecutor.execute(threadTask);
            }
        } catch (
                Exception ex) {
            return ResponseFormat.retParam(1000, ConstantData.ORDER, "下单接口:Order出现问题");
        }
        return ResponseFormat.retParam(200, ConstantData.ORDER, retJsonObj);
    }

}
