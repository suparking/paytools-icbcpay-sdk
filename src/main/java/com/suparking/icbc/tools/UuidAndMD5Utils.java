package com.suparking.icbc.tools;

import sun.misc.BASE64Encoder;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * @author Alsa
 */
public class UuidAndMD5Utils {

//    private static String muchKey = "a75792ea49ef776b978b33c2bfaec568";
//    private static String muchKeyTwo = "61ed5bc967df05e265d173a983b2c5f8";
//
//   private static String icbcPrivateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBALRYITNavFRgdS8NkfVSn1D7XBJhroNoj683exjXL+NVuDOq9w9osKDEzC73VAbq+BT7las6yhFdwvAe1llL+fIPpHjZm0iJofBw6A0L3YMAw/aQWZK371mAOrc9YoXXHIrCrYFzkicdQslA4O19uhDHSqqIoSnPCtu+rJkrNucXAgMBAAECgYBUDbNDrSmTpfVX/domXeDVLKIoCxD8PDKjtpcP59NxAFW+9xL5QpD4DWEhDrNCGieQGAYU7WyalXmy0pySt/+A0xCb/DmiAf08aB3mlfBmf6MXvvaX16DQw0kUQRlmhsCnhdUBetUKC9o/FLydZMI31qcHF5ctRn/2LDqADP4CUQJBANfN8VpEtSIxMU3oczncrBB75IXSV+4fqb47DEyTR+pf+pG7jg3J8vklubWaLGwjypwQlr/xv3UsIhowlQw4AZkCQQDV71yr51Cm5l8pe5n0rQiWeSwHxsKEnJEspjQe4ho6fH747hFWDgcG0bcj/ZPxHRQm0/KW9UGN/5LHInH3vPwvAkAdI3iQOLDAciX3IAjW6j3tZ90eWJ140JupzO1HNafDNLxviwSORhNhor9ljvCqlVaZJgBE4I56csAUCzVE1hcZAkBVIyHNI4v8L5fVaWP2dFoDkAtOuPG2VpyLmUUKuU5Y0iyxogRyK7juJM1uXD7g6IKZhSV4n/fLq9bcur+CKMpvAkEA0zlsQOZrJscm4wVFM8nqwcHG5xg99gO59ywKkdIirs6wBVQmVJy6ugzZ+DksuIMjCxj+wmSFtSw4UGcBICeGNw==";
//
//    private static String icbcPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC0WCEzWrxUYHUvDZH1Up9Q+1wSYa6DaI+vN3sY1y/jVbgzqvcPaLCgxMwu91QG6vgU+5WrOsoRXcLwHtZZS/nyD6R42ZtIiaHwcOgNC92DAMP2kFmSt+9ZgDq3PWKF1xyKwq2Bc5InHULJQODtfboQx0qqiKEpzwrbvqyZKzbnFwIDAQAB";
//    //private static String muchKey = "a75792ea49ef776b978b33c2bfaec569";

    private static MessageDigest md5 = null;

    static{
        try{
            md5 = MessageDigest.getInstance("MD5");

        } catch (NoSuchAlgorithmException e) {
        }
    }

    /**
     *
     * @param params
     * @return
     */
    public static boolean checkParam(Map<String,String> params,String muchKey)
    {
        boolean result = false;
        if(params.containsKey("sign"))
        {
            String sign = params.get("sign");
            params.remove("sign");
            String tempString = getResSign(params,muchKey);
            if(tempString.equals(sign)) {
                result = true;
            }
        }
        return result;
    }
    /**
     * ??????Uuid
     * @return
     */
    public static String getUuid()
    {

        return UUID.randomUUID().toString().replaceAll("-","").toUpperCase();
    }

    /**
     *
     * @param inStr
     * @return
     */
    public static String string2MD5(String inStr){
        MessageDigest md5 = null;
        try{
            md5 = MessageDigest.getInstance("MD5");
        }catch (Exception e){
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++){
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();

    }
    /**
     * ?????? MD5
     * @param param
     * @return
     */
    public static String getMd5(String param)
    {
        byte[] bs = md5.digest(param.getBytes());
        StringBuilder sb = new StringBuilder(40);
        for(byte x:bs) {
            if((x & 0xff)>>4 == 0) {
                sb.append("0").append(Integer.toHexString(x & 0xff));
            } else {
                sb.append(Integer.toHexString(x & 0xff));
            }
        }
        return sb.toString();
    }

    /**
     * ????????????
     * @param param
     * @param signature
     * @return
     */
    public static boolean verityWhole(String param,String signature,String muchKey)
    {
        //????????????
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
            byte[] keyByte = Base64.getDecoder().decode(muchKey);
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyByte);
            Security.addProvider(
                    new org.bouncycastle.jce.provider.BouncyCastleProvider()
            );
            PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
            //????????????????????????,????????????????????????param ????????????????????????????????????signature ????????????
            Signature sign = Signature.getInstance("SHA1WithRSA");
            sign.initVerify(publicKey);
            sign.update(param.getBytes());
            //???16???????????????????????????
            return sign.verify(signature.getBytes());
        } catch (NoSuchAlgorithmException e) {
            new Exception(e.getCause());
        } catch (SignatureException e) {
        } catch (InvalidKeyException e) {
        } catch (InvalidKeySpecException e) {
        }
        return false;
    }

    /**
     * ?????????
     * @param param
     * @return
     */
    public static String signWhole(String param,String muchKey)
    {
        byte[] signature = null;
        try {
            //??????privatekey
            byte[] keyByte = Base64.getDecoder().decode(muchKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyByte);
            Security.addProvider(
                    new org.bouncycastle.jce.provider.BouncyCastleProvider()
            );
            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);

            //????????????????????????
            Signature sign = Signature.getInstance("SHA1WithRSA");
            sign.initSign(privateKey);
            sign.update(param.getBytes());
            signature = sign.sign();

            //????????? ???BASE64
            BASE64Encoder encoder = new BASE64Encoder();
            return encoder.encode(signature);
        } catch (NoSuchAlgorithmException e) {
        } catch (InvalidKeySpecException e) {
        } catch (InvalidKeyException e) {
        } catch (SignatureException e) {
        }
        return null;
    }

    /**
     * ??????????????????????????????
     * @param map
     * @return
     */
    public static boolean icbcVerityGwSign(Map<String,Object> map,String signature,String muchKey)
    {
        String result = "";
        try
        {
            List<Map.Entry<String, Object>> infoIds = new ArrayList<Map.Entry<String, Object>>(map.entrySet());
            //?????? ?????????  ???????????? ??????
            Collections.sort(infoIds, new Comparator<Map.Entry<String, Object>>() {
                @Override
                public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2) {
                    return (o1.getKey()).toString().compareTo(o2.getKey());
                }
            });
            //?????????????????? ?????? ???????????????
            StringBuilder icbcSb = new StringBuilder();
            for(Map.Entry<String,Object> info: infoIds)
            {
                String keys = info.getKey();
                Object vals = info.getValue();
                if(!(vals== "" || vals == null)){
                    icbcSb.append(keys+"="+vals+"&");
                }
            }
            //??????????????????  &
            result = icbcSb.toString();
            result = result.substring(0,result.length()-1);
            return verityWhole(result,signature,muchKey);
        }catch (Exception ex)
        {
        }
        return false;
    }

    /**
     * CCB  MD5
     * @param map
     * @return
     */
    public static String ccbGetMd5(Map<String,Object> map)
    {
        String result="";
        try
        {
            List<Map.Entry<String,Object>> infoIds = new ArrayList<>(map.entrySet());
            StringBuilder ccbSb = new StringBuilder();
            for(Map.Entry<String,Object> info: infoIds)
            {
                String keys = info.getKey();
                Object vals = info.getValue();
                ccbSb.append(keys+"="+vals+"&");

            }
            // ?????? ???????????? &
            result = ccbSb.toString();
            result = result.substring(0,result.length()-1);
            String md5str = Md5Util.encryption(result).toLowerCase();
            return md5str;
        }catch ( Exception ex)
        {
        }
        return null;
    }
    /**
     * ?????? icbc ??????,???????????? ??????????????? ??????
     * @param map
     * @return
     */
    public static String icbcGetApiGwSign(Map<String,Object> map,String muchKey)
    {
        String result = "";
        try
        {
            List<Map.Entry<String, Object>> infoIds = new ArrayList<Map.Entry<String, Object>>(map.entrySet());
            //?????? ?????????  ???????????? ??????
            Collections.sort(infoIds, new Comparator<Map.Entry<String, Object>>() {
                @Override
                public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2) {
                    return (o1.getKey()).toString().compareTo(o2.getKey());
                }
            });
            //?????????????????? ?????? ???????????????
            StringBuilder icbcSb = new StringBuilder();
            for(Map.Entry<String,Object> info: infoIds)
            {
                String keys = info.getKey();
                Object vals = info.getValue();
                if(!(vals== "" || vals == null)){
                    icbcSb.append(keys+"="+vals+"&");
                }
            }
            //??????????????????  &
            result = icbcSb.toString();
            result = result.substring(0,result.length()-1);
            return signWhole(result,muchKey);
        }catch (Exception ex)
        {
            return null;
        }
    }

    /**
     * ????????????????????????????????????
     * @param map
     * @return
     */
    public static String getResSign(Map<String,String> map,String muchKey){
        String result = "";
        try{
            List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(map.entrySet());
            //?????? ascill ???????????? ??????
            Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
                @Override
                public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                    return (o1.getKey()).toString().compareTo(o2.getKey());
                }
            });
            //?????????????????? ?????? ???????????????
            StringBuilder sb = new StringBuilder();
            for(Map.Entry<String,String> item: infoIds)
            {
                String key = item.getKey();
                Object val = item.getValue();
                if(!(val== "" || val == null)){
                    sb.append(key+"="+val+"&");
                }
            }
            sb.append("key="+muchKey);
            result = sb.toString();
            //????????????????????????
            //result = result.substring(0,result.length()-1);
            return getMd5(result);

        }catch (Exception ex)
        {
            return null;
        }
    }
    /**
     * ????????????????????????????????????
     * @param map
     * @return
     */
    public static String getSign(Map<String,Object> map,String muchKey){
        String result = "";
        try{
            List<Map.Entry<String, Object>> infoIds = new ArrayList<Map.Entry<String, Object>>(map.entrySet());
            //?????? ascill ???????????? ??????
            Collections.sort(infoIds, new Comparator<Map.Entry<String, Object>>() {
                @Override
                public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2) {
                    return (o1.getKey()).toString().compareTo(o2.getKey());
                }
            });
            //?????????????????? ?????? ???????????????
            StringBuilder sb = new StringBuilder();
            for(Map.Entry<String,Object> item: infoIds)
            {
                String key = item.getKey();
                Object val = item.getValue();
                if(!(val== "" || val == null)){
                    sb.append(key+"="+val+"&");
                }
            }
            sb.append("key="+muchKey);
            result = sb.toString();
            //????????????????????????
            //result = result.substring(0,result.length()-1);
            return getMd5(result);

        }catch (Exception ex)
        {
            return null;
        }
    }

    /**
     * ??????????????????
     * @param map
     * @return
     */
    public static String getReqStr(Map<String,Object> map)
    {
        StringBuilder stringBuilder = new StringBuilder();
        try
        {
            Iterator<Map.Entry<String,Object>> entryIterator = map.entrySet().iterator();
            while (entryIterator.hasNext())
            {
                Map.Entry<String,Object> entry = entryIterator.next();
                if (!(entry.getValue() == "" || entry.getValue() == null))
                {
                    stringBuilder.append(entry.getKey()+"="+entry.getValue()+"&");
                }
            }

        }catch (Exception ex)
        {
            return null;
        }
        return stringBuilder.toString().substring(0,stringBuilder.toString().length()-1);
    }
    /**
     * ???????????? ?????? bit??? ????????????????????????
     * @param bit
     * @return
     */
    public static String RandomHexString(int bit)
    {
        try{
            StringBuffer randomStr = new StringBuffer();
            for(int i = 0; i<bit;i++)
            {
                randomStr.append(Integer.toHexString(new Random().nextInt(16)));
            }
            return randomStr.toString().toUpperCase();
        }catch (Exception ex)
        {
        }
        return null;
    }


    public static void main(String[] args) {
//        System.out.println(getUuid());
//        System.out.println(getMd5("2323"));
        System.out.println(RandomHexString(2));
    }

}
