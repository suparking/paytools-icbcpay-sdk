package com.suparking.icbc.pojo.abcnosense;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AbcNoSenseInfo {
    /** 农行无感支付商户号 */
    private String partnerId;
}
