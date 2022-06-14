package com.suparking.icbc.tools;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpUtils {

    public static final Logger LOGGER = LoggerFactory.getLogger(HttpUtils.class);

    private static Integer connTimeout=1000;
    private static Integer readTimeout=1000;
    /**
     * 获取流传输对象
     * @param request
     * @return
     */
    public static Object loadRequestInfo(HttpServletRequest request)
    {
        Object obj = new Object();
        StringBuilder stringBuilder = new StringBuilder();
        try
        {
            InputStream inputStream = request.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
            String line = null;
            while((line=bufferedReader.readLine())!=null) {
                stringBuilder.append(line);
            }
            obj = stringBuilder;
        }catch (Exception e)
        {
        }
        return obj;
    }

    /**
     * 向指定URL 发送POST方法的请求
     * @param url
     * @param param
     * @return
     */
    public static String sendPost(String url,String param) throws Exception {
        HttpResponse httpResponse = null;
        JSONObject retJsonObj = new JSONObject();
        try
        {
            httpResponse = HttpRequest.post(url).body(param).timeout(15000).execute();
            if (httpResponse != null && httpResponse.isOk())
            {
                String result = httpResponse.body();
                JSONObject jsonObject =null;
                jsonObject = JSON.parseObject(result);
                return jsonObject.toJSONString();
            }else
            {
                retJsonObj.put("result_code",httpResponse.getStatus());
                retJsonObj.put("result_desc",httpResponse.body());
            }
        }catch (Exception ex)
        {
            throw new Exception("sendPost,异常",ex);
        }
        return retJsonObj.toJSONString();
    }
}
