package com.suparking.icbc.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.suparking.icbc.datamodule.ICBC.ICBCJsOrderNode;
import com.suparking.icbc.datamodule.ICBC.ICBCOrderNode;
import com.suparking.icbc.datamodule.ICBC.ICBCOrderQueryNode;
import com.suparking.icbc.datamodule.ICBC.ICBCPayNode;
import com.suparking.icbc.datamodule.ICBC.ICBCRefundNode;
import com.suparking.icbc.datamodule.ICBC.ICBCRefundQueryNode;
import com.suparking.icbc.datamodule.PlatformLists;
import com.suparking.icbc.datamodule.ProjectInfo;
import com.suparking.icbc.pojo.APIOrderModel;
import com.suparking.icbc.pojo.APIPayModel;
import com.suparking.icbc.pojo.APIRefundModel;
import com.suparking.icbc.pojo.APIWithDrawaModel;
import com.suparking.icbc.tools.ConstantData;
import com.suparking.icbc.tools.HttpUtils;
import com.suparking.icbc.tools.OrderManagerTool;
import com.suparking.icbc.tools.SnowflakeConfig;
import com.suparking.icbc.tools.TimeUtils;
import com.suparking.icbc.tools.UuidAndMD5Utils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ICBCServiceFunction {
    public static final Logger LOGGER = LoggerFactory.getLogger(ICBCServiceFunction.class);
    private static ICBCPaymentServiceImpl icbcpaymentService = new ICBCPaymentServiceImpl();

    private static OrderManagerTool orderManagerTool = new OrderManagerTool();

    private static final String systemIp = "127.0.0.1";
    private static String EpayCloudUrl = "http://order.suparking.cn";

    private static final String FIND_ABC_ORDER = "/AbcLocal/FindAbcOrder";

    private static String PayTypeWx = "WXPAY";

    private static String PayTypeAli = "ALIPAY";

    private static String PayTypeDi = "DIPAY";
    /**
     * 请求结果判断　key
     */
    private static final String RET_KEY = "resultCode";
    /**
     * 请求结果判断　value
     */
    private static final String RET_VALUE = "0000";
    /**
     * 各种通知
     * @param Url
     * @param Params
     */
    public void Notify(String Url,String Params) throws Exception {
        try
        {
            String reviceJsomStr = HttpUtils.sendPost(Url,Params);
        }catch (Exception ex)
        {
            throw new Exception("Notify 发送通知 异常",ex);
        }
    }

    /**
     * 原生的二维码订单支付.
     * @param projectInfo {@link ProjectInfo}
     * @param apiOrderModel {@link APIOrderModel}
     * @param globalOrder order no.
     * @return {@link JSONObject}
     */
    public JSONObject icbcNativeOrderFunction(ProjectInfo projectInfo, APIOrderModel apiOrderModel, String globalOrder) {
        JSONObject result = new JSONObject();
        try {
            String icbc_native_order_url  = PlatformLists.getPlatformUrl(ConstantData.BUTT_ICBC,ConstantData.ICBC_ORDER);
            if(StringUtils.isNotBlank(icbc_native_order_url)) {
                Map<String,String> urlMap = getUrls(icbc_native_order_url);
                ICBCOrderNode icbcOrderNode = new ICBCOrderNode();
                icbcOrderNode.setUrl(urlMap.get("url"));
                icbcOrderNode.setApp_id(projectInfo.getIcbcAppId());
                icbcOrderNode.setMsg_id(String.valueOf(SnowflakeConfig.snowflakeId()));
                icbcOrderNode.setFormat("json");
                icbcOrderNode.setCharset("UTF-8");
                icbcOrderNode.setSign_type("RSA2");

                icbcOrderNode.setAppId(projectInfo.getIcbcAppId());
                icbcOrderNode.setOutVendorId(projectInfo.getMerchantId());
                icbcOrderNode.setOutUserId(apiOrderModel.getProjectOrderNo() + apiOrderModel.getTermInfo() + TimeUtils.getPayDate());
                icbcOrderNode.setNotifyUrl(apiOrderModel.getNotifyUrl());
                icbcOrderNode.setOutOrderId(globalOrder);
                icbcOrderNode.setPayExpire(apiOrderModel.getTimeExpire());
                icbcOrderNode.setGoodsName(apiOrderModel.getGoodsDesc());
                icbcOrderNode.setTrxIp(apiOrderModel.getCreateIp());
                icbcOrderNode.setTrxChannel("09");
                icbcOrderNode.setPayAmount(apiOrderModel.getTotalAmount());
                icbcOrderNode.setPayType("01");
                icbcOrderNode.setVarNote(apiOrderModel.getGoodsDesc() + "-" + apiOrderModel.getAttach());
                LOGGER.info("SDK-ICBC-Native-PAY 原生二维码支付 参数: " + icbcOrderNode);
                String retJsonStr = icbcpaymentService.nativeOrder(projectInfo, icbcOrderNode);
                LOGGER.info("SDK-ICBC-Native-PAY 原生二维码支付 返回: " + retJsonStr);
                if (StringUtils.isBlank(retJsonStr)) {
                    result.put("result_code", "20003");
                } else {
                    JSONObject retJsonObj = JSON.parseObject(retJsonStr);
                    result.put("result_code", retJsonObj.getString("result_code"));
                    result.put("result_desc", retJsonObj.getString("result_desc"));
                    if (retJsonObj.containsKey("result_code") && retJsonObj.containsKey("result_desc") && "0000".equals(retJsonObj.getString("result_code"))) {
                        result.put("code_url", retJsonObj.getString("qrCode"));
                    }
                    result.put("out_trade_no",globalOrder);
                }
            }

        }catch (Exception ex) {
            result.put("result_code","20004");
            result.put("result_desc",ex.getMessage());
        }
        return result;
    }

    /**
     * H5 JS 支付
     * @param projectInfo
     * @param apiOrderModel
     * @param globalOrder
     * @return
     */
    public JSONObject icbcOrderFunction(ProjectInfo projectInfo, APIOrderModel apiOrderModel, String globalOrder, String trade_type) {
        JSONObject result = new JSONObject();
        try {
            String icbc_h5_order_url  = PlatformLists.getPlatformUrl(ConstantData.BUTT_ICBC,ConstantData.ICBC_JSAPI_ORDER);
            if(StringUtils.isNotBlank(icbc_h5_order_url)) {
                Map<String,String> urlMap = getUrls(icbc_h5_order_url);
                ICBCJsOrderNode icbcJsOrderNode = new ICBCJsOrderNode();
                icbcJsOrderNode.setUrl(urlMap.get("url"));
                icbcJsOrderNode.setApp_id(projectInfo.getIcbcAppId());
                icbcJsOrderNode.setMsg_id(String.valueOf(SnowflakeConfig.snowflakeId()));
                icbcJsOrderNode.setFormat("json");
                icbcJsOrderNode.setCharset("UTF-8");
                icbcJsOrderNode.setSign_type("RSA2");

                icbcJsOrderNode.setAppId(projectInfo.getIcbcAppId());
                icbcJsOrderNode.setOutOrderId(globalOrder);
                icbcJsOrderNode.setOutVendorId(projectInfo.getMerchantId());
                icbcJsOrderNode.setOutUserId(apiOrderModel.getProjectOrderNo() + apiOrderModel.getTermInfo() + TimeUtils.getPayDate());
                icbcJsOrderNode.setPayAmount(apiOrderModel.getTotalAmount());
                icbcJsOrderNode.setPayType("01");
                icbcJsOrderNode.setNotifyUrl(apiOrderModel.getNotifyUrl());
                if (trade_type.equals(ConstantData.WETCHATMINI)) {
                   icbcJsOrderNode.setPayMode("01");
                }
                if (trade_type.equals(ConstantData.WETCHATOFFICAL)) {
                    icbcJsOrderNode.setPayMode("03");
                }

                if (trade_type.equals(ConstantData.ALIJSPAY)) {
                    icbcJsOrderNode.setPayMode("02");
                }

                icbcJsOrderNode.setTrxChannel("03");
                if (trade_type.equals(ConstantData.WETCHATMINI) || trade_type.equals(ConstantData.WETCHATOFFICAL)) {
                    icbcJsOrderNode.setTradeType(apiOrderModel.getTradetype());
                    icbcJsOrderNode.setTpAppId(apiOrderModel.getAppid());
                    icbcJsOrderNode.setTpOpenId(apiOrderModel.getSubopenid());

                } else if (trade_type.equals(ConstantData.ALIJSPAY)) {
                    icbcJsOrderNode.setTradeType(apiOrderModel.getTradetype());
                    icbcJsOrderNode.setUnionId(apiOrderModel.getBuyerId());
                }
                if(apiOrderModel.getGoodsDetail().length() > 0 && apiOrderModel.getAttach().length() > 0)
                {
                    icbcJsOrderNode.setVarVote(apiOrderModel.getAttach());
                }
                else if(apiOrderModel.getGoodsDetail().length() > 0 && apiOrderModel.getAttach().length() == 0)
                {
                    icbcJsOrderNode.setVarVote(apiOrderModel.getGoodsDetail());
                }
                else if(apiOrderModel.getGoodsDetail().length() == 0 && apiOrderModel.getAttach().length() > 0)
                {
                    icbcJsOrderNode.setVarVote(apiOrderModel.getAttach());
                }
                else
                {
                    icbcJsOrderNode.setVarVote(apiOrderModel.getGoodsDesc());
                }
                icbcJsOrderNode.setGoodsName(apiOrderModel.getGoodsDesc());
                icbcJsOrderNode.setTrxIp(apiOrderModel.getCreateIp());
                LOGGER.info("SDK-ICBC-JS-PAY H5支付 参数: " + icbcJsOrderNode);
                String retJsonStr = icbcpaymentService.order(projectInfo, icbcJsOrderNode);
                LOGGER.info("SDK-ICBC-JS-PAY H5支付 返回: " + retJsonStr);
                if (StringUtils.isBlank(retJsonStr)) {
                    result.put("result_code", "20003");
                } else {
                    JSONObject retJsonObj = JSON.parseObject(retJsonStr);
                    result.put("result_code", retJsonObj.getString("result_code"));
                    result.put("result_desc", retJsonObj.getString("result_desc"));
                    if (retJsonObj.containsKey("result_code") && retJsonObj.containsKey("result_desc") && "0000".equals(retJsonObj.getString("result_code"))) {
                        result.put("code_url", retJsonObj.getString("qrCode"));
                    }
                    result.put("out_trade_no",globalOrder);
                }

            }

        }catch (Exception ex) {
            result.put("result_code","20004");
            result.put("result_desc",ex);
        }
        return result;
    }

    /**
     * 工行 微信 支付宝 数币 刷卡支付
     * @param projectInfo
     * @param apiPayModel
     * @param orderNo
     * @return
     */
    public JSONObject icbcPayFunction(ProjectInfo projectInfo, APIPayModel apiPayModel, String orderNo, String payType) {
       JSONObject result = new JSONObject();
       try {
           String codeType = "01";
           if (payType.equals(PayTypeWx) || payType.equals(PayTypeAli))
           {
               codeType = "01";

           } else if (payType.equals(PayTypeDi))
           {
               codeType = "02";
           }

           String icbc_pay_url  = PlatformLists.getPlatformUrl(ConstantData.BUTT_ICBC,ConstantData.ICBC_PAY);
           if(StringUtils.isNotBlank(icbc_pay_url)) {
               Map<String,String> urlMap = getUrls(icbc_pay_url);
               ICBCPayNode icbcPayNode = new ICBCPayNode();
               icbcPayNode.setUrl(urlMap.get("url"));
               icbcPayNode.setApp_id(projectInfo.getIcbcAppId());
               icbcPayNode.setMsg_id(String.valueOf(SnowflakeConfig.snowflakeId()));
               icbcPayNode.setFormat("json");
               icbcPayNode.setCharset("UTF-8");
               icbcPayNode.setSign_type("RSA2");

               icbcPayNode.setAppId(projectInfo.getIcbcAppId());
               icbcPayNode.setOutVendorId(projectInfo.getMerchantId());
               icbcPayNode.setOutUserId(apiPayModel.getProjectOrderNo() + apiPayModel.getTermInfo() + TimeUtils.getPayDate());
               icbcPayNode.setNotifyUrl(apiPayModel.getNotifyUrl());
               icbcPayNode.setOutOrderId(orderNo);
               icbcPayNode.setGoodsName(apiPayModel.getGoodsDesc());
               icbcPayNode.setTrxIp(apiPayModel.getCreateIp());
               icbcPayNode.setTrxChannel("08");
               icbcPayNode.setPayAmount(apiPayModel.getTotalAmount());
               icbcPayNode.setQrCode(apiPayModel.getAuthCode());
               icbcPayNode.setPayType("01");
               icbcPayNode.setVarNote(apiPayModel.getGoodsDesc() + "-" + apiPayModel.getAttach());
               icbcPayNode.setCodeType(codeType);
               LOGGER.info("SDK-ICBC-PAY 刷卡支付" + payType + ", 参数: " + icbcPayNode);
               String retJsonStr = icbcpaymentService.pay(projectInfo, icbcPayNode);
               LOGGER.info("SDK-ICBC-PAY 刷卡支付" + payType + ", 返回: " + retJsonStr);
               if (StringUtils.isBlank(retJsonStr)) {
                   result.put("result_code", "20003");
               } else {
                   JSONObject retJsonObj = JSON.parseObject(retJsonStr);
                   result.put("result_code", retJsonObj.getString("result_code"));
                   result.put("result_desc", retJsonObj.getString("result_desc"));
                   if (retJsonObj.containsKey("result_code") && retJsonObj.containsKey("result_desc") && "0000".equals(retJsonObj.getString("result_code"))) {
                      result.put("trade_type", "micropay");
                      result.put("pay_result", retJsonObj.getString("orderStatus"));
                      result.put("pay_info", retJsonStr);
                      result.put("time_end", retJsonObj.getString("payTime"));
                   }
                   result.put("out_trade_no",retJsonObj.getString("orderId"));
               }
           }

       }catch (Exception ex) {
           result.put("result_code","20004");
           result.put("result_desc",ex);
       }
        return result;
    }

    /**
     * 农行无感退费
     * @param projectInfo
     * @param apiRefundModel
     * @return
     */
    public JSONObject abcNoSenseRefundFunction(ProjectInfo projectInfo, APIRefundModel apiRefundModel) {
        JSONObject result = new JSONObject();
        try {
            Map<String, Object> paramterMap = new HashMap<>();
            paramterMap.put("partnerId",projectInfo.getAbcNoSenseInfo().getPartnerId());
            paramterMap.put("seqNo", UuidAndMD5Utils.getUuid());
            /** 此处根据 业务订单号以及 预支付ID 向epay 平台查询 真正的业务第三方订单号 */
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("orderNo",apiRefundModel.getOrderNo());
            jsonParams.put("payParkingId",apiRefundModel.getPayParkingId());
            LOGGER.info("SDK-ICBC-ABCRefund 农行无感支付退费 参数:{}",jsonParams.toJSONString());
            String retStr = HttpUtils.sendPost(EpayCloudUrl + FIND_ABC_ORDER,jsonParams.toJSONString());
            LOGGER.info("SDK-ICBC-ABCRefund 农行无感支付退费返回:{}",retStr);
            JSONObject retJson = JSONObject.parseObject(retStr);
            if (retJson.containsKey("code") && retJson.getString("code").equals("00000") && retJson.containsKey("abcOrderNo")) {
                paramterMap.put("orgSeqNo",retJson.getString("abcOrderNo"));
                String  tmpResult = icbcpaymentService.sendRequest4Abc(paramterMap);
                if (StringUtils.isNotEmpty(tmpResult)) {
                    JSONObject jsonObject = JSONObject.parseObject(tmpResult);
                    if (jsonObject.containsKey(RET_KEY) && jsonObject.getString(RET_KEY).equals(RET_VALUE)) {
                        result.put("result_code","0000");
                        result.put("result_desc","执行成功");
                        result.put("seqNo",paramterMap.get("seqNo"));
                        result.put("orgSeqNo",paramterMap.get("abcOrderNo"));
                    } else {
                        result.put("result_code",jsonObject.getString("resultCode"));
                        result.put("result_desc", jsonObject.getString("resultDes"));
                    }
                }
            } else {
                result.put("result_code",retJson.getString("code"));
                result.put("result_desc",retJson.getString("msg"));
            }

        } catch (Exception ex) {
            result.put("result_code","20004");
            result.put("result_desc",ex);
        }
        return result;
    }

    /**
     * 工行 退款 接口
     * @param projectInfo
     * @return
     */
    public JSONObject icbcRefundFunction(ProjectInfo projectInfo, APIRefundModel apiRefundModel, JSONObject jsonObject)
    {
        JSONObject result = new JSONObject();
        try{
            String icbc_refund_url  = PlatformLists.getPlatformUrl(ConstantData.BUTT_ICBC,ConstantData.ICBC_REFUND);
            if(StringUtils.isNotBlank(icbc_refund_url)) {
                Map<String,String> urlMap = getUrls(icbc_refund_url);
                ICBCRefundNode icbcRefundNode = new ICBCRefundNode();
                icbcRefundNode.setUrl(urlMap.get("url"));
                icbcRefundNode.setApp_id(projectInfo.getIcbcAppId());
                icbcRefundNode.setMsg_id(String.valueOf(SnowflakeConfig.snowflakeId()));
                icbcRefundNode.setFormat("json");
                icbcRefundNode.setCharset("UTF-8");
                icbcRefundNode.setSign_type("RSA2");

                icbcRefundNode.setAppId(projectInfo.getIcbcAppId());
                icbcRefundNode.setVendorId(projectInfo.getMerchantId());
                icbcRefundNode.setUserId(apiRefundModel.getProjectNo() + apiRefundModel.getTermInfo()+TimeUtils.getPayDate());
                icbcRefundNode.setPayType("01");
                icbcRefundNode.setOrderId((String) jsonObject.get("out_trade_no"));
                icbcRefundNode.setRefundId((String) jsonObject.get("out_refund_no"));
                icbcRefundNode.setRefundAmount((Integer) jsonObject.get("total_fee"));
                icbcRefundNode.setNotifyUrl(apiRefundModel.getNotifyUrl());
                icbcRefundNode.setExtension(ConstantData.PAY_CENTER_INFO);
                LOGGER.info("SDK-ICBC 退费 参数:{}",icbcRefundNode);
                String retJsonStr = icbcpaymentService.refund(projectInfo,icbcRefundNode);
                LOGGER.info("SDK-ICBC 退费 返回:{}",retJsonStr);
                // 根据返回的json字符串进行结果的分析
                if(retJsonStr.equals("")) {
                    result.put("result_code","20003");
                }
                else
                {
                    JSONObject retJsonObj = JSON.parseObject(retJsonStr);
                    result.put("result_code", retJsonObj.getString("result_code"));
                    result.put("result_desc", retJsonObj.getString("result_desc"));
                    if (retJsonObj.containsKey("result_code") && retJsonObj.containsKey("result_desc") && "0000".equals(retJsonObj.getString("result_code"))) {
                        result.put("out_trade_no",retJsonObj.get("out_trade_no"));
                        result.put("out_refund_no",retJsonObj.get("out_refund_no"));
                        result.put("refund_id",retJsonObj.get("refund_id"));
                        result.put("refund_fee",retJsonObj.get("refund_fee"));
                    }
                }
            }
        }catch (Exception ex)
        {
            result.put("result_code","20004");
            result.put("result_desc",ex);
        }
        return result;
    }

    /**
     * 工行退款查询接口
     * @param projectInfo
     * @return
     */
    public JSONObject icbcRefundQueryFunction(ProjectInfo projectInfo,JSONObject jsonObject)
    {
        JSONObject result = new JSONObject();
        try{
            String icbc_refundquery_url  = PlatformLists.getPlatformUrl(ConstantData.BUTT_ICBC,ConstantData.ICBC_REFUNDQUERY);
            if(StringUtils.isNotBlank(icbc_refundquery_url)) {
                Map<String,String> urlMap = getUrls(icbc_refundquery_url);
                ICBCRefundQueryNode icbcRefundQueryNode = new ICBCRefundQueryNode();
                icbcRefundQueryNode.setUrl(urlMap.get("url"));
                icbcRefundQueryNode.setApp_id(projectInfo.getIcbcAppId());
                icbcRefundQueryNode.setMsg_id(String.valueOf(SnowflakeConfig.snowflakeId()));
                icbcRefundQueryNode.setFormat("json");
                icbcRefundQueryNode.setCharset("UTF-8");
                icbcRefundQueryNode.setSign_type("RSA2");

                icbcRefundQueryNode.setAppId(projectInfo.getIcbcAppId());
                icbcRefundQueryNode.setPayType("01");
                icbcRefundQueryNode.setIsParent("1");
                icbcRefundQueryNode.setVendorId(projectInfo.getMerchantId());
                icbcRefundQueryNode.setRefundId((String) jsonObject.get("out_refund_no"));

                LOGGER.info("SDK-ICBC 退费查询 参数:{}",icbcRefundQueryNode);
                String retJsonStr = icbcpaymentService.refundquery(projectInfo,icbcRefundQueryNode);
                LOGGER.info("SDK-ICBC 退费查询 返回:{}",retJsonStr);
                // 根据返回的json字符串进行结果的分析
                if(retJsonStr.equals("")) {
                    result.put("result_code","20003");
                }
                else
                {
                    JSONObject retJsonObj = JSON.parseObject(retJsonStr);
                    if(!retJsonObj.containsKey("ICBC_API_RETCODE")||
                            !retJsonObj.containsKey("ICBC_API_RETMSG")||
                            0 != (Integer)retJsonObj.get("ICBC_API_RETCODE"))
                    {
                        result.put("result_code",retJsonObj.get("ICBC_API_RETCODE").toString());
                        result.put("result_desc",retJsonObj.get("ICBC_API_RETMSG"));
                    }
                    else
                    {
                        if (!retJsonObj.containsKey("hostRspCode") ||
                                !retJsonObj.containsKey("hostRspMsg") ||
                                !((String)retJsonObj.get("hostRspCode")).contains("0000") ||
                                !((String)retJsonObj.get("hostRspMsg")).contains("成功")) {
                            result.put("result_code", retJsonObj.get("hostRspCode"));
                            result.put("result_desc", retJsonObj.get("hostRspMsg"));
                        } else {
                            result.put("result_code", retJsonObj.get("hostRspCode"));
                            result.put("result_desc", retJsonObj.get("hostRspMsg"));
                            result.put("response",retJsonObj.get("response"));
                        }
                    }
                }
            }
        }catch (Exception ex)
        {
            result.put("result_code","20004");
            result.put("result_desc",ex.getMessage());
        }
        return result;
    }

    /**
     * 支付订单查询接口
     * @param projectInfo
     * @return
     */
    public JSONObject icbcTradeQueryFunction(ProjectInfo projectInfo,String orderNo)
    {
        JSONObject result = new JSONObject();
        try{
            String icbc_trade_query_url  = PlatformLists.getPlatformUrl(ConstantData.BUTT_ICBC,ConstantData.ICBC_ORDERQUERY);
            if(!icbc_trade_query_url.equals("")) {
                Map<String,String> urlMap = getUrls(icbc_trade_query_url);
                ICBCOrderQueryNode icbcOrderQueryNode = new ICBCOrderQueryNode();
                icbcOrderQueryNode.setUrl(urlMap.get("url"));
                icbcOrderQueryNode.setApp_id(projectInfo.getIcbcAppId());
                icbcOrderQueryNode.setMsg_id(String.valueOf(SnowflakeConfig.snowflakeId()));
                icbcOrderQueryNode.setFormat("json");
                icbcOrderQueryNode.setCharset("UTF-8");
                icbcOrderQueryNode.setSign_type("RSA2");

                icbcOrderQueryNode.setAppId(projectInfo.getIcbcAppId());
                icbcOrderQueryNode.setOutVendorId(projectInfo.getMerchantId());
                icbcOrderQueryNode.setOutOrderId(orderNo);

                LOGGER.info("SDK-ICBC 订单查询 参数:{}",icbcOrderQueryNode);
                String retJsonStr = icbcpaymentService.orderquery(projectInfo, icbcOrderQueryNode);
                LOGGER.info("SDK-ICBC 订单查询 返回:{}",retJsonStr);
                // 根据返回的json字符串进行结果的分析
                if(retJsonStr.equals("")) {
                    result.put("result_code","20003");
                }
                else{
                    JSONObject retJsonObj = JSON.parseObject(retJsonStr);
                    result.put("result_code", retJsonObj.getString("result_code"));
                    result.put("result_desc", retJsonObj.getString("result_desc"));
                    if (retJsonObj.containsKey("result_code") && retJsonObj.containsKey("result_desc") && "0000".equals(retJsonObj.getString("result_code"))) {
                       JSONObject response = new JSONObject();
                        response.put("serialNo", retJsonObj.getString("serialNo"));
                        response.put("appId", retJsonObj.getString("appId"));
                        response.put("outVendorId", retJsonObj.getString("outVendorId"));
                        response.put("outOrderId", retJsonObj.getString("outOrderId"));
                        response.put("payAmount", retJsonObj.getString("payAmount"));
                        response.put("orderCreateDate", retJsonObj.getString("orderCreateDate"));
                        response.put("orderCreateTime", retJsonObj.getString("orderCreateTime"));
                        response.put("payMethod", retJsonObj.getString("payMethod"));
                        if (retJsonObj.getString("orderStatus").equals("02")) {
                            response.put("tradeStatus", ConstantData.PAY_SUCCESS);
                        } else if (retJsonObj.getString("orderStatus").equals("01")) {
                            response.put("tradeStatus", ConstantData.PAY_NOTPAY);
                        } else {
                            response.put("tradeStatus", retJsonObj.getString("orderStatus"));
                        }
                        response.put("payCompleteDate", retJsonObj.getString("payCompleteDate"));
                        response.put("payCompleteTime", retJsonObj.getString("payComplateTime"));
                        response.put("personalCardNum", retJsonObj.getString("personalCardNum"));
                        response.put("bankDiscount", retJsonObj.getString("bankDiscount"));
                        response.put("vendorDiscount", retJsonObj.getString("vendorDiscount"));
                        response.put("icbcOrderId", retJsonObj.getString("icbcOrderId"));
                        response.put("isJftDiscount", retJsonObj.getString("isJftDiscount"));
                        response.put("jOrderId", retJsonObj.getString("jOrderId"));
                        result.put("response",response);
                    }
                }
            }
        }catch (Exception ex)
        {
            result.put("result_desc",ex.getMessage());
            result.put("result_code",20004);
        }
        return result;
    }

    /**
     * 解析 swift url
     * @param nativeUrl
     * @return
     */
    public static Map<String,String> getUrls(String nativeUrl)
    {
        try
        {
            String Url = "";
            Map<String,String> urlMap = new HashMap<>(1);
            String[] swiftUrl = nativeUrl.split("/");
            urlMap.put("service",swiftUrl[swiftUrl.length -1]);
            for (int i = 0; i< swiftUrl.length - 2 ;i++)
            {
                Url += swiftUrl[i]+"/";
            }
            Url += swiftUrl[swiftUrl.length - 2];
            urlMap.put("url",Url);
            return urlMap;
        }catch (Exception ex)
        {
            return null;
        }
    }
    /**
     * 根据提供的前后时间计算出差值
     * @param timeStart
     * @param timeExpire
     * @return
     */
    public long getTimestamp(String timeStart,String timeExpire) throws Exception {
        try
        {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new SimpleDateFormat("yyyyMMddHHmmss").parse(timeStart));
            long startTime = calendar.getTimeInMillis();

            calendar.setTime(new SimpleDateFormat("yyyyMMddHHmmss").parse(timeExpire));
            long expireTime = calendar.getTimeInMillis();

            if(expireTime <= startTime)
            {
                return -1;
            }
            return expireTime-startTime;
        }catch (Exception ex)
        {
            throw new Exception("获取时间差出错",ex);
        }
    }
    /**
     * 根据起始时间 + 时间间隔 计算出 结束时间
     * @param timeStart
     * @param timeStamp
     * @return
     */
    public String getTimeExpire(String timeStart,long timeStamp)
    {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new SimpleDateFormat("yyyyMMddHHmmss").parse(timeStart));
            long startTime = calendar.getTimeInMillis();

            Date date = new Date(startTime+timeStamp);
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            return format.format(date);
        }catch (Exception ex)
        {
            return "";
        }
    }
    /**
     * Ali:支付授权码，25~30开头的长度为16~24位的数字，实际字符串长度以开发者获取的付款码长度为准
     * Wx: 用户刷卡条形码规则：18位纯数字，以10、11、12、13、14、15开头
     */
    /**
     * 根据 扫码获取 支付宝还是微信
     * @param auto_code
     * @return
     */
    public String getCarPayType(String auto_code)
    {
        if(auto_code.length() < 16 || auto_code.length() > 24) {
            return "付款码长度不正确";
        }
        String  temp = auto_code.substring(0,2);
        if("10".equals(temp) || "11".equals(temp) || "12".equals(temp) || "13".equals(temp) || "14".equals(temp) || "15".equals(temp))
        {
            if (auto_code.length() == 18) {
                return "WXPAY";
            }else {
                return "微信付款码长度不正确";
            }
        }
        if("25".equals(temp) || "26".equals(temp) || "27".equals(temp) || "28".equals(temp) || "29".equals(temp) || "30".equals(temp))
        {
            return "ALIPAY";
        }
        if ("01".equals(temp)) {
           if (auto_code.length() == 19) {
               return "DPAY";
            } else {
               return "数字人民币付款码长度不正确";
           }
        }
        return "非法付款码";
    }
}
