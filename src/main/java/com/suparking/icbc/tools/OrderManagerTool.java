package com.suparking.icbc.tools;

/**
 * @author Alsa
 * 威富通 与 icbc 使用
 */
public class OrderManagerTool{

    /**
     * 生成订单号,规则如下:
     * [14]年月日时分秒+[6]项目号+[6]终端编号+ [2自增十六进制字符]+[1会员车 临时车统计]+[1 下单类型 P]+[1 微信/支付宝] 由外部 接口区分
     * 项目编号 与 支付终端通过 传参 传进来
     * 2020-04-15 订单组成修改
     *  [17]年月日时分秒毫秒+[6]车场代码(项目编号)+[3]终端代码+[1]递增序号(0-F)+
     *  [1]业务代码0-临停支付,1-会员支付,A-临停退费,B-会员退费+[1]支付渠道代码0-线下,1-swifpass,2-icbc,3-abc,4-union,5-city,6-etc
     *  [1]支付类型代码A-支付宝,W-微信,0-现金,9-其他
     */
    public String getOrderNo(String projectNo, String termNo,String increaseValue,char businessType,char payChannel, char payType) {
        return TimeUtils.getOrderDate() + projectNo + termNo + increaseValue + businessType + payChannel + payType;
    }

    public String getTransactionId(String projectNo,String increaseValue,char index) {
        return TimeUtils.getOrderDate()+projectNo+increaseValue+index;
    }

}
