package com.suparking.icbc.tools;

import com.google.common.collect.Maps;
import com.suparking.icbc.pojo.GenericResponse;

import java.util.Map;

public class ResponseFormat {
    private static Map<Integer,String> messageMap = Maps.newHashMap();

    /**
     * 定义 http返回错误码
     */
    static{
        messageMap.put(200,"执行成功");
        messageMap.put(100,"下单失败,原因见result_desc");
        messageMap.put(1000,"服务器错误");
        messageMap.put(1001,"刷卡请求返回错误");
        messageMap.put(1002,"当前服务器忙");
        messageMap.put(1003,"商户基础配置文件不存在");
        messageMap.put(1004,"未初始化支付库");
        messageMap.put(1005,"支付库初始化失败");
        messageMap.put(1006,"暂不支持此调用方式");
        messageMap.put(1007,"农行无感支付配置信息初始化有误");
        messageMap.put(1008, "农行无感退费不支持查询");
        messageMap.put(10001,"参数无效");
        messageMap.put(10002,"参数为空");
        messageMap.put(10003,"参数类型错误");
        messageMap.put(10004,"参数缺失");
        messageMap.put(10005,"订单号规则有误");
        messageMap.put(10006,"下单金额不是Integer类型");
        messageMap.put(10008,"下单tradeType类型不支持");
        messageMap.put(10009,"WetChat OpenId 有误,或者 APPID 有误");
        messageMap.put(10010,"APP Id 有误");
        messageMap.put(10016,"支付宝服务窗buyer_logon_id,buyer_id信息不能全为空");
        messageMap.put(10017,"银联JS支付Customer_ip信息为空");
        messageMap.put(10011,"当前支付平台不支持此支付方式");
        messageMap.put(10012,"ProductId 有误");
        messageMap.put(10013,"此项目号对应的支付平台不可手动查询");
        messageMap.put(10014,"选择支付平台, ConstantData.BUTT_SWIFT/BUTT_ICBC/BUTT_CCB");
        messageMap.put(10015,"选择下单方式, ConstantData.COMMONTRADETYPE(二维码支付)/WETCHATOFFICAL(公众号支付)/WETCHATMINI(小程序支付)/APPTRADETYPE(APP支付)");
        messageMap.put(10018,"订单号最大长度:32位,时间:14,项目号+终端号 > 18");
        messageMap.put(10019,"提现金额必须大于0");
        messageMap.put(20000,"无权下单");
        messageMap.put(20001,"未对接此支付平台");
        messageMap.put(20002,"未配置下单接口");
        messageMap.put(20003,"远端服务无返回");
        messageMap.put(20004,"系统异常捕获错误");
        messageMap.put(20005,"重复下单");
        messageMap.put(20006,"订单有误");
        messageMap.put(20007,"订单已支付成功");
        messageMap.put(20008,"订单未支付");
        messageMap.put(20009,"退款金额大于支付金额");
        messageMap.put(20010,"申请退款原订单号与系统中原订单号不匹配");
        messageMap.put(20011,"订单已关闭");
        messageMap.put(20012,"正在支付中");
        messageMap.put(20013,"退款成功");
        messageMap.put(20014,"退款失败");
        messageMap.put(20015,"ICBC验签错误");
        messageMap.put(20016,"订单已经申请退款成功");
        messageMap.put(20018,"此订单支付失败,或者被关闭");
        messageMap.put(20019,"存在相同的退单号");
        messageMap.put(20020,"订单不存在");
        messageMap.put(20021,"CCB验签失败");
        messageMap.put(20022,"CCB JSAPI下单失败");
        messageMap.put(20023,"未知支付方式");
    }

    /**
     *
     * @param status
     * @param data
     * @return
     */
    public  static GenericResponse retParam(Integer status,String cmd,Object data){
        GenericResponse json = new GenericResponse(status,messageMap.get(status).length() > 0?messageMap.get(status):"非系统异常",cmd,data);
        return json;
    }
}
