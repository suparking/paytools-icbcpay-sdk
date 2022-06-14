package com.suparking.icbc.pojo;

import lombok.Data;

@Data
public class APIWithDrawaModel {
    private String projectNo;
    private String transactionId;
    private int amount;
    private String postscript;
    private String withdrawatime;
    private String increaseValue;
    private char index;
}
