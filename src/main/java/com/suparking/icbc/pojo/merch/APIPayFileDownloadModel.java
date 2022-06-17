package com.suparking.icbc.pojo.merch;

import com.suparking.icbc.datamodule.ICBC.ICBCBaseNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * .
 *
 * @author nuo-promise
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class APIPayFileDownloadModel extends ICBCBaseNode {
    private String projectNo;
    private String subInstId;
    private String randomValue;
    private String token;
    private String acDate;
}
