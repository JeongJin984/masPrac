package com.example.fooservice.security.util.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.Base64;

@Slf4j
@Component
public class GetTokenInfo {

    @Value("${token.secret}")
    private String secretKey;

    private Claims getClaimsFormToken(String token) {
        return Jwts.parser().setSigningKey(secretKey)
                .parseClaimsJws(token).getBody();
    }

    public String getUsernameFromValidToken(String token) {
        Claims claims = getClaimsFormToken(token);
        return (String) claims.get("username");
    }

//    private Role getRoleFromValidToken(String token) {
//        Claims claims = getClaimsFormToken(token);
//        return (Role) claims.get("role");
//    }

    public boolean isValidToken(String token) {
        String subject = null;
        try {
            subject = Jwts.parser().setSigningKey(secretKey)
                    .parseClaimsJws(token).getBody()
                    .getSubject();
        } catch (JwtException | NullPointerException exception) {
            return false;
        }
        return true;
    }

    private static String decode(String token) {
        String[] split_string = token.split("\\.");
        String base64EncodedHeader = split_string[0];
        String base64EncodedBody = split_string[1];
        String base64EncodedSignature = split_string[2];

        return new String(Base64.getDecoder().decode(base64EncodedBody));
    }

    public static String getUserName(String token) {
        String username = null;

        String result = decode(token);
        String[] split = result.replaceAll("[^a-z|0-9|,|:]", "").split("[,|:]");

        for (int i = 0; i < split.length; i++) {
            if(split[i].equals("name")) {
                username = split[i+1];
            };
        }
        return username;
    }
}