/* 
 * @(#)HttpUtils.java    Created on 2012-05-31
 * Copyright (c) 2012 Guomi, Inc. All rights reserved.
 */
package com.guomi.meazza.util;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;

/**
 * 提供访问 HTTP 服务的工具类。
 * 
 * @author akuma
 */
public abstract class HttpUtils {

    private static final String DEFAULT_CHARSET = "UTF-8"; // 默认的请求参数编码方式: utf-8
    private static final int DEFAULT_CONNECTION_TIMEOUT = 1000 * 30; // 默认连接超时时间: 30s
    private static final int DEFAULT_READ_TIMEOUT = 1000 * 30; // 默认读取超时时间: 30s

    /**
     * 以 GET 方式请求某个地址，返回值为响应消息体。
     * 
     * @param url
     *            地址
     * @return 响应消息
     * @throws IOException
     *             请求过程中发生异常时抛出
     */
    public static String httpGet(String url) throws IOException {
        return httpGet(url, null);
    }

    /**
     * 以 GET 方式请求某个地址，返回值为响应消息体。默认参数编码为 UTF-8。
     * 
     * @param url
     *            地址
     * @param params
     *            请求参数
     * @return 响应消息
     * @throws IOException
     *             请求过程中发生异常时抛出
     */
    public static String httpGet(String url, Map<String, String> params) throws IOException {
        return httpGet(url, params, DEFAULT_CHARSET);
    }

    /**
     * 以 GET 方式请求某个地址，返回值为响应消息体。
     * 
     * @param url
     *            地址
     * @param params
     *            请求参数
     * @param charset
     *            请求参数编码方式
     * @return 响应消息
     * @throws IOException
     *             请求过程中发生异常时抛出
     */
    public static String httpGet(String url, Map<String, String> params, String charset) throws IOException {
        return httpRequestWithResult(HttpMethod.GET, url, params, charset).getResponseBody();
    }

    /**
     * 以 GET 方式请求某个地址，返回值为响应结果对象。默认参数编码为 UTF-8。
     * 
     * @param url
     *            地址
     * @param params
     *            请求参数
     * @return 响应结果对象
     * @throws IOException
     *             请求过程中发生异常时抛出
     */
    public static ResponseResult httpGetWithResult(String url, Map<String, String> params) throws IOException {
        return httpRequestWithResult(HttpMethod.GET, url, params, DEFAULT_CHARSET);
    }

    /**
     * 以 GET 方式请求某个地址，返回值为响应结果对象。
     * 
     * @param url
     *            地址
     * @param params
     *            请求参数
     * @param charset
     *            请求参数编码方式
     * @return 响应结果对象
     * @throws IOException
     *             请求过程中发生异常时抛出
     */
    public static ResponseResult httpGetWithResult(String url, Map<String, String> params, String charset)
            throws IOException {
        return httpRequestWithResult(HttpMethod.GET, url, params, charset);
    }

    /**
     * 以 POST 方式请求某个地址，返回值为响应消息体。默认参数编码为 UTF-8。
     * 
     * @param url
     *            地址
     * @param params
     *            请求参数
     * @return 响应消息
     * @throws IOException
     *             请求过程中发生异常时抛出
     */
    public static String httpPost(String url, Map<String, String> params) throws IOException {
        return httpPost(url, params, DEFAULT_CHARSET);
    }

    /**
     * 以 POST 方式请求某个地址，返回值为响应消息体。
     * 
     * @param url
     *            地址
     * @param params
     *            请求参数
     * @param charset
     *            请求参数编码方式
     * @return 响应消息
     * @throws IOException
     *             请求过程中发生异常时抛出
     */
    public static String httpPost(String url, Map<String, String> params, String charset) throws IOException {
        return httpPostWithResult(url, params, charset).getResponseBody();
    }

    /**
     * 以 POST 方式请求某个地址，返回值为响应结果对象。默认参数编码为 UTF-8。
     * 
     * @param url
     *            地址
     * @param params
     *            请求参数
     * @return 响应结果对象
     * @throws IOException
     *             请求过程中发生异常时抛出
     */
    public static ResponseResult httpPostWithResult(String url, Map<String, String> params) throws IOException {
        return httpRequestWithResult(HttpMethod.POST, url, params);
    }

    /**
     * 以 POST 方式请求某个地址，返回值为响应结果对象。
     * 
     * @param url
     *            地址
     * @param params
     *            请求参数
     * @param charset
     *            请求参数编码方式
     * @return 响应结果对象
     * @throws IOException
     *             请求过程中发生异常时抛出
     */
    public static ResponseResult httpPostWithResult(String url, Map<String, String> params, String charset)
            throws IOException {
        return httpRequestWithResult(HttpMethod.POST, url, params, charset);
    }

    /**
     * 以指定方式请求某个地址，返回值为响应结果对象。默认参数编码为 UTF-8。
     * 
     * @param url
     *            地址
     * @param params
     *            请求参数
     * @return 响应结果对象
     * @throws IOException
     *             请求过程中发生异常时抛出
     */
    public static ResponseResult httpRequestWithResult(HttpMethod httpMethod, String url, Map<String, String> params)
            throws IOException {
        return httpRequestWithResult(httpMethod, url, params, DEFAULT_CHARSET);
    }

    /**
     * 以指定方式请求某个地址，返回值为响应结果对象。
     * 
     * @param url
     *            地址
     * @param params
     *            请求参数
     * @param charset
     *            请求参数编码方式
     * @return 响应结果对象
     * @throws IOException
     *             请求过程中发生异常时抛出
     */
    public static ResponseResult httpRequestWithResult(HttpMethod httpMethod, String url, Map<String, String> params,
            String charset) throws IOException {
        HttpClient client = new HttpClient();
        client.getHttpConnectionManager().getParams().setConnectionTimeout(DEFAULT_CONNECTION_TIMEOUT); // 设置连接超时时间: 60s
        client.getHttpConnectionManager().getParams().setSoTimeout(DEFAULT_READ_TIMEOUT); // 设置读取超时时间: 60s

        // 以 POST 方式请求
        org.apache.commons.httpclient.HttpMethod method = null;
        final String reqCharSet = StringUtils.isEmpty(charset) ? DEFAULT_CHARSET : charset;
        if (HttpMethod.POST.equals(httpMethod)) {
            method = new PostMethod(url) {
                @Override
                public String getRequestCharSet() {
                    return reqCharSet;
                }
            };
        } else if (HttpMethod.GET.equals(httpMethod)) { // 以 GET 方式请求
            method = new GetMethod(url) {
                @Override
                public String getRequestCharSet() {
                    return reqCharSet;
                }
            };
        } else { // TODO 其他方法暂时不支持
            throw new UnsupportedOperationException();
        }

        if (MapUtils.isNotEmpty(params)) {
            int index = 0;
            NameValuePair[] parametersBody = new NameValuePair[params.size()];
            for (Map.Entry<String, String> entry : params.entrySet()) {
                NameValuePair pair = new NameValuePair();
                pair.setName(entry.getKey());
                pair.setValue(entry.getValue());
                parametersBody[index++] = pair;
            }

            // 设置请求参数，需要对 POST 方式进行特殊处理
            if (method instanceof PostMethod) {
                ((PostMethod) method).setRequestBody(parametersBody);
            } else {
                method.setQueryString(parametersBody);
            }
        }

        ResponseResult responseResult = null;
        try {
            client.executeMethod(method);

            // 获取 HTTP 响应结果并返回
            responseResult = new ResponseResult();
            responseResult.statusCode = method.getStatusCode();
            responseResult.statusText = method.getStatusText();
            responseResult.headers = method.getResponseHeaders();
            responseResult.responseBody = method.getResponseBodyAsString();
            return responseResult;
        } finally {
            if (method != null) {
                method.releaseConnection(); // 关闭连接
            }
        }
    }

    /**
     * HTTP 请求方法的枚举类。
     */
    public static enum HttpMethod {
        GET, POST, HEAD, OPTIONS, PUT, DELETE, TRACE
    }

    /**
     * HTTP 请求返回的响应内容封装类。
     */
    public static class ResponseResult {
        private Header[] headers;
        private String responseBody;
        private int statusCode;
        private String statusText;

        public Header getHeader(String name) {
            if (StringUtils.isEmpty(name) || headers == null) {
                return null;
            }

            Header retVal = null;
            for (Header header : headers) {
                if (header != null && name.equalsIgnoreCase(header.getName())) {
                    retVal = header;
                    break;
                }
            }
            return retVal;
        }

        public Header[] getHeaders() {
            return headers;
        }

        public String getResponseBody() {
            return responseBody;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getStatusText() {
            return statusText;
        }

        public void setHeaders(Header[] headers) {
            this.headers = headers;
        }

        public void setResponseBody(String responseBody) {
            this.responseBody = responseBody;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public void setStatusText(String statusText) {
            this.statusText = statusText;
        }
    }

}
