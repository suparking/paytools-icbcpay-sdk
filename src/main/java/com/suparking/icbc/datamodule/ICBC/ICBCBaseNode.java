package com.suparking.icbc.datamodule.ICBC;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ICBCBaseNode {

    private String url;

    private String app_id;

    private String msg_id;

    private String format = "json";

    private String charset = "UTF-8";

    private String encrypt_type = "AES";

    private String sign_type = "RSA2";

    private String sign;

    private String timestamp;

    private String ca;
}
