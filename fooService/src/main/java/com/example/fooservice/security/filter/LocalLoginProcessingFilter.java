package com.example.fooservice.security.filter;

import com.example.fooservice.security.util.LocalAuthenticationToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LocalLoginProcessingFilter extends AbstractAuthenticationProcessingFilter {

    public LocalLoginProcessingFilter() {
        super(new AntPathRequestMatcher("/login"));
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
        AccountParamDto accountParam = objectMapper.readValue(request.getReader(), AccountParamDto.class);

        if(!StringUtils.hasText(accountParam.getUsername()) || !StringUtils.hasText(accountParam.getPassword())) {
            throw new IllegalArgumentException("Username or Password is required");
        }

        LocalAuthenticationToken token = new LocalAuthenticationToken(accountParam.getUsername(), accountParam.getPassword());

        return this.getAuthenticationManager().authenticate(token);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class AccountParamDto {
        String username;
        String password;
    }
}
