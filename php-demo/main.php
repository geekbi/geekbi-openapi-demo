<?php
// 服务端地址
const BASE_URL = "https://openapi.geekbi.com";
// App 唯一标识
const APP_KEY = "xxxxxx";
// App 秘钥
const APP_SECRET = "xxxxxx";

// 生成签名
function calculateSign($urlPath, $queryParams = null)
{
    $urlBuilder = $urlPath;
    if ($queryParams!== null && count($queryParams) > 0) {
        $urlBuilder.= "?";
        ksort($queryParams);
        foreach ($queryParams as $key => $value) {
            $urlBuilder.= $key. "=". $value. "&";
        }
        $urlBuilder = rtrim($urlBuilder, "&");
    }
    $hmac = hash_hmac('sha256', $urlBuilder, APP_SECRET, true);
    return base64_encode($hmac);
}

// Api 接口路径
$urlPath = "/api/v1/temu/goods/search";

// 查询参数
$queryParams = [
    "keyword" => "dress",
    "page" => "1",
    "size" => "10"
];

// 生成签名
$sign = calculateSign($urlPath, $queryParams);

// 构建请求 URL
$fullUrl = BASE_URL. $urlPath;
if ($queryParams!== null && count($queryParams) > 0) {
    $fullUrl.= "?". http_build_query($queryParams);
}

// 发送请求
$ch = curl_init($fullUrl);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_HTTPHEADER, [
    "appKey: ". APP_KEY,
    "sign: ". $sign
]);
$response = curl_exec($ch);
if ($response === false) {
    echo "Curl error: ". curl_error($ch);
} else {
    echo $response;
}
curl_close($ch);