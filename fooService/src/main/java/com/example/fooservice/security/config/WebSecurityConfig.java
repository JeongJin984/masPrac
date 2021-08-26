package com.example.fooservice.security.config;

import com.example.fooservice.security.filter.LocalLoginProcessingFilter;
import com.example.fooservice.security.handler.CustomLoginFailureHandler;
import com.example.fooservice.security.handler.CustomLoginSuccessHandler;
import com.example.fooservice.security.provider.LocalAuthenticationProvider;
import com.example.fooservice.security.service.CustomUserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomUserDetailsServiceImpl userDetailsService;
    private final AuthenticationSuccessHandler customLoginSuccessHandler;
    private final AuthenticationFailureHandler customLoginFailureHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
        .and()
                .csrf().disable()
                .authorizeRequests()
                .anyRequest().permitAll()
        .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
                .formLogin().disable()
                .addFilterBefore(localAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(localAuthenticationProvider());
        auth.userDetailsService(userDetailsService);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowCredentials(true);
        configuration.addAllowedOrigin("http://localhost:8000");
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public LocalLoginProcessingFilter localAuthenticationFilter() throws Exception {
        LocalLoginProcessingFilter localLoginProcessingFilter = new LocalLoginProcessingFilter();
        localLoginProcessingFilter.setAuthenticationManager(authenticationManagerBean());
        localLoginProcessingFilter.setAuthenticationSuccessHandler(customLoginSuccessHandler);
        localLoginProcessingFilter.setAuthenticationFailureHandler(customLoginFailureHandler);
        localLoginProcessingFilter.afterPropertiesSet();
        return localLoginProcessingFilter;
    }

    @Bean
    public LocalAuthenticationProvider localAuthenticationProvider() {
        return new LocalAuthenticationProvider();
    }

}