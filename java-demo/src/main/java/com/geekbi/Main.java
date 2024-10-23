package com.geekbi;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Main {
    // 服务端地址
    private static final String BASE_URL = "https://openapi.geekbi.com";
    // App唯一标识
    private static final String APP_KEY = "xxxxxx";
    // App秘钥
    private static final String APP_SECRET = "xxxxxx";

    // 生成签名
    private static String calculateSign(String urlPath, Map<String, String> queryParams) throws NoSuchAlgorithmException, InvalidKeyException {
        StringBuilder urlBuilder = new StringBuilder(urlPath);
        if (queryParams!= null &&!queryParams.isEmpty()) {
            urlBuilder.append("?");
            List<String> sortedKeys = new ArrayList<>(queryParams.keySet());
            sortedKeys.sort(String::compareTo);
            for (String key : sortedKeys) {
                urlBuilder.append(key).append("=").append(queryParams.get(key)).append("&");
            }
            urlBuilder.deleteCharAt(urlBuilder.length() - 1);
        }
        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(APP_SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256Hmac.init(secretKey);
        byte[] hmacBytes = sha256Hmac.doFinal(urlBuilder.toString().getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hmacBytes);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        // Api接口路径
        String urlPath = "/api/v1/temu/goods/search";

        // 查询参数
        Map<String, String> queryParams = new TreeMap<>();
        queryParams.put("keyword", "dress");
        queryParams.put("page", "1");
        queryParams.put("size", "10");

        // 生成签名
        String sign = calculateSign(urlPath, queryParams);

        // 构建请求URL
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + urlPath).newBuilder();
        queryParams.forEach(urlBuilder::addQueryParameter);

        // 发送请求
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urlBuilder.toString())
                .addHeader("appKey", APP_KEY)
                .addHeader("sign", sign)
                .build();
        Response response = client.newCall(request).execute();

        // 打印结果
        System.out.println(response.body().string());
    }


}