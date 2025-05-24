package com.himedia.luckydokiapi.config.filter;

import com.himedia.luckydokiapi.config.filter.wrapper.XssRequestWrapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;


import java.io.IOException;

// securityConfig 에서 이미 xss 방어 로직이 있기 때문에 쓸모없음!
public class XssFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        XssRequestWrapper wrappedRequest = new XssRequestWrapper((HttpServletRequest) request);
        chain.doFilter(wrappedRequest, response);
    }
}