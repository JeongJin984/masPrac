package com.example.fooservice.security.util.jwt;

import com.example.fooservice.security.util.AccountContext;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Component
public class TokenUtils extends CreateTokenInfo {

    private Environment env;

    @Autowired
    public TokenUtils(Environment env) {
        this.env = env;
    }

    public String generateJwtToken(AccountContext context, int minute) {
        String secret = env.getProperty("token.secret");
        JwtBuilder builder = Jwts.builder()
                .setSubject(context.getUsername())
                .setHeader(createHeader())
                .setClaims(createClaims(context))
                .setExpiration(createExpireDate(minute))
                .signWith(SignatureAlgorithm.HS512, secret);

        return builder.compact();
    }


}