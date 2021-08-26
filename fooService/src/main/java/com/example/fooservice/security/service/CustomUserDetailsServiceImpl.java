package com.example.fooservice.security.service;

import com.example.fooservice.db.entity.Account;
import com.example.fooservice.security.util.AccountContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsServiceImpl implements UserDetailsService {

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Account account = new Account();
        account.setUsername(s);
        String encoded = passwordEncoder.encode("asdf");
        account.setPassword(encoded);

//        if(account == null) {
//            throw new UsernameNotFoundException("No user found with username: " + s);
//        }

        Set<GrantedAuthority> userRoles = new HashSet<>();

        return new AccountContext(account, userRoles);
    }
}