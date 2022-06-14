package com.suparking.icbc.datamodule.ICBC;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ICBCTokenNode extends ICBCBaseNode {
    private String appId;

    private String validTime;

    private String version;
}
