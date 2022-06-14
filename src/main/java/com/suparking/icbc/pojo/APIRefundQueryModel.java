package com.suparking.icbc.pojo;

import lombok.Data;

@Data
public class APIRefundQueryModel {
    private String projectNo;
    private String orderNo; //退款的订单号
    private String refundNo;
    private String projectRefundNo;
}
