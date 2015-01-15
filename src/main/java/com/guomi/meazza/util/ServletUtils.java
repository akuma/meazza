/*
 * @(#)ServletUtils.java    Created on 2012-05-31
 * Copyright (c) 2012 Guomi, Inc. All rights reserved.
 */
package com.guomi.meazza.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet 工具类。
 *
 * @author akuma
 */
public abstract class ServletUtils {

    private static final Logger logger = LoggerFactory.getLogger(ServletUtils.class);

    /**
     * HTTP POST 方法。
     */
    public static final String METHOD_POST = "POST";

    /**
     * HTTP GET 方法。
     */
    public static final String METHOD_GET = "GET";

    /**
     * HTTP PUT 方法。
     */
    public static final String METHOD_PUT = "PUT";

    /**
     * HTTP DELETE 方法。
     */
    public static final String METHOD_DELETE = "DELETE";

    /**
     * AJAX 请求头的名称：X-Requested-With
     */
    public static final String AJAX_REQUEST_HEADER = "X-Requested-With";

    /**
     * AJAX 请求头的值：XMLHttpRequest
     */
    public static final String AJAX_REQUEST_HEADER_VALUE = "XMLHttpRequest";

    private static final String DEFAULT_MIME_TYPE = "text/html";
    private static final String MULTIPART = "multipart/form-data";

    private static final String P3P_HEADER = "CP=\"NOI CURa ADMa DEVa TAIa OUR BUS IND UNI COM NAV INT\"";

    private static final String UNKNOWN = "unknown";
    private static final String X_FORWARDED_FOR_HEADER = "x-forwarded-for";
    private static final String X_REAL_IP_HEADER = "x-real-ip";
    private static final String PROXY_CLIENT_IP_HEADER = "Proxy-Client-IP";
    private static final String WL_PROXY_CLIENT_IP_HEADER = "WL-Proxy-Client-IP";

    private static final int ONE_KB = 1024; // 1 KB
    private static final int BUFFER_SIZE = ONE_KB * 4;

    private static final String USER_AGENT = "user-agent";

    private static final Pattern PATTERN_BROWSER_REGEX_MOST = Pattern.compile(
            ".*((msie |firefox/|chrome/|opera/)\\d+(\\.\\d+)*).*", Pattern.CASE_INSENSITIVE);
    private static final Pattern PATTERN_BROWSER_REGEX_SAFARI = Pattern.compile(
            "(?!.*chrome).*(safari/\\d+(\\.\\d+)*).*", Pattern.CASE_INSENSITIVE);

    private static final Pattern PATTERN_IP_ADDRESS_PREFIX = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.");
    private static final String REGEX_IP_ADDRESS = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";

    private static final String MANIFEST_FILE = "/META-INF/MANIFEST.MF";

    private static final int NONE_FLAG = -1;

    private static String charSet = "GBK";

    /**
     * 增加 cookie，cookie 的 path 为"/"。
     *
     * @param response
     *            http 响应
     * @param cookieName
     *            cookie 的名称
     * @param cookieValue
     *            cookie 的值
     * @param maxAge
     *            cookie 的存活期，毫秒为单位
     */
    public static void addCookie(HttpServletResponse response, String cookieName, String cookieValue, int maxAge) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setMaxAge(maxAge);
        cookie.setPath("/");
        addCookie(response, cookie);
    }

    /**
     * 增加 cookie。如果 cookie 的 path 为 null，会被设置为 "/"。
     *
     * @param response
     *            http 响应
     * @param cookie
     *            cookie 对象
     */
    public static void addCookie(HttpServletResponse response, Cookie cookie) {
        if (cookie.getPath() == null) {
            cookie.setPath("/");
        }
        response.addCookie(cookie);
    }

    /**
     * 删除 cookie。
     *
     * @param response
     *            http 响应
     * @param cookieName
     *            cookie 的名称
     */
    public static void removeCookie(HttpServletResponse response, String cookieName) {
        addCookie(response, cookieName, "", 0);
    }

    /**
     * 删除 cookie。
     *
     * @param response
     *            http 响应
     * @param cookie
     *            cookie 对象
     */
    public static void removeCookie(HttpServletResponse response, Cookie cookie) {
        cookie.setMaxAge(0);
        addCookie(response, cookie);
    }

    /**
     * 取得 cookie 的值。
     *
     * @param request
     *            http 请求
     * @param cookieName
     *            cookie 的名称
     * @return cookie 的值
     */
    public static String getCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * 清除 http 缓存。
     *
     * @param response
     *            http 响应
     */
    public static void clearCache(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }

    /**
     * 下载文件。
     *
     * @param file
     *            文件
     * @param request
     *            http 请求
     * @param response
     *            http 响应
     * @throws ServletException
     *             Servlet 异常时抛出
     * @throws IOException
     *             IO 异常时抛出
     */
    public static void download(File file, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        download(file, request, response, null, null);
    }

    /**
     * 下载文件。
     *
     * @param file
     *            文件
     * @param request
     *            http 请求
     * @param response
     *            http 响应
     * @param mimeTypes
     *            mime 类型的映射表
     * @throws ServletException
     *             Servlet 异常时抛出
     * @throws IOException
     *             IO 异常时抛出
     */
    public static void download(File file, HttpServletRequest request, HttpServletResponse response,
            Map<String, String> mimeTypes) throws ServletException, IOException {
        download(file, request, response, mimeTypes, null);
    }

    /**
     * 下载文件。
     *
     * @param file
     *            文件
     * @param request
     *            http 请求
     * @param response
     *            http 响应
     * @param mimeTypes
     *            mime 类型的映射表
     * @param fileName
     *            指定用户浏览器下载的文件名
     * @throws ServletException
     *             Servlet 异常时抛出
     * @throws IOException
     *             IO 异常时抛出
     */
    public static void download(File file, HttpServletRequest request, HttpServletResponse response,
            Map<String, String> mimeTypes, String fileName) throws ServletException, IOException {
        String mimeType = null;

        if (mimeTypes != null) {
            String extension = FilenameUtils.getExtension(file.getName());

            if (extension != null) {
                mimeType = mimeTypes.get(extension);
            }
        }

        if (mimeType == null) {
            mimeType = "application/data";
        }

        response.setContentType(mimeType + "; charset=" + charSet);

        if (fileName != null) {
            fileName = URLEncoder.encode(fileName, charSet);
            fileName = fileName.replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        }

        String range = request.getHeader("Range");

        if (range == null) {
            doDownload(file, request, response);

            if (logger.isDebugEnabled()) {
                logger.debug("doDownload: {}", file.getPath());
            }
            return;
        }

        long startPos = getStartPosition(range);
        if (startPos == NONE_FLAG) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        long endPos = getEndPosition(range, file.length());

        doRangeDownload(file, startPos, endPos, request, response);

        if (logger.isDebugEnabled()) {
            logger.debug("doRangeDownload: file={}, range={}-{}", new Object[] { file.getPath(), startPos, endPos });
        }
    }

    /**
     * 下载文件。
     *
     * @param file
     *            文件
     * @param request
     *            http 请求
     * @param response
     *            http 响应
     * @param fileName
     *            指定用户浏览器下载的文件名
     * @throws ServletException
     *             Servlet 异常时抛出
     * @throws IOException
     *             IO 异常时抛出
     */
    public static void download(File file, HttpServletRequest request, HttpServletResponse response, String fileName)
            throws ServletException, IOException {
        download(file, request, response, null, fileName);
    }

    /**
     * 下载输入流的内容为文件。
     *
     * @param in
     *            输入流
     * @param request
     *            http 请求
     * @param response
     *            http 响应
     * @throws ServletException
     *             Servlet 异常时抛出
     * @throws IOException
     *             IO 异常时抛出
     */
    public static void download(InputStream in, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        download(in, request, response, null);
    }

    /**
     * 下载输入流的内容为文件。
     *
     * @param in
     *            输入流
     * @param request
     *            http 请求
     * @param response
     *            http 响应
     * @param fileName
     *            指定用户浏览器下载的文件名
     * @throws ServletException
     *             Servlet 异常时抛出
     * @throws IOException
     *             IO 异常时抛出
     */
    public static void download(InputStream in, HttpServletRequest request, HttpServletResponse response,
            String fileName) throws ServletException, IOException {
        if (fileName != null) {
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, charSet));
        }

        try (OutputStream out = response.getOutputStream()) {
            int length;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((length = in.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }
            out.flush();
        } catch (IOException e) {
            String ename = e.getClass().getName();
            if (StringUtils.contains(ename, "ClientAbortException")
                    || StringUtils.contains(e.getMessage(), "ClientAbortException")) {
                logger.info("ClientAbortException: name={}, message={}, User-Agent={}", ename, e.getMessage(),
                        ServletUtils.getUserAgent(request));
                return;
            }

            throw e;
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * 获取客户端浏览器的信息，包括名称和版本号，例如：MSIE 8、Firefox/8.0、Chrome/16.0.899.0、Safari/534.30。<br>
     * 目前支持的浏览器为：IE、Firefox、Chrome、Safari、Opera，对于其他类型的浏览器，会返回 “unknown” 字符串。
     *
     * @param request
     *            请求对象
     * @return 客户端浏览器的信息，包括名称和版本号
     */
    public static String getBrowserInfo(HttpServletRequest request) {
        String userAgent = request.getHeader(USER_AGENT);
        return getBrowserInfo(userAgent);
    }

    /**
     * 获取客户端浏览器的信息，包括名称和版本号，例如：MSIE 8、Firefox/8.0、Chrome/16.0.899.0、Safari/534.30。<br>
     * 目前支持的浏览器为：IE、Firefox、Chrome、Safari、Opera，对于其他类型的浏览器，会返回 “unknown” 字符串。
     *
     * @param userAgent
     *            客户端浏览器的 User-Agent 信息
     * @return 客户端浏览器的信息，包括名称和版本号
     */
    public static String getBrowserInfo(String userAgent) {
        if (StringUtils.isBlank(userAgent)) {
            return UNKNOWN;
        }

        String browserName = UNKNOWN;
        Matcher matcher = PATTERN_BROWSER_REGEX_MOST.matcher(userAgent);
        if (matcher.matches()) {
            browserName = matcher.group(1);
        } else {
            matcher = PATTERN_BROWSER_REGEX_SAFARI.matcher(userAgent);
            if (matcher.matches()) {
                browserName = matcher.group(1);
            }
        }
        return browserName;
    }

    /**
     * 在服务器使用反向代理（例如 Nginx、Squid ）的情况下，直接调用 {@link HttpServletRequest#getRemoteAddr()} 方法将无法获取客户端真实的 IP 地址。<br>
     * 在代理服务器配置支持的情况下，使用此方法可以获取到发起原始请求的客户端的真实 IP 地址。
     *
     * @param request
     *            请求对象
     * @return 请求客户端的真实 IP 地址
     */
    public static String getRealRemoteAddr(HttpServletRequest request) {
        String remoteAddr = request.getHeader(X_FORWARDED_FOR_HEADER);

        if (remoteAddr == null || remoteAddr.trim().length() == 0 || UNKNOWN.equalsIgnoreCase(remoteAddr)) {
            remoteAddr = request.getHeader(X_REAL_IP_HEADER);
        }

        if (remoteAddr == null || remoteAddr.trim().length() == 0 || UNKNOWN.equalsIgnoreCase(remoteAddr)) {
            remoteAddr = request.getHeader(PROXY_CLIENT_IP_HEADER);
        }

        if (remoteAddr == null || remoteAddr.trim().length() == 0 || UNKNOWN.equalsIgnoreCase(remoteAddr)) {
            remoteAddr = request.getHeader(WL_PROXY_CLIENT_IP_HEADER);
        }

        if (remoteAddr == null || remoteAddr.trim().length() == 0 || UNKNOWN.equalsIgnoreCase(remoteAddr)) {
            remoteAddr = request.getRemoteAddr();
        }

        if (remoteAddr != null && remoteAddr.trim().length() != 0) {
            String[] remoteAddrs = remoteAddr.split(",");
            remoteAddr = remoteAddrs[0].trim();
        }

        return remoteAddr;
    }

    /**
     * 获取客户端浏览器的 User-Agent 信息，例如 IE8 的 User-Agent 信息：Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0;
     * .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)。
     *
     * @param request
     *            请求对象
     * @return 客户端浏览器的 User-Agent 信息
     */
    public static String getUserAgent(HttpServletRequest request) {
        return request.getHeader(USER_AGENT);
    }

    /**
     * 从程序包的元数据文件 “/META-INF/MANIFEST.MF” 中获取 Web 应用的一些信息数据，包含系统名称、版本号、时间戳等。
     *
     * <p>
     * 返回的 Map 中包含的信息如下：
     *
     * <ul>
     * <li>appName: 应用名称，例如：passport</li>
     * <li>version: 程序包版本号，例如：2.4.2.1</li>
     * <li>qualifier: 程序包限定符，例如：release</li>
     * <li>timestamp: 程序包生成的时间戳，例如：20110415</li>
     * <li>revision: 程序包所对应的源代码版本号，例如：r14897</li>
     * <li>fullVersion: 程序包完整的版本号，例如：2.4.2.1-release-20110415-r14897</li>
     * </ul>
     *
     * 对于取不到的值，会设置为 "unknown"。
     *
     * @param servletContext
     *            servlet 上下文信息
     * @return 包含 Web 应用系统名称、版本号、时间戳、摘要信息、服务器标记等信息的 Map
     */
    public static Map<String, String> getWebappMetaInfo(ServletContext servletContext) {
        String appName = null;
        String version = null;
        String qualifier = null;
        String timestamp = null;
        String revision = null;

        InputStream in = null;
        try {
            in = servletContext.getResourceAsStream(MANIFEST_FILE);
            if (in != null) {
                Manifest manifest = new Manifest(in);
                Attributes atts = manifest.getMainAttributes();

                appName = atts.getValue("Implementation-Title");
                version = atts.getValue("Implementation-Version");
                qualifier = atts.getValue("Implementation-Qualifier");
                timestamp = atts.getValue("Implementation-Timestamp");
                revision = atts.getValue("Implementation-Revision");
            }
        } catch (IOException e) {
            logger.debug("Read webapp's '" + MANIFEST_FILE + "' error", e);
        } finally {
            IOUtils.closeQuietly(in);
        }

        appName = StringUtils.defaultIfEmpty(appName, "unknown");
        version = StringUtils.defaultIfEmpty(version, "unknown");
        qualifier = StringUtils.defaultIfEmpty(qualifier, "unknown");
        timestamp = StringUtils.defaultIfEmpty(timestamp, "unknown");
        revision = StringUtils.defaultIfEmpty(revision, "unknown");

        Map<String, String> webappInfo = new HashMap<String, String>();
        webappInfo.put("appName", appName);
        webappInfo.put("version", version);
        webappInfo.put("qualifier", qualifier);
        webappInfo.put("timestamp", timestamp);
        webappInfo.put("revision", revision);
        String fullVersion = StringUtils.join(new String[] { version, qualifier, timestamp, revision }, "-");
        webappInfo.put("fullVersion", fullVersion);
        return webappInfo;
    }

    /**
     * 获取服务器标识信息，主要用于方便在集群环境下定位应用程序的版本和所在的服务器，例如：passport-2.4.2.1-release-20110415-r14897@cqydweb1_37.98。
     *
     * @param servletContext
     *            servlet 上下文信息
     * @return 服务器标识信息
     */
    public static String getWebappServerMark(ServletContext servletContext) {
        // 获取服务器名称和 IPv4地址的末两位，如果存在多个网卡，则优先选用 eth0 的地址
        String hostname = null;
        String hostAddr = null;
        try {
            InetAddress address = InetAddress.getLocalHost();
            hostname = address.getHostName();

            List<NetworkInterface> nets = Collections.list(NetworkInterface.getNetworkInterfaces());
            Collections.sort(nets, new Comparator<NetworkInterface>() {

                @Override
                public int compare(NetworkInterface o1, NetworkInterface o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
            for (NetworkInterface net : nets) {
                List<InetAddress> inetAddrs = Collections.list(net.getInetAddresses());
                for (InetAddress inetAddr : inetAddrs) {
                    if (inetAddr instanceof Inet4Address) {
                        hostAddr = inetAddr.getHostAddress();
                    }
                }
                if ("eth0".equals(net.getName())) {
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("Get inet address error", e);
        }

        // 设置服务器标识，主要用于方便在集群环境下定位应用程序的版本和所在的服务器
        hostname = StringUtils.defaultIfEmpty(hostname, "unknown_host");
        hostAddr = StringUtils.defaultIfEmpty(hostAddr, "unknown_addr");
        if (hostAddr.length() > 0) {
            // 获取服务器本地 IP 地址的后两位，例：192.168.0.222 -> 0.222
            hostAddr = PATTERN_IP_ADDRESS_PREFIX.matcher(hostAddr).replaceAll("");
        }

        Map<String, String> webappMetaInfo = getWebappMetaInfo(servletContext);
        String appName = webappMetaInfo.get("appName");
        String fullVersion = webappMetaInfo.get("fullVersion");

        StringBuilder serverMark = new StringBuilder();
        serverMark.append(StringUtils.join(new String[] { appName, fullVersion }, "-"));
        serverMark.append("@" + hostname + "_" + hostAddr);
        return serverMark.toString();
    }

    /**
     * 取得网站的跟目录，比如：http://www.foobar.com。
     *
     * @param request
     *            http 请求
     * @return 网站的跟目录
     */
    public static String getWebsiteRoot(HttpServletRequest request) {
        int serverPort = request.getServerPort();
        return request.getScheme()
                + "://"
                + request.getServerName()
                + (serverPort == 80 ? "" : ":" + serverPort)
                + request.getContextPath();
    }

    /**
     * 获取顶级域名。返回值举例：
     * <ul>
     * <li>foo.com => foo.com</li>
     * <li>bar.foo.com => foo.com</li>
     * <li>a.bar.foo.com => foo.com</li>
     * <li>foo => null</li>
     * <li>localhost => null</li>
     * <li>127.0.0.1 => null</li>
     * <li>192.168.1.10 => null</li>
     * </ul>
     */
    public static String getTopDomain(HttpServletRequest request) {
        String domain = request.getServerName();
        if (!domain.contains(".") || domain.matches(REGEX_IP_ADDRESS)) {
            return null;
        }
        return domain.replaceFirst("(.*\\.)?(.+\\..+)", "$2");
    }

    /**
     * 是否是文件上传的 http 请求。
     *
     * @param request
     *            http 请求
     * @return true/false
     */
    public static boolean isMultipart(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.startsWith(MULTIPART);
    }

    /**
     * 是否是 POST 请求。
     *
     * @param request
     *            http 请求
     * @return true/false
     */
    public static boolean isPost(HttpServletRequest request) {
        return METHOD_POST.equals(request.getMethod());
    }

    /**
     * 输出字符串内容到 http 响应中。
     *
     * @param response
     *            http 响应
     * @param value
     *            字符串内容
     * @throws IOException
     *             出现 IO 异常时抛出
     */
    public static void print(HttpServletResponse response, String value) throws IOException {
        print(response, value, DEFAULT_MIME_TYPE);
    }

    /**
     * 输出字符串内容到 http 响应中。
     *
     * @param response
     *            http 响应
     * @param value
     *            字符串内容
     * @param mimeType
     *            MIME 类型
     * @throws IOException
     *             出现 IO 异常时抛出
     */
    public static void print(HttpServletResponse response, String value, String mimeType) throws IOException {
        if (mimeType == null) {
            mimeType = DEFAULT_MIME_TYPE;
        }

        response.setContentType(mimeType + "; charset=" + charSet);
        // response.setContentLength(value.getBytes().length);

        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.print(value);
            out.flush();
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    /**
     * 设置 http 请求的字符集。
     *
     * @param request
     *            http请求
     * @throws ServletException
     *             出现 Servlet 异常时抛出
     * @throws IOException
     *             出现 IO 异常时抛出
     */
    public static void setCharacterEncoding(HttpServletRequest request) throws ServletException, IOException {
        if (request.getCharacterEncoding() == null) {
            request.setCharacterEncoding(charSet);
            logger.debug("request.setCharacterEncoding[{}]", charSet);
        }
    }

    /**
     * 设置字符集。
     *
     * @param charSet
     *            字符集
     */
    public static void setCharSet(String charSet) {
        ServletUtils.charSet = charSet;
    }

    /**
     * 设置http请求的字符集为GBK。
     *
     * @param request
     *            http请求
     * @throws ServletException
     *             出现 Servlet 异常时抛出
     * @throws IOException
     *             出现 IO 异常时抛出
     */
    public static void setGBKEncoding(HttpServletRequest request) throws ServletException, IOException {
        if (request.getCharacterEncoding() == null) {
            request.setCharacterEncoding("GBK");
            logger.debug("request.setCharacterEncoding[GBK]");
        }
    }

    /**
     * 为了跨域设置cookie需要增加P3P的HTTP头。
     *
     * @param response
     *            HTTP 响应对象
     */
    public static void setP3PHeader(HttpServletResponse response) {
        response.setHeader("P3P", P3P_HEADER);
    }

    /**
     * 判断请求是否是 Ajax 请求。
     *
     * @param request
     *            请求对象
     * @return true/false
     */
    public static boolean isAjaxRequest(HttpServletRequest request) {
        String header = request.getHeader(AJAX_REQUEST_HEADER);
        return AJAX_REQUEST_HEADER_VALUE.equalsIgnoreCase(header);
    }

    private static void doDownload(File file, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try (InputStream in = new FileInputStream(file);
             OutputStream out = response.getOutputStream();) {
            response.setContentLength((int) file.length());

            byte[] buffer = new byte[BUFFER_SIZE];
            int length;
            while ((length = in.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }
            out.flush();
        } catch (IOException e) {
            String ename = e.getClass().getName();
            if (StringUtils.contains(ename, "ClientAbortException")
                    || StringUtils.contains(e.getMessage(), "ClientAbortException")) {
                logger.info("ClientAbortException: name={}, message={}, User-Agent={}", ename, e.getMessage(),
                        ServletUtils.getUserAgent(request));
                return;
            }

            throw e;
        }
    }

    private static void doRangeDownload(File file, long startPos, long endPos, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        try (InputStream in = new FileInputStream(file);
             OutputStream out = response.getOutputStream();) {
            if (startPos > 0) {
                in.skip(startPos);
            }

            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            response.setContentLength((int) (endPos - startPos + 1));
            response.setHeader("Content-Range", "bytes " + startPos + '-' + endPos + '/' + file.length());

            byte[] buffer = new byte[BUFFER_SIZE];
            int length;
            while ((length = in.read(buffer)) != -1) {
                out.write(buffer, 0, length);

                startPos = startPos + length;

                if (startPos > endPos) {
                    break;
                }
            }
            out.flush();
        } catch (IOException e) {
            String ename = e.getClass().getName();
            if (StringUtils.contains(ename, "ClientAbortException")
                    || StringUtils.contains(e.getMessage(), "ClientAbortException")) {
                logger.info("ClientAbortException: name={}, message={}, User-Agent={}", ename, e.getMessage(),
                        ServletUtils.getUserAgent(request));
                return;
            }

            throw e;
        }
    }

    private static long getEndPosition(String range, long fileSize) {
        long endPos = string2Long(range.substring(range.indexOf("-") + 1));
        if (endPos == NONE_FLAG || endPos >= fileSize) {
            endPos = fileSize - 1;
        }
        return endPos;
    }

    private static long getStartPosition(String range) {
        return string2Long(range.substring(range.indexOf("=") + 1, range.lastIndexOf("-")));
    }

    private static long string2Long(String stringValue) {
        long numeric = 0;

        if (stringValue.length() == 0) {
            numeric = NONE_FLAG;
        } else {
            try {
                numeric = Long.parseLong(stringValue);
            } catch (NumberFormatException ex) {
                numeric = 0;
            }
        }

        return numeric;
    }

}
