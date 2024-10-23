const axios = require('axios');
const crypto = require('crypto');

const BASE_URL = 'https://openapi.geekbi.com';
const APP_KEY = 'xxxxxx';
const APP_SECRET = 'xxxxxx';

function calculateSign(urlPath, queryParams = {}) {
    let urlBuilder = urlPath;
    if (Object.keys(queryParams).length > 0) {
        urlBuilder += '?';
        const sortedKeys = Object.keys(queryParams).sort();
        for (const key of sortedKeys) {
            urlBuilder += `${key}=${queryParams[key]}&`;
        }
        urlBuilder = urlBuilder.slice(0, -1);
    }
    const hmac = crypto.createHmac('sha256', APP_SECRET);
    hmac.update(urlBuilder);
    return hmac.digest('base64');
}

async function main() {
    const urlPath = '/api/v1/temu/goods/search';
    const queryParams = {
        keyword: 'dress',
        page: '1',
        size: '10',
        timestamp: Math.floor(Date.now() / 1000),
    };
    const sign = calculateSign(urlPath, queryParams);
    try {
        const response = await axios.get(urlPath, {
            baseURL: BASE_URL,
            params: queryParams,
            headers: {
                appKey: APP_KEY,
                sign: sign,
            },
        });
        console.log(response.data);
    } catch (error) {
        console.error(error);
    }
}

main();