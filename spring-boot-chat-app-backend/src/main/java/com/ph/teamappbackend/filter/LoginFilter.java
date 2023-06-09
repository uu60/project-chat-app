package com.ph.teamappbackend.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import com.ph.teamappbackend.constant.RespCode;
import com.ph.teamappbackend.utils.JwtUtils;
import com.ph.teamappbackend.utils.LoginManager;
import com.ph.teamappbackend.utils.Resp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    public static final String TOKEN_KEY = "JWT-Token";

    @Override

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (UNCHECK_URI_LIST.contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }
        String jwt = request.getHeader(LoginFilter.TOKEN_KEY);
        try {
            DECODED_JWT_THREADLOCAL.set(JwtUtils.verify(jwt));
            boolean newLogin = LoginManager.checkAndDealWithExistedLogin();
            if (newLogin) {
                filterChain.doFilter(request, response);
            } else {
                throw new RuntimeException();
            }
        } catch (Exception e) {
            response.getOutputStream().write(gson.toJson(Resp.error(RespCode.JWT_TOKEN_INVALID,
                    e.getMessage())).getBytes());
        } finally {
            DECODED_JWT_THREADLOCAL.remove();
        }
    }
}
