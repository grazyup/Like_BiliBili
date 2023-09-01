package com.grazy.config;

import org.springframework.context.annotation.Configuration;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: grazy
 * @Date: 2023/8/31 15:40
 * @Description:
 */

/**
 * 跨域解决配置
 *
 * 跨域概念：
 *      出于浏览器的同源策略限制，同源策略会阻止一个域的javascript脚本和另外一个域的内容进行交互。
 *      所谓同源就是指两个页面具有相同的协议（protocol），主机（host）和端口号（port）
 *
 * 非同源的限制：
 *  【1】无法读取非同源网页的 Cookie、LocalStorage 和 IndexedDB
 *  【2】无法接触非同源网页的 DOM
 *  【3】无法向非同源地址发送 AJAX 请求
 *
 *  spingboot解决跨域方案：CORS 是跨域资源分享（Cross-Origin Resource Sharing）的缩写。
 *  它是 W3C 标准，属于跨源 AJAX 请求的根本解决方法。
 *
 *
 *  Filter是用来过滤任务的，既可以被使用在请求资源，也可以是资源响应，或者二者都有
 *  Filter使用doFilter方法进行过滤
 */

@Configuration
public class CorsConfig implements Filter {

    private final String[] allowedDomain = {"http://localhost:8080", "http://39.107.54.180","http://localhost:9090",};


    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        httpResponse.setHeader("Access-Control-Allow-Origin", "*");
//        response.addHeader("Access-Control-Allow-Origin", request.getHeader("origin"));
        // 解决预请求（发送2次请求），此问题也可在 nginx 中作相似设置解决。
        httpResponse.setHeader("Access-Control-Allow-Headers", "x-requested-with,Cache-Control,Pragma,Content-Type,Token, Content-Type");
        httpResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        httpResponse.setHeader("Access-Control-Max-Age", "3600");
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        String method = httpRequest.getMethod();
        if (method.equalsIgnoreCase("OPTIONS")) {
            httpResponse.getOutputStream().write("Success".getBytes("utf-8"));
        } else {
            chain.doFilter(httpRequest, httpResponse);
        }

    }
}
