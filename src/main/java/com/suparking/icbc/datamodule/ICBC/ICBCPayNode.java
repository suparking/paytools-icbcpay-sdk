package com.suparking.icbc.datamodule.ICBC;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ICBCPayNode extends ICBCBaseNode {

    private String appId;

    private String outVendorId;

    private String outUserId;

    private String notifyUrl;

    private String outOrderId;

    private String goodsName;

    private String trxIp;

    private String mac;

    private String trxIpCity;

    private String trxChannel =  "08";

    private Integer payAmount;

    private String subMerRateWx;

    private String subMerRateZfb;

    private String qrCode;

    private String payType;

    private String varNote;

    // 01 银行卡,银联,微信,支付宝 02: 数字人民币
    private String codeType;
}
