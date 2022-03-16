package org.paytm.milestone2._Milestone2.controllers;

import org.paytm.milestone2._Milestone2.DTO.Request.SignInRequestBody;
import org.paytm.milestone2._Milestone2.models.User;
import org.paytm.milestone2._Milestone2.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    // @desc    User Signup
    // @route   POST /signup
    // @access  Public
    @RequestMapping(value = "/signup",method = RequestMethod.POST)
    public ResponseEntity<?> signUpUser(@RequestBody User user){
        return userService.signUpUser(user);
    }

    // @desc    User Signin
    // @route   POST /signin
    // @access  Public
    @RequestMapping(value = "/signin",method = RequestMethod.POST)
    public ResponseEntity<?> signInUser(@RequestBody SignInRequestBody signInRequestBody){
        return userService.signInUser(signInRequestBody);
    }
}
