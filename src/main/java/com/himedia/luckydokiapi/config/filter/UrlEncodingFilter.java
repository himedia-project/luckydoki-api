package com.himedia.luckydokiapi.config.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class UrlEncodingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        chain.doFilter(new RequestWrapper((HttpServletRequest) request), response);
    }

    private static class RequestWrapper extends HttpServletRequestWrapper {
        public RequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getParameter(String name) {
            String value = super.getParameter(name);
            return decodeValue(value);
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            Map<String, String[]> paramMap = super.getParameterMap();
            Map<String, String[]> result = new HashMap<>();
            
            for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
                String[] values = entry.getValue();
                String[] decodedValues = new String[values.length];
                for (int i = 0; i < values.length; i++) {
                    decodedValues[i] = decodeValue(values[i]);
                }
                result.put(entry.getKey(), decodedValues);
            }
            return result;
        }

        @Override
        public String[] getParameterValues(String name) {
            String[] values = super.getParameterValues(name);
            if (values == null) {
                return null;
            }
            
            String[] decodedValues = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                decodedValues[i] = decodeValue(values[i]);
            }
            return decodedValues;
        }

        private String decodeValue(String value) {
            if (value == null) {
                return null;
            }
            try {
                // URL 디코딩을 두 번 수행하여 이중 인코딩된 문자도 처리
                String decoded = URLDecoder.decode(value, StandardCharsets.UTF_8);
                // 이미 디코딩된 문자열인지 확인
                String doubleDecoded = URLDecoder.decode(decoded, StandardCharsets.UTF_8);
                return doubleDecoded.equals(decoded) ? decoded : doubleDecoded;
            } catch (IllegalArgumentException e) {
                // 디코딩 실패 시 원래 값 반환
                return value;
            }
        }
    }
} 