package com.suparking.icbc.pojo;

import lombok.Data;

@Data
public class APIPayModel {
    private String projectNo;
    private String termInfo;
    private String subject;
    private String productId;
    private String goodsDesc;
    private String goodsDetail;
    private String goodsTag;
    private String attach;
    private String authCode;
    private Integer goodsPrice;
    private Integer goodsQuantity;
    private Integer totalAmount;
    private String createIp;
    private String timeStart;
    private String timeExpire;
    private String notifyUrl;
    private String operatorId;
    private String projectOrderNo;
    // 自增数据
    private String increaseValue;
    // 业务端唯一数据
    private char businessType;
}
