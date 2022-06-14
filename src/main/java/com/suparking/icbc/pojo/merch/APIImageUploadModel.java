package com.suparking.icbc.pojo.merch;

import lombok.Data;

/**
 * .
 *
 * @author nuo-promise
 */
@Data
public class APIImageUploadModel {
    private String projectNo;
    private String subInstId;
    private String imageFileName;
    private String imageFilePath;
    private String imageType;
    private String finishFlag;
}
