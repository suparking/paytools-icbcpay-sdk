package com.suparking.icbc.threadtask;

import com.alibaba.fastjson.JSONObject;
import com.suparking.icbc.datamodule.ProjectInfo;
import com.suparking.icbc.service.ICBCServiceFunction;
import com.suparking.icbc.tools.ConstantData;
import com.suparking.icbc.tools.TimeUtils;
import lombok.Data;

/**
 * 此线程池 作用就是 用户下单之后 当进行扫码支付时候 支付中心根据订单号进行查询 状态,如果订单支付成功 那么 更新数据库
 * 查询订单号 状态 和 异步通知 两个方式 都同时进行,双方只要有一种 查询到 该订单支付成功,那么 就结束所有查询动作
 * 支付[扫码支付与刷卡支付]状态查询存在时效性 将带上其查询的订单号+订单过期时间,在查询的时候先匹配下时间 是否在范围内,如果没有在范围内,那么就不需要查询
 * 直接更新数据库
 * 退款查询
 * 所有 状态 都将通过修改数据库状态
 */
@Data
public class ThreadTask implements Runnable{

    private static Integer threadTimestamp=1000;

    private static ICBCServiceFunction icbcServiceFunction = new ICBCServiceFunction();

    // 线程中使用的变量
    private String orderNo;
    private String timeExpire;
    private String projectOrderNo;
    private String notifyUrl;
    private String globalNo;
    private ProjectInfo projectInfo;
    public ThreadTask(String orderNo,
                      String timeExpire,
                      String projectOrderNo,
                      String notifyUrl,
                      String globalNo,
                      ProjectInfo projectInfo)
    {
        this.orderNo = orderNo;
        this.timeExpire = timeExpire;
        this.projectOrderNo = projectOrderNo;
        this.notifyUrl = notifyUrl;
        this.globalNo = globalNo;
        this.projectOrderNo = projectOrderNo;
        this.projectInfo = projectInfo;
    }
    @Override
    public void run()
    {
        String trade_status = "";
        boolean queryflag = false;
        while (!queryflag)
        {
            JSONObject ret = new JSONObject();
            ret.put("msg","订单号: ["+this.orderNo+"] 正在订单查询");
            try {
                icbcServiceFunction.Notify(notifyUrl,ret.toJSONString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (TimeUtils.getQueryCodeDate().compareTo(timeExpire) <=0)
            {
                JSONObject jsonObject = icbcServiceFunction.icbcTradeQueryFunction(projectInfo,orderNo);
                ret.put("msg","订单号: ["+this.orderNo+"] 查询返回 => "+jsonObject.toJSONString());
                try {
                    icbcServiceFunction.Notify(notifyUrl,ret.toJSONString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (jsonObject.containsKey("result_code") &&
                        jsonObject.containsKey("result_desc") &&
                        jsonObject.getString("result_code").contains("0000") &&
                        jsonObject.getString("result_desc").contains("成功"))
                {
                    // 订单查询返回,根据返回的状态类型,进行提示
                    trade_status = "";
                    trade_status = ConstantData.PAY_SUCCESS;
                }else
                {
                    trade_status = "";
                    trade_status = (String) jsonObject.getJSONObject("response").get("tradeStatus");
                }
                if (ConstantData.PAY_SUCCESS.equals(trade_status))
                {
                    queryflag = true;
                    if (notifyUrl != null && !notifyUrl.equals(""))
                    {
                        /** TODO 如果异步回掉地址正确 ,那么发送通知*/
                        JSONObject notifyJson = new JSONObject();
                        notifyJson.put("status",200);
                        notifyJson.put("message","执行成功");
                        JSONObject tmpJson = new JSONObject();
                        tmpJson.put("order_no",orderNo);
                        tmpJson.put("project_order_no",projectOrderNo);
                        tmpJson.put("trade_time",TimeUtils.getQueryCodeDate());
                        tmpJson.put("result_code","0");
                        tmpJson.put("result_desc","支付成功");
                        notifyJson.put("result",tmpJson);
                        try {
                            icbcServiceFunction.Notify(notifyUrl,notifyJson.toJSONString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }else if (ConstantData.PAY_UNDEFINED.equals(trade_status))
                {
                    queryflag = true;
                    break;
                }
            }else
            {
                if (!queryflag)
                {
                    queryflag = true;
                    break;
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
        }
    }
}
