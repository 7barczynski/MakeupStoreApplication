package com.tbar.MakeupStoreApplication.utility.interceptors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This iterceptor logs every request that is handled by application.
 */
@Slf4j
public class LoggerInterceptor implements HandlerInterceptor {

    // === handler methods ===
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Baeldung's logs
//        log.info("[preHandle][" + request + "][" + request.getMethod() + "]" + request.getRequestURI() + getParameters(request));

        // Tim Buchalka's logs
        log.debug("preHandle method called. handler = {}", handler);
        log.debug("URL = {}", request.getRequestURL());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // Baeldung's logs
//        log.info("[postHandle][" + request + "]");

        // Tim Buchalka's logs
        log.debug("postHandle method called. handler = {}", handler);
        log.debug("URL = {}", request.getRequestURL());
        if (modelAndView != null) {
            log.debug("model = {}", modelAndView.getModel());
            log.debug("view = {}", modelAndView.getViewName());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // Baeldung's logs
//        if (ex != null){
//            ex.printStackTrace();
//        }
//        log.info("[afterCompletion][" + request + "][exception: " + ex + "]");

        // Tim Buchalka's logs
        log.debug("afterCompletion method called. handler = {}", handler);
        log.debug("URL = {}", request.getRequestURL());
    }

    // === private methods ===
    // For Baeldung's logs
//    private String getParameters(final HttpServletRequest request) {
//        final StringBuffer posted = new StringBuffer();
//        final Enumeration<?> e = request.getParameterNames();
//        if (e != null)
//            posted.append("?");
//        while (e != null && e.hasMoreElements()) {
//            if (posted.length() > 1)
//                posted.append("&");
//            final String curr = (String) e.nextElement();
//            posted.append(curr).append("=");
//            if (curr.contains("password") || curr.contains("answer") || curr.contains("pwd")) {
//                posted.append("*****");
//            } else {
//                posted.append(request.getParameter(curr));
//            }
//        }
//
//        final String ip = request.getHeader("X-FORWARDED-FOR");
//        final String ipAddr = (ip == null) ? getRemoteAddr(request) : ip;
//        if (!ipAddr.isBlank())
//            posted.append("&_psip=").append(ipAddr);
//        return posted.toString();
//    }
//
//    private String getRemoteAddr(HttpServletRequest request) {
//        String ipFromHeader = request.getHeader("X-FORWARDED-FOR");
//        if (ipFromHeader != null && ipFromHeader.length() > 0) {
//            log.debug("ip from proxy - X-FORWARDED-FOR : " + ipFromHeader);
//            return ipFromHeader;
//        }
//        return request.getRemoteAddr();
//    }
}
