package com.example.fooservice.api;

import com.example.fooservice.db.dto.UserDto;
import com.example.fooservice.security.util.jwt.GetTokenInfo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

@Controller
public class GoogleAuthorizeController {

    @GetMapping(value = "/auth")
    public String permit(@RequestParam String code, HttpServletResponse servletResponse) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", "512265054010-g3t0difpimrb59jr33dqld08gqkr7ijf.apps.googleusercontent.com");
        map.add("client_secret", "Y0dnZIQ--M6uIz_Xafe7v1a9");
        map.add("code", code);
        map.add("grant_type", "authorization_code");
        map.add("redirect_uri", "http://localhost:8000/foo/auth");
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        try {
            ResponseEntity<TokenData> response = restTemplate.postForEntity("https://oauth2.googleapis.com/token", entity, TokenData.class);
            Cookie access_cookie = new Cookie("access_token", response.getBody().access_token);
            Cookie platform_name = new Cookie("platform", "google");
            access_cookie.setPath("/");
            platform_name.setPath("/");
            servletResponse.addCookie(access_cookie);
            servletResponse.addCookie(platform_name);

        } catch (Exception e)  {
            System.out.println("Error");
        }
        return "redirect:http://localhost:3000/";
    }

    @GetMapping(value = "/user")
    @ResponseBody
    public UserDto authorize(HttpServletRequest request) throws IOException {
        String access_token = "";
        String platform = "";
        for(Cookie cookie : request.getCookies()) {
            if(cookie.getName().equals("access_token")) {
                access_token = cookie.getValue();
            } else if(cookie.getName().equals("platform")) {
                platform = cookie.getValue();
            }
        }

        UserDto user = new UserDto();
        if(platform.equals("google")) {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(access_token);
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(new LinkedMultiValueMap<>(),headers);
            ResponseEntity<GoogleProfile> response = restTemplate.exchange(
                    "https://www.googleapis.com/userinfo/v2/me",
                    HttpMethod.GET,
                    entity,
                    GoogleProfile.class
            );
            user.setName(Objects.requireNonNull(response.getBody()).name);
        } else if(platform.equals("local")) {
            user.setName(GetTokenInfo.getUserName(access_token));
        }
        return user;
    }

    @Getter @Setter
    static class Code {
        String code;
    }

    @Getter @Setter
    static class TokenData {
        String access_token;
        Integer expires_in;
        String refresh_token;
        String scope;
        String token_type;
    }

    @Getter @Setter
    static class GoogleProfile {
        String family_name;
        String name;
    }
}
