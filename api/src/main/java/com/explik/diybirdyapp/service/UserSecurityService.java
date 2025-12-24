package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.user.UserModel;
import com.explik.diybirdyapp.persistence.query.FindUserByEmailQuery;
import com.explik.diybirdyapp.persistence.query.handler.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserSecurityService implements UserDetailsService {
    @Autowired
    QueryHandler<FindUserByEmailQuery, Optional<UserModel>> findUserByEmailQueryHandler;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var query = new FindUserByEmailQuery();
        query.setEmail(email);
        UserModel user = findUserByEmailQueryHandler.handle(query)
                .orElseThrow(() ->
                        new UsernameNotFoundException(email + " not found." ));

        Set<GrantedAuthority> authorities = user
                .getRoles()
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                authorities
        );
    }
}
