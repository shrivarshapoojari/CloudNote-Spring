package com.cloudnote.spring.demo.utils;

import com.cloudnote.spring.demo.Repository.UserRepository;
import com.cloudnote.spring.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {

    @Autowired
    UserRepository userRepository;

    public  Long  loggedInUserId() throws Exception {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        User user=userRepository.findByUserName(authentication.getName()).orElseThrow(()->new Exception("User Not found"));

        return user.getUserId();
    }



    public  User loggedInUser() throws Exception {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();

        return userRepository.findByUserName(authentication.getName()).orElseThrow(()->new Exception("User Not found"));
    }
}
