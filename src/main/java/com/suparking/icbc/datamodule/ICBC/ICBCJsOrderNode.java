package com.suparking.icbc.datamodule.ICBC;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ICBCJsOrderNode extends ICBCBaseNode {

    private String appId;

    private String outOrderId;

    private String outVendorId;

    private String outUserId;

    private Integer payAmount;

    private String payType;

    // 01 微信小程序 02 支付宝生活号 03 微信公众号
    private String payMode;

    private String notifyUrl;

    private String goodsName;

    private String mac;

    private String trxIp;

    private String trxIpCity;

    // 03(h5支付) 13(小程序)
    private String trxChannel;

    private String subMerRateWx;

    private String subMerRateZfb;

    private String varVote;

    private String tradeType;

    // 微信公众号/小程序 上送
    private String tpAppId;

    private String tpOpenId;

    // 商户在支付宝生活号,上送唯一标识
    private String unionId;
}
