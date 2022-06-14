package com.suparking.icbc.datamodule.merch;

import lombok.Data;

/**
 * the merch image upload.
 *
 * @author nuo-promise
 */
@Data
public class ICBCImageUploadNode {
    private String url;
    private String service;
    private String subInstId;
    private String imageFileName;
    // 图片文件路径
    private String imageFilePath;
    // 10:营业执照 21:负责人证件1(身份证正面) 22:负责人证件2(身份证反面) 31:账户持有人证件1 32:账户持有人证件2 40:其他证件(如果有多个需要打包成一个压缩文件)
    private String imageType;
    // 影像件上传完毕标志 0 上传未完毕 1 上传完毕
    private String finishFlag;
}
