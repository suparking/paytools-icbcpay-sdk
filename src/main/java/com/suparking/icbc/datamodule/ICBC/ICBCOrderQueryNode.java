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
public class ICBCOrderQueryNode extends ICBCBaseNode{
    private String appId;

    private String outVendorId;

    private String outOrderId;
}
