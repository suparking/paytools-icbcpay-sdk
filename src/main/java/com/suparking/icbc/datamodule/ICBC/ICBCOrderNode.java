package com.suparking.icbc.datamodule.ICBC;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ICBCOrderNode extends ICBCBaseNode {

    private String appId;

    // 收方商户编号
    private String outVendorId;

    // 用户外部编号
    private String outUserId;

    // 支付成功通知
    private String notifyUrl;

    // 商品名称
    private String goodsName;

    // 合作方订单ID
    private String outOrderId;

    //二维码有效期,必须小于30分钟,单位 s
    private String payExpire;

    // 支付提交用户PC的Mac地址
    private String mac;

    // 支付提交用户IP地址
    private String trxIp;

    //交易IP 所在地方
    private String trxIpCity;

    // 交易渠道
    private String trxChannel = "09";

    // 支付金额(元0.02)
    private Integer payAmount;

    private String subMerRateWx;

    private String subMerRateZfb;

    // 支付方式 01 单订单支付
    private String payType;

    private String varNote;

}
