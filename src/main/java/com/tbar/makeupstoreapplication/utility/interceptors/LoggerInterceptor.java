package com.tbar.makeupstoreapplication.utility.interceptors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("NullableProblems")
@Slf4j
public class LoggerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.debug("[preHandle] [{}] [{}{}]", request.getMethod(), request.getRequestURL(),
                getQueryStringIfNotEmptyFrom(request));
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if (modelAndView != null) {
            log.debug("[postHandle] [model = {}] [view = {}]", modelAndView.getModel(), modelAndView.getViewName());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (ex != null) {
            ex.printStackTrace();
        }
        log.debug("[afterCompletion] [response status = {}] [{}{}] ", response.getStatus(), request.getRequestURL(),
                getQueryStringIfNotEmptyFrom(request));
    }

    private String getQueryStringIfNotEmptyFrom(HttpServletRequest request) {
        return StringUtils.isEmpty(request.getQueryString()) ? "" : "?" + request.getQueryString();
    }
}
