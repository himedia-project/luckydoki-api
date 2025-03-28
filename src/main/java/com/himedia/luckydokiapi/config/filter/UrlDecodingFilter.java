package com.himedia.luckydokiapi.config.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * URL 디코딩 필터
 */
@Component
public class UrlDecodingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        chain.doFilter(new RequestWrapper((HttpServletRequest) request), response);
    }

    private static class RequestWrapper extends HttpServletRequestWrapper {
        public RequestWrapper(HttpServletRequest request) {
            super(request);
        }

        /**
         * 반환, 디코딩된 매개변수 값을 반환합니다.
         * @param name  매개변수 이름
         *
         * @return  디코딩된 매개변수 값
         */
        @Override
        public String getParameter(String name) {
            String value = super.getParameter(name);
            return decodeValue(value);
        }

        /**
         * 반환, 디코딩된 매개변수 맵을 반환합니다.
         * @return 디코딩된 매개변수 맵
         */
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

        /**
         * 디코딩된 매개변수 값을 반환합니다.
         * @param name 매개변수 이름
         *
         * @return 디코딩된 매개변수 값
         */
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

        /**
         * 디코딩된 매개변수 값을 반환합니다.
         * @param value 디코딩할 값
         * @return 디코딩된 값
         */
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