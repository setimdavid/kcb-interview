package com.kcb.interview.kcb.config;


import com.kcb.interview.kcb.model.ClientAuth;
import com.kcb.interview.kcb.objects.ClientDto;
import com.kcb.interview.kcb.repository.ClientAuthRepository;
import com.kcb.interview.kcb.util.GlobalMethods;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JwtUserDetailsService implements UserDetailsService {
    @Autowired
    private ClientAuthRepository clientAuthRepository;
    @Autowired
    private PasswordEncoder bcryptEncoder;

    @Override
    public UserDetails loadUserByUsername(String clientKey) throws UsernameNotFoundException {
        ClientAuth client = clientAuthRepository.findClientAuthByClientKey(clientKey);
        if (client == null) {
            throw new UsernameNotFoundException("Client not found with clientKey : " + clientKey);
        }
        final List<GrantedAuthority> authorities = new ArrayList<>();
        UserDetails userDetails = new User(client.getClientKey(), client.getClientSecret(), authorities);
        return userDetails;
    }

    public ClientAuth save(ClientDto client) {
        ClientAuth newUser = new ClientAuth();
        newUser.setClientKey(client.getClientKey());
        newUser.setClientSecret(bcryptEncoder.encode(client.getClientSecret()));
        return clientAuthRepository.save(newUser);
    }
}