package com.example.fooservice.security.handler;

import com.example.fooservice.db.dto.UserDto;
import com.example.fooservice.security.util.AccountContext;
import com.example.fooservice.security.util.jwt.TokenUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
@NoArgsConstructor
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    private TokenUtils tokenUtils;
    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public CustomLoginSuccessHandler(TokenUtils tokenUtils) {
        this.tokenUtils = tokenUtils;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        final UserDto user  = new UserDto();
        user.setName(authentication.getName());
        final AccountContext context = new AccountContext((String)authentication.getPrincipal(), null, authentication.getAuthorities());

        final String accessToken = tokenUtils.generateJwtToken(context, 60 * 24 * 7);
        Cookie[] cookies = new Cookie[2];

        cookies[0] = new Cookie("access_token", accessToken);
        cookies[1] = new Cookie("platform", "local");

        // expires in 7 days
        cookies[0].setMaxAge(7 * 24 * 60 * 60);
        cookies[1].setMaxAge(7 * 24 * 60 * 60);

        // optional properties
        cookies[0].setSecure(true);
        cookies[0].setHttpOnly(true);
        cookies[0].setPath("/");
        cookies[1].setSecure(true);
        cookies[1].setHttpOnly(true);
        cookies[1].setPath("/");


        response.addCookie(cookies[0]);
        response.addCookie(cookies[1]);
        response.getWriter().write(objectMapper.writeValueAsString(user));
    }
}
