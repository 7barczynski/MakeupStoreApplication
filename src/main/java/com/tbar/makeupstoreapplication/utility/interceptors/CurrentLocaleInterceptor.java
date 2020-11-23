package com.tbar.makeupstoreapplication.utility.interceptors;

import com.tbar.makeupstoreapplication.utility.AttributeNames;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class CurrentLocaleInterceptor extends HandlerInterceptorAdapter {

    @SuppressWarnings("NullableProblems")
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           @Nullable ModelAndView modelAndView) {
        if (modelAndView != null) {
            var currentLocale = LocaleContextHolder.getLocale();
            modelAndView.addObject(AttributeNames.CURRENT_LOCALE, currentLocale);
            log.debug("Current locale (\"{}\") added to model", currentLocale);
        }
    }
}
