import base64
import requests
import hmac
import hashlib
from collections import OrderedDict

# 服务端地址
BASE_URL = "https://openapi.geekbi.com"
# App 唯一标识
APP_KEY = "xxxxxx"
# App 秘钥
APP_SECRET = "xxxxxx"

def calculate_sign(url_path, query_params):
    """
    计算签名函数。

    :param url_path: API 接口路径。
    :param query_params: 查询参数的字典。
    :return: 计算得到的签名。
    """
    url_builder = url_path
    if query_params:
        # 对查询参数的键进行排序
        sorted_keys = sorted(query_params.keys())
        query_string = "&".join(f"{key}={query_params[key]}" for key in sorted_keys)
        url_builder += f"?{query_string}"
    # 使用 HMAC-SHA256 算法计算签名
    hmac_obj = hmac.new(APP_SECRET.encode(), url_builder.encode(), hashlib.sha256)
    return base64.b64encode(hmac_obj.digest()).decode()

def main():
    """
    主函数，发送 HTTP 请求并打印结果。
    """
    url_path = "/api/v1/temu/goods/search"
    query_params = OrderedDict([('keyword', 'dress'), ('page', '1'),('size','10')])
    # 计算签名
    sign = calculate_sign(url_path, query_params)
    url = f"{BASE_URL}{url_path}"
    if query_params:
        url += "?" + "&".join(f"{key}={value}" for key, value in query_params.items())
    headers = {'appKey': APP_KEY, 'sign': sign}
    # 发送 GET 请求
    response = requests.get(url, headers=headers)
    print(response.text)

if __name__ == "__main__":
    try:
        main()
    except Exception as e:
        print(f"An error occurred: {e}")