package com.suparking.icbc.datamodule;

import lombok.Data;

@Data
public class OrderInfoNode {
    //项目编号
    private String projectNo;
    //项目订单号

    private String projectOrderNo;
    //订单号
    private String orderNo;
    private String codeUrl;
    private String codeImageUrl;

}
