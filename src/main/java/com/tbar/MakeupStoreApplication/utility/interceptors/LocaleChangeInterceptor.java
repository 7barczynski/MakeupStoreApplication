package com.tbar.MakeupStoreApplication.utility.interceptors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * <p> * * * * * * * * *
 * === MODIFIED ===
 * * * * * * * * * * </p>
 *
 * Interceptor that allows for changing the current locale on every request,
 * via a configurable request parameter (default parameter name: "locale").
 *
 * <p>It is override version of original springframework's LocaleChangeInterceptor
 * that allow to check if the parameter in request is a valid language of this application
 * and sets ignoreInvalidLocale variable default to true. Modified parts are marked in comments.
 * All other features are the same.
 *
 * @author Juergen Hoeller
 * @author Rossen Stoyanchev
 * @author 7omasz8
 * @see org.springframework.web.servlet.LocaleResolver
 * @since 20.06.2003
 */
@Slf4j
public class LocaleChangeInterceptor extends HandlerInterceptorAdapter {

    // === constants ===
    /**
     * Default name of the locale specification parameter: "locale".
     */
    public static final String DEFAULT_PARAM_NAME = "locale";

    // === fields ===
    private String paramName = DEFAULT_PARAM_NAME;

    @Nullable
    private String[] httpMethods;

    private boolean ignoreInvalidLocale = true;

    /*
    * * * * * * * * * *
    * === MODIFIED ===
    * * * * * * * * * *
    * languagesList contains languages codes that are available for application.
    */
    private final List<String> languagesList;

    // === constructors ===
    /*
     * * * * * * * * * *
     * === MODIFIED ===
     * * * * * * * * * *
     * This constructor is added just to set languagesList.
     */
    public LocaleChangeInterceptor(String[] languagesList) {
        this.languagesList = Arrays.asList(languagesList);
        log.debug("Setting languagesList from constructor. languagesList = {}", this.languagesList);
    }

    // === public methods ===
    /**
     * Set the name of the parameter that contains a locale specification
     * in a locale change request. Default is "locale".
     */
    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    /**
     * Return the name of the parameter that contains a locale specification
     * in a locale change request.
     */
    public String getParamName() {
        return this.paramName;
    }

    /**
     * Configure the HTTP method(s) over which the locale can be changed.
     *
     * @param httpMethods the methods
     * @since 4.2
     */
    public void setHttpMethods(@Nullable String... httpMethods) {
        this.httpMethods = httpMethods;
    }

    /**
     * Return the configured HTTP methods.
     *
     * @since 4.2
     */
    @Nullable
    public String[] getHttpMethods() {
        return this.httpMethods;
    }

    /**
     * Set whether to ignore an invalid value for the locale parameter.
     *
     * @since 4.2.2
     */
    public void setIgnoreInvalidLocale(boolean ignoreInvalidLocale) {
        this.ignoreInvalidLocale = ignoreInvalidLocale;
    }

    /**
     * Return whether to ignore an invalid value for the locale parameter.
     *
     * @since 4.2.2
     */
    public boolean isIgnoreInvalidLocale() {
        return this.ignoreInvalidLocale;
    }

    /**
     * Specify whether to parse request parameter values as BCP 47 language tags
     * instead of Java's legacy locale specification format.
     * <p><b>NOTE: As of 5.1, this resolver leniently accepts the legacy
     * {@link Locale#toString} format as well as BCP 47 language tags.</b>
     *
     * @see Locale#forLanguageTag(String)
     * @see Locale#toLanguageTag()
     * @since 4.3
     * @deprecated as of 5.1 since it only accepts {@code true} now
     */
    @Deprecated
    public void setLanguageTagCompliant(boolean languageTagCompliant) {
        if (!languageTagCompliant) {
            throw new IllegalArgumentException("LocaleChangeInterceptor always accepts BCP 47 language tags");
        }
    }

    /**
     * Return whether to use BCP 47 language tags instead of Java's legacy
     * locale specification format.
     *
     * @since 4.3
     * @deprecated as of 5.1 since it always returns {@code true} now
     */
    @Deprecated
    public boolean isLanguageTagCompliant() {
        return true;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws ServletException {
        String newLocale = request.getParameter(getParamName());
        if (newLocale != null) {
            if (checkHttpMethod(request.getMethod())) {
                LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
                if (localeResolver == null) {
                    throw new IllegalStateException(
                            "No LocaleResolver found: not in a DispatcherServlet request?");
                }
                try {
                    localeResolver.setLocale(request, response, parseLocaleValue(newLocale));
                }
                catch (IllegalArgumentException ex) {
                    if (isIgnoreInvalidLocale()) {
                        log.debug("Ignoring invalid locale value [" + newLocale + "]: " + ex.getMessage());
                    }
                    else {
                        throw ex;
                    }
                }
            }
        }
        // Proceed in any case.
        return true;
    }

    // === private & protected methods ===
    private boolean checkHttpMethod(String currentMethod) {
        String[] configuredMethods = getHttpMethods();
        if (ObjectUtils.isEmpty(configuredMethods)) {
            return true;
        }
        for (String configuredMethod : configuredMethods) {
            if (configuredMethod.equalsIgnoreCase(currentMethod)) {
                return true;
            }
        }
        return false;
    }

    /**
     * <p>
     * * * * * * * * * * *
     * * === MODIFIED ===
     * * * * * * * * * * *
     * </p>
     * Parse the given locale value as coming from a request parameter and checking if
     * it's in language list defined in properties file.
     * <p>The default implementation calls {@link StringUtils#parseLocale(String)},
     * accepting the {@link Locale#toString} format as well as BCP 47 language tags,
     * but in modified version it isn't relevant until these formats doesn't exists in
     * application's language list.</p>
     *
     * @param localeValue the locale value to parse
     * @return the corresponding {@code Locale} instance
     * @since 4.3
     */
    @Nullable
    protected Locale parseLocaleValue(String localeValue) {
        if (!languagesList.contains(localeValue)) {
            throw new IllegalArgumentException("Given locale isn't on valid languages list of this application.");
        }
        return StringUtils.parseLocale(localeValue);
    }
}

