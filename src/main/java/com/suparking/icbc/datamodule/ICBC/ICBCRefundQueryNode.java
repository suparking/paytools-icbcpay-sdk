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
public class ICBCRefundQueryNode extends ICBCBaseNode {
    private String appId;

    private String payType;

    private String isParent;

    private String vendorId;

    private String pRefundId;

    private String refundId;
}
