package com.ph.teamappbackend.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import com.ph.teamappbackend.constant.ErrorCodeConst;
import com.ph.teamappbackend.utils.JwtUtils;
import com.ph.teamappbackend.utils.Resp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author octopus
 * @since 2023/4/15 22:57
 */
@Component
public class LoginFilter implements Filter {

    @Autowired
    Gson gson;

    public final static ThreadLocal<DecodedJWT> DECODED_JWT_THREADLOCAL = new ThreadLocal<>();
    private final static List<String> UNCHECK_URI_LIST = new ArrayList<>();
    static {
        UNCHECK_URI_LIST.addAll(Arrays.asList("/login", "/register"));
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String jwt = request.getHeader("JWT-Token");
        boolean isTokenOk = false;
        if (StringUtils.hasText(jwt)) {
            try {
                DECODED_JWT_THREADLOCAL.set(JwtUtils.verify(jwt));
                isTokenOk = true;
                filterChain.doFilter(request, response);
                DECODED_JWT_THREADLOCAL.remove();
            } catch (Exception ignored) {
                // invalid token
            }
        } else if (UNCHECK_URI_LIST.contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }
        if (!isTokenOk) {
            response.getOutputStream().write(gson.toJson(Resp.error(ErrorCodeConst.JWT_TOKEN_INVALID,
                    "jwt token invalid")).getBytes());
        }
    }
}
