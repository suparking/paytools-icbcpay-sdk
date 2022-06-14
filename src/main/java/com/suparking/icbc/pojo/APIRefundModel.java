package com.suparking.icbc.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class APIRefundModel {
    private String projectNo;
    private String orderNo;
    private String termInfo;
    private String projectOrderNo;
    private String projectRefundNo;
    private Integer refundAmount;
    private Integer totalAmount;
    private String notifyUrl;
    private String operatorId;
    private Date timeCreate;
    // 自增数据
    private String increaseValue;
    // 业务端唯一数据
    private char businessType;
    /** 新增 农行无感 退费 使用的 离场流水号*/
    private String payParkingId;
}
