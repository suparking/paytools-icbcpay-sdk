package com.suparking.icbc.datamodule.ICBC;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ICBCRefundNode extends ICBCBaseNode {
    private String appId;
    private String vendorId;
    private String userId;
    private String payType;
    private String orderId;
    private String refundId;
    private Integer refundAmount;
    private String notifyUrl;
    private String extension;
}
