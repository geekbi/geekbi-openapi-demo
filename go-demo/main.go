package main

import (
	"crypto/hmac"
	"crypto/sha256"
	"encoding/base64"
	"fmt"
	"io"
	"net/http"
	"sort"
	"time"
)

const (
	BASE_URL   = "https://openapi.geekbi.com"
	APP_KEY    = "xxxxxx"
	APP_SECRET = "xxxxxx"
)

// calculateSign 生成签名
func calculateSign(urlPath string, queryParams map[string]string) string {
	urlBuilder := urlPath
	if queryParams != nil && len(queryParams) > 0 {
		urlBuilder += "?"
		keys := make([]string, 0, len(queryParams))
		for key := range queryParams {
			keys = append(keys, key)
		}
		sort.Strings(keys)
		for _, key := range keys {
			urlBuilder += key + "=" + queryParams[key] + "&"
		}
		urlBuilder = urlBuilder[:len(urlBuilder)-1]
	}
	h := hmac.New(sha256.New, []byte(APP_SECRET))
	h.Write([]byte(urlBuilder))
	return base64.StdEncoding.EncodeToString(h.Sum(nil))
}

func main() {
	urlPath := "/api/v1/temu/goods/search"
	queryParams := map[string]string{
		"keyword":   "dress",
		"page":      "1",
		"size":      "10",
		"timestamp": fmt.Sprintf("%d", time.Now().Unix()),
	}
	sign := calculateSign(urlPath, queryParams)
	url := BASE_URL + urlPath
	if len(queryParams) > 0 {
		queryStr := ""
		keys := make([]string, 0, len(queryParams))
		for key := range queryParams {
			keys = append(keys, key)
		}
		sort.Strings(keys)
		for _, key := range keys {
			queryStr += key + "=" + queryParams[key] + "&"
		}
		queryStr = queryStr[:len(queryStr)-1]
		url += "?" + queryStr
	}
	req, err := http.NewRequest("GET", url, nil)
	if err != nil {
		fmt.Println(err)
		return
	}
	req.Header.Add("appKey", APP_KEY)
	req.Header.Add("sign", sign)
	client := &http.Client{}
	resp, err := client.Do(req)
	if err != nil {
		fmt.Println(err)
		return
	}
	defer resp.Body.Close()
	// 打印结果
	bodyBytes, err := io.ReadAll(resp.Body)
	if err != nil {
		fmt.Println(err)
		return
	}
	fmt.Println(string(bodyBytes))
}
