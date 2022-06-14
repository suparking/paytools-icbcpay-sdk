package com.suparking.icbc.pojo;
import lombok.Data;

@Data
public class APIOrderModel {
    private String projectNo;
    private String termInfo;
    private String subject;
    private String productId;
    private String goodsDesc;
    private String goodsDetail;
    private String goodsTag;
    private String attach;
    private Integer goodsPrice;
    private Integer goodsQuantity;
    private Integer totalAmount;
    private String createIp;
    private String timeStart;
    private String timeExpire;
    private String notifyUrl;
    private String operatorId;
    private String projectOrderNo;
    private String tradetype;
    private String subopenid;
    private String mappid;
    private String appid;
    // 自增数据
    private String increaseValue;
    // 业务端唯一数据
    private char businessType;
    // 支付宝服务窗
    private String buyerId;
    // 银联 js支付
    private String customerIp;
    private String userId;

}
