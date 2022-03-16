package org.paytm.milestone2._Milestone2.security.services;

import org.paytm.milestone2._Milestone2.models.User;
import org.paytm.milestone2._Milestone2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{

        try{
            User user = userRepository.findByUserName(username);
            return new CustomUserDetails(user);

        }catch (Exception e){
            throw new UsernameNotFoundException("User Not Found with username: " + username);
        }

    }
}
