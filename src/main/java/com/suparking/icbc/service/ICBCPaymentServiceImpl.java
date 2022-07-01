package com.suparking.icbc.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.icbc.api.DefaultIcbcClient;
import com.icbc.api.UiIcbcClient;
import com.icbc.api.request.JftApiPayAddTracelessOrderForH5RequestV2;
import com.icbc.api.request.JftApiPayGenPayOrderQrcodeRequestV2;
import com.icbc.api.request.JftApiPayQrcodeRequestV2;
import com.icbc.api.request.QueryOrderRequestV1;
import com.icbc.api.request.RefundAcceptRequestV1;
import com.icbc.api.request.RefundQueryRequestV1;
import com.icbc.api.response.JftApiPayAddTracelessOrderForH5ResponseV2;
import com.icbc.api.response.JftApiPayGenPayOrderQrcodeResponseV2;
import com.icbc.api.response.JftApiPayQrcodeResponseV2;
import com.icbc.api.response.QueryOrderResponseV1;
import com.icbc.api.response.RefundAcceptResponseV1;
import com.icbc.api.response.RefundQueryResponseV1;
import com.suparking.icbc.datamodule.ICBC.ICBCJsOrderNode;
import com.suparking.icbc.datamodule.ICBC.ICBCOrderNode;
import com.suparking.icbc.datamodule.ICBC.ICBCOrderQueryNode;
import com.suparking.icbc.datamodule.ICBC.ICBCPayNode;
import com.suparking.icbc.datamodule.ICBC.ICBCRefundNode;
import com.suparking.icbc.datamodule.ICBC.ICBCRefundQueryNode;
import com.suparking.icbc.datamodule.PlatformLists;
import com.suparking.icbc.datamodule.ProjectInfo;
import com.suparking.icbc.tools.ConstantData;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

public class ICBCPaymentServiceImpl{

    private static String icbcAppid = "12021000000000000128";
    private static String icbcFormat = "json";
    private static String icbcVersion = "1.0";
    private static String icbcSignType = "RSA";
    public static String PFXPATH  = "./abcnosensecert/suparking-cert.pfx";
    public static String KEYSTORE_PASSWORD ="suparking";

    public static RestTemplate restTemplate = new RestTemplate();
    /**
     * 加签名
     * @param dataString
     * @return
     */
    public String signWhithsha1withrsa(String dataString) {
        String signatureString = null;
        try {
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ClassPathResource resource = new ClassPathResource(PFXPATH);
            InputStream inputStream = resource.getInputStream();
            File file = File.createTempFile("test", ".txt");
            try {
                FileUtils.copyInputStreamToFile(inputStream, file);
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
            FileInputStream fis = new FileInputStream(file);
            char[] nPassword = null;
            if ((KEYSTORE_PASSWORD == null) || KEYSTORE_PASSWORD.trim().equals("")) {
                nPassword = null;
            } else {
                nPassword = KEYSTORE_PASSWORD.toCharArray();
            }
            ks.load(fis, nPassword);
            fis.close();
            Enumeration<String> enums = ks.aliases();
            String keyAlias = null;
            if (enums.hasMoreElements()) {
                keyAlias = (String) enums.nextElement();
            }
            PrivateKey prikey = (PrivateKey) ks.getKey(keyAlias, nPassword);
            Certificate cert = ks.getCertificate(keyAlias);
            PublicKey pubkey = cert.getPublicKey();
            // SHA1withRSA算法进行签名
            Signature sign = Signature.getInstance("SHA1withRSA");
            sign.initSign(prikey);
            byte[] data = dataString.getBytes("utf-8");
            sign.update(data);
            byte[] signature = sign.sign();
            signatureString = new String(Base64.encodeBase64(signature));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return signatureString;
    }
    /**
     * 发送请求
     *
     * @param parameterMap 　请求参数
     * @return 返回请求结果
     */
    public String sendRequest4Abc(Map<String, Object> parameterMap) {
        // 请求参数
        String reqJson = com.alibaba.fastjson.JSON.toJSONString(parameterMap);
        // 对需要发送的json字符串进行签名
        String sign = signWhithsha1withrsa(reqJson);
        // 加签名
        String reqStr = sign + reqJson;
        String url = PlatformLists.getPlatformUrl(ConstantData.BUTT_ABC,ConstantData.ABC_NO_SENSE);
        if (StringUtils.isNotEmpty(url)) {
            // 发送签名信息获取返回签名信息
            String result = restTemplate.postForObject(url, reqStr, String.class);
            if (StringUtils.isNotEmpty(result) && result.contains("{")) {
                return result.substring(result.indexOf("{"));
            }
            return result;
        }
        return null;
    }

    /**
     * Native order.
     * @param projectInfo
     * @param obj
     * @return
     * @throws Exception
     */
    public String nativeOrder(ProjectInfo projectInfo,Object obj) throws Exception {
        JSONObject result = new JSONObject();
        try {
            ICBCOrderNode icbcOrderNode = (ICBCOrderNode) obj;
            DefaultIcbcClient client = new DefaultIcbcClient(projectInfo.getIcbcAppId(), icbcOrderNode.getSign_type(),
                    projectInfo.getMuchkey(), icbcOrderNode.getCharset(), icbcOrderNode.getFormat(), projectInfo.getApiGwPublicKey(),
                    icbcOrderNode.getEncrypt_type(), projectInfo.getEncryptKey(), "", "");
            JftApiPayGenPayOrderQrcodeRequestV2 request = new JftApiPayGenPayOrderQrcodeRequestV2();
            request.setServiceUrl(icbcOrderNode.getUrl());
            JftApiPayGenPayOrderQrcodeRequestV2.JftApiPayGenPayOrderQrcodeRequestV2Biz bizContent = new JftApiPayGenPayOrderQrcodeRequestV2.JftApiPayGenPayOrderQrcodeRequestV2Biz();
            bizContent.setAppId(icbcOrderNode.getAppId());
            bizContent.setOutVendorId(icbcOrderNode.getOutVendorId());
            bizContent.setOutUserId(icbcOrderNode.getOutUserId());
            bizContent.setNotifyUrl(icbcOrderNode.getNotifyUrl());
            bizContent.setOutOrderId(icbcOrderNode.getOutOrderId());
            bizContent.setGoodsName(icbcOrderNode.getGoodsName());
            bizContent.setTrxIp(icbcOrderNode.getTrxIp());
            bizContent.setTrxChannel(icbcOrderNode.getTrxChannel());
            DecimalFormat df = new DecimalFormat("#0.00");
            bizContent.setPayAmount(df.format(((float) icbcOrderNode.getPayAmount())/100));
            bizContent.setVarNote(icbcOrderNode.getVarNote());
            bizContent.setPayExpire(icbcOrderNode.getPayExpire());
            bizContent.setPayType(icbcOrderNode.getPayType());
            bizContent.setVersion("1.0.0");
            request.setBizContent(bizContent);

            JftApiPayGenPayOrderQrcodeResponseV2 response;
            response = client.execute(request, icbcOrderNode.getMsg_id());
            if (response.isSuccess()) {
                result.put("result_code", "0000");
                result.put("result_desc", "成功");
                result.put("msg_id", response.getMsgId());
                result.put("qrCode", response.getQrcode());
            } else {
                result.put("result_code", String.valueOf(response.getReturnCode()));
                result.put("result_desc", response.getReturnMsg());
            }
        }catch (Exception ex) {
            throw new Exception("native order,异常",ex);
        }
        return result.toJSONString();
    }

    /**
     * H5 下单接口
     * @param projectInfo {@link ProjectInfo}
     * @param obj
     * @return
     * @throws Exception
     */
    public String order(ProjectInfo projectInfo, Object obj) throws Exception {
        JSONObject result = new JSONObject();
        //Map<String,Object> sendJson = new HashMap<>(1);
        try
        {
            ICBCJsOrderNode icbcJsOrderNode = (ICBCJsOrderNode) obj;
            DefaultIcbcClient client = new DefaultIcbcClient(projectInfo.getIcbcAppId(), icbcJsOrderNode.getSign_type(),
                    projectInfo.getMuchkey(), icbcJsOrderNode.getCharset(), icbcJsOrderNode.getFormat(), projectInfo.getApiGwPublicKey(),
                    icbcJsOrderNode.getEncrypt_type(), projectInfo.getEncryptKey(), "", "");
            JftApiPayAddTracelessOrderForH5RequestV2 request = new JftApiPayAddTracelessOrderForH5RequestV2();
            request.setServiceUrl(icbcJsOrderNode.getUrl());
            JftApiPayAddTracelessOrderForH5RequestV2.JftApiPayAddTracelessOrderForH5V2Biz bizContent = new JftApiPayAddTracelessOrderForH5RequestV2.JftApiPayAddTracelessOrderForH5V2Biz();
            bizContent.setAppId(icbcJsOrderNode.getAppId());
            bizContent.setOutOrderId(icbcJsOrderNode.getOutOrderId());
            bizContent.setOutVendorId(icbcJsOrderNode.getOutVendorId());
            bizContent.setOutUserId(icbcJsOrderNode.getOutUserId());
            DecimalFormat df = new DecimalFormat("#0.00");
            bizContent.setPayAmount(df.format(((float) icbcJsOrderNode.getPayAmount())/100));
            bizContent.setPayType("01");
            bizContent.setPayMode(icbcJsOrderNode.getPayMode());
            bizContent.setNotifyUrl(icbcJsOrderNode.getNotifyUrl());
            bizContent.setGoodsName(icbcJsOrderNode.getGoodsName());
            bizContent.setTrxIp(icbcJsOrderNode.getTrxIp());
            bizContent.setTrxChannel(icbcJsOrderNode.getTrxChannel());
            bizContent.setVarNote(icbcJsOrderNode.getVarVote());
            if (icbcJsOrderNode.getTradeType().equals(ConstantData.WETCHATOFFICAL) || icbcJsOrderNode.getTradeType().equals(ConstantData.WETCHATMINI)) {
                bizContent.setTpAppId(icbcJsOrderNode.getTpAppId());
                bizContent.setTpOpenId(icbcJsOrderNode.getTpOpenId());
            } else if(icbcJsOrderNode.getTradeType().equals(ConstantData.ALIJSPAY)) {
                bizContent.setUnionId(icbcJsOrderNode.getUnionId());
            }
            request.setBizContent(bizContent);

            JftApiPayAddTracelessOrderForH5ResponseV2 response = client.execute(request, icbcJsOrderNode.getMsg_id());
            if (response.getReturnCode() == 10100000) {
                result.put("result_code", "0000");
                result.put("result_desc", "成功");
                JSONObject paySign = JSON.parseObject(response.getPaySign());
                if (icbcJsOrderNode.getTradeType().equals(ConstantData.WETCHATOFFICAL) || icbcJsOrderNode.getTradeType().equals(ConstantData.WETCHATMINI)) {
                    result.put("Prepayid", paySign.getString("package"));
                    result.put("payInfo", response.getPaySign());
                } else if (icbcJsOrderNode.getTradeType().equals(ConstantData.ALIJSPAY)) {
                    JSONObject payInfo = new JSONObject();
                    payInfo.put("tradeNo", paySign.getString("tradeNo"));
                    payInfo.put("outTradeNo", icbcJsOrderNode.getOutOrderId());
                    result.put("payInfo", payInfo);
                }
            } else {
                result.put("result_code", String.valueOf(response.getReturnCode()));
                result.put("result_desc", response.getReturnMsg());
            }

        }catch (Exception ex)
        {
            throw new Exception("order,异常",ex);
        }
        return result.toJSONString();
    }

    /**
     * 工行刷卡支付.
     * @param projectInfo {@link ProjectInfo}
     * @param obj {@link ICBCPayNode}
     * @return pay String
     * @throws Exception
     */
    public String pay(ProjectInfo projectInfo, Object obj) throws Exception {
        JSONObject result = new JSONObject();
        try
        {
            ICBCPayNode icbcPayNode = (ICBCPayNode) obj;
            DefaultIcbcClient client = new DefaultIcbcClient(projectInfo.getIcbcAppId(), icbcPayNode.getSign_type(),
                    projectInfo.getMuchkey(), icbcPayNode.getCharset(), icbcPayNode.getFormat(), projectInfo.getApiGwPublicKey(),
                    icbcPayNode.getEncrypt_type(), projectInfo.getEncryptKey(), "", "");
            JftApiPayQrcodeRequestV2 request = new JftApiPayQrcodeRequestV2();
            request.setServiceUrl(icbcPayNode.getUrl());
            JftApiPayQrcodeRequestV2.JftApiPayQrcodeRequestV2Biz bizContent = new JftApiPayQrcodeRequestV2.JftApiPayQrcodeRequestV2Biz();
            bizContent.setAppId(icbcPayNode.getAppId());
            bizContent.setOutVendorId(icbcPayNode.getOutVendorId());
            bizContent.setOutUserId(icbcPayNode.getOutUserId());
            bizContent.setNotifyUrl(icbcPayNode.getNotifyUrl());
            bizContent.setOutOrderId(icbcPayNode.getOutOrderId());
            bizContent.setGoodsName(icbcPayNode.getGoodsName());
            bizContent.setTrxIp(icbcPayNode.getTrxIp());
            bizContent.setTrxChannel(icbcPayNode.getTrxChannel());
            DecimalFormat df = new DecimalFormat("#0.00");
            bizContent.setPayAmount(df.format(((float)icbcPayNode.getPayAmount())/100));
            bizContent.setQrCode(icbcPayNode.getQrCode());
            bizContent.setPayType(icbcPayNode.getPayType());
            bizContent.setVarNote(icbcPayNode.getVarNote());
            bizContent.setCodeType(icbcPayNode.getCodeType());
            bizContent.setVersion("1.0.0");
            request.setBizContent(bizContent);

            JftApiPayQrcodeResponseV2 response;
            response = client.execute(request, icbcPayNode.getMsg_id());
            if (response.isSuccess()) {
                result.put("result_code", "0000");
                result.put("result_desc", "成功");
                result.put("orderId", response.getOrderId());
                result.put("orderStatus", response.getOrderStatus());
                result.put("payAmount", response.getPayAmount());
                result.put("payTime", response.getPayTime());
            } else {
                result.put("result_code", String.valueOf(response.getReturnCode()));
                result.put("result_desc", response.getReturnMsg());
                result.put("msg_id", response.getMsgId());
            }
        }catch (Exception ex) {
            throw new Exception("pay,异常",ex);
        }
        return result.toJSONString();
    }

    public String orderquery(ProjectInfo projectInfo, Object obj) throws Exception {
        JSONObject result = new JSONObject();
        try
        {
            ICBCOrderQueryNode icbcOrderQueryNode = (ICBCOrderQueryNode) obj;
            DefaultIcbcClient client = new DefaultIcbcClient(projectInfo.getIcbcAppId(), icbcOrderQueryNode.getSign_type(),
                    projectInfo.getMuchkey(), icbcOrderQueryNode.getCharset(), icbcOrderQueryNode.getFormat(), projectInfo.getApiGwPublicKey(),
                    icbcOrderQueryNode.getEncrypt_type(), projectInfo.getEncryptKey(), "", "");
            QueryOrderRequestV1 request = new QueryOrderRequestV1();
            request.setServiceUrl(icbcOrderQueryNode.getUrl());
            QueryOrderRequestV1.QueryOrderRequestV1Biz bizContent = new QueryOrderRequestV1.QueryOrderRequestV1Biz();
            bizContent.setAppId(icbcOrderQueryNode.getAppId());
            bizContent.setOutVendorId(icbcOrderQueryNode.getOutVendorId());
            bizContent.setOutOrderId(icbcOrderQueryNode.getOutOrderId());

            request.setBizContent(bizContent);

            QueryOrderResponseV1 response = client.execute(request, icbcOrderQueryNode.getMsg_id());

            if (response.isSuccess()) {
                result.put("result_code", "0000");
                result.put("result_desc", "成功");
                result.put("serialNo", response.getSerialNo());
                result.put("appId", response.getAppId());
                result.put("outVendorId", response.getOutVendorId());
                result.put("outOrderId", response.getOutOrderId());
                result.put("payAmount", response.getPayAmount());
                result.put("orderCreateDate", response.getOrderCreateDate());
                result.put("orderCreateTime", response.getOrderCreateTime());
                result.put("payMethod", response.getPayMethod());
                result.put("orderStatus", response.getOrderStatus());
                result.put("payCompleteDate", response.getPayCompleteDate());
                result.put("payCompleteTime", response.getPayCompleteTime());
                result.put("personalCardNum", response.getPersonalCardNum());
                result.put("bankDiscount", response.getBankDiscount());
                result.put("vendorDiscount", response.getBankDiscount());
                result.put("icbcOrderId", response.getIcbcOrderId());
                result.put("isJftDiscount", response.getIsJftDiscount());
                result.put("jOrderId", response.getjOrderId());
            } else {
                result.put("result_code", String.valueOf(response.getReturnCode()));
                result.put("result_desc", response.getReturnMsg());
            }

        }catch (Exception ex)
        {
            throw new Exception("orderquery,异常",ex);
        }
        return result.toJSONString();
    }

    public String refund(ProjectInfo projectInfo,Object obj) throws Exception {
        JSONObject result = new JSONObject();
        try
        {
            ICBCRefundNode icbcRefundNode = (ICBCRefundNode) obj;
            DefaultIcbcClient client = new DefaultIcbcClient(projectInfo.getIcbcAppId(), icbcRefundNode.getSign_type(),
                    projectInfo.getMuchkey(), icbcRefundNode.getCharset(), icbcRefundNode.getFormat(), projectInfo.getApiGwPublicKey(),
                    icbcRefundNode.getEncrypt_type(), projectInfo.getEncryptKey(), "", "");
            RefundAcceptRequestV1 request = new RefundAcceptRequestV1();
            request.setServiceUrl(icbcRefundNode.getUrl());
            RefundAcceptRequestV1.RefundAcceptRequestV1Biz bizContent = new RefundAcceptRequestV1.RefundAcceptRequestV1Biz();
            bizContent.setAppId(icbcRefundNode.getAppId());
            bizContent.setVendorId(projectInfo.getMerchantId());
            bizContent.setUserId(icbcRefundNode.getUserId());
            bizContent.setPayType(icbcRefundNode.getPayType());
            bizContent.setOrderId(icbcRefundNode.getOrderId());
            bizContent.setRefundId(icbcRefundNode.getRefundId());
            DecimalFormat df = new DecimalFormat("#0.00");
            bizContent.setRefundAmount(df.format(((float)icbcRefundNode.getRefundAmount())/100));
            bizContent.setNotifyUrl(icbcRefundNode.getNotifyUrl());
            bizContent.setExtension(icbcRefundNode.getExtension());

            request.setBizContent(bizContent);

            RefundAcceptResponseV1 response = client.execute(request, icbcRefundNode.getMsg_id());
            if (response.isSuccess()) {
                result.put("result_code", "0000");
                result.put("result_desc", "成功");
                List<RefundAcceptResponseV1.SubRefund> subReunds = response.getSubRefunds();
                subReunds.forEach(item -> {
                    if(item.getEc().equals("0") && item.getRs().equals("03")) {
                        result.put("result_cdoe", "0000");
                    } else {
                        result.put("result_code", item.getEc() + "/" + item.getRs());
                        result.put("result_desc", item.getEc() + "/" + item.getRs());
                    }
                    result.put("refund_id", item.getEm());
                    result.put("out_trade_no", icbcRefundNode.getOrderId());
                    result.put("out_refund_no", item.getRi());
                    result.put("refund_fee", item.getRra());
                });
            } else {
                result.put("result_code", String.valueOf(response.getReturnCode()));
                result.put("result_desc", response.getReturnMsg());
            }
        }catch (Exception ex)
        {
            throw new Exception("refund,异常",ex);
        }
        return result.toJSONString();
    }

    public String refundquery(ProjectInfo projectInfo,Object obj) throws Exception {
        JSONObject result = new JSONObject();
        try
        {
            ICBCRefundQueryNode icbcRefundQueryNode = (ICBCRefundQueryNode) obj;
            DefaultIcbcClient client = new DefaultIcbcClient(projectInfo.getIcbcAppId(), icbcRefundQueryNode.getSign_type(),
                    projectInfo.getMuchkey(), icbcRefundQueryNode.getCharset(), icbcRefundQueryNode.getFormat(), projectInfo.getApiGwPublicKey(),
                    icbcRefundQueryNode.getEncrypt_type(), projectInfo.getEncryptKey(), "", "");
            RefundQueryRequestV1 request = new RefundQueryRequestV1();
            request.setServiceUrl(icbcRefundQueryNode.getUrl());
            RefundQueryRequestV1.RefundQueryRequestV1Biz bizContent = new RefundQueryRequestV1.RefundQueryRequestV1Biz();
            bizContent.setAppId(projectInfo.getIcbcAppId());
            bizContent.setPayType(icbcRefundQueryNode.getPayType());
            bizContent.setVendorId(icbcRefundQueryNode.getVendorId());
            bizContent.setRefundId(icbcRefundQueryNode.getRefundId());
            bizContent.setIsParent(icbcRefundQueryNode.getIsParent());

            request.setBizContent(bizContent);

            RefundQueryResponseV1 response = client.execute(request, icbcRefundQueryNode.getMsg_id());
            if (response.isSuccess()) {
                result.put("result_code", "0000");
                result.put("result_desc", "成功");
                JSONObject tmp = new JSONObject();
                tmp.put("orderId", response.getOrderId());
                tmp.put("jOrderId", response.getjOrderId());
                tmp.put("refundId", response.getRefundId());
                tmp.put("jRefundId", response.getjRefundId());
                tmp.put("refundStatus", response.getRefundStatus());
                if (response.getRefundStatus().equals("03")) {
                    tmp.put("trade_state", ConstantData.REFUND_SUCCESS);
                } else {
                    tmp.put("trade_state", ConstantData.REFUND_FAILED);
                }
                tmp.put("acceptTime", response.getAcceptTime());
                tmp.put("refundAmount", response.getRefundAmount());
                tmp.put("realRefundAmount", response.getRealRefundAmount());
                tmp.put("completeTime", response.getCompleteTime());
                tmp.put("extension", response.getExtension());
            } else {
                result.put("result_code", String.valueOf(response.getReturnCode()));
                result.put("result_desc", response.getReturnMsg());
            }

        }catch (Exception ex)
        {
            throw new Exception("refundquery,异常",ex);
        }
        return result.toJSONString();
    }
}
