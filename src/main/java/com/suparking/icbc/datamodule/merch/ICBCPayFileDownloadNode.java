package com.suparking.icbc.datamodule.merch;

import com.suparking.icbc.datamodule.ICBC.ICBCBaseNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ICBCPayFileDownloadNode extends ICBCBaseNode {
    private String appId;

    private String outVendorId;

    private String randomValue;

    private String token;

    private String acDate;
}
