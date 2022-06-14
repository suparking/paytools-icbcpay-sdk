package com.suparking.icbc.datamodule.merch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ICBCPayFileDownloadNode {
    private String appId;

    private String randomValue;

    private String token;

    private String acDate;
}
