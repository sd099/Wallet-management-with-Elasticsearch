package org.paytm.milestone2._Milestone2.services;

import org.paytm.milestone2._Milestone2.DTO.Request.SignInRequestBody;
import org.paytm.milestone2._Milestone2.DTO.Response.MessageResponse;
import org.paytm.milestone2._Milestone2.DTO.Response.SignInResponseBody;
import org.paytm.milestone2._Milestone2.models.User;
import org.paytm.milestone2._Milestone2.repository.UserRepository;
import org.paytm.milestone2._Milestone2.security.jwt.JwtUtil;
import org.paytm.milestone2._Milestone2.security.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class UserService {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    JwtUtil jwtUtil;

    private static Logger logger = LogManager.getLogger(UserService.class);

    //Method for sign up user
    public ResponseEntity<?> signUpUser(User user){
        if(userRepository.findByUserName(user.getUserName())!=null){
            logger.debug("Username = "+user.getUserName()+" already exist. Different username required.");
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username already Exist.Try different Username"));
        }
        if(userRepository.findByEmailId(user.getEmailId())!=null){
            logger.debug("Email = "+user.getEmailId()+" already exist. Different email required");
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email already Exist.Try different Email"));
        }
        if(userRepository.findByMobileNumber(user.getMobileNumber())!=null){
            logger.debug("Mobile Number = "+user.getMobileNumber()+" already exist. Different mobile number required");
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Mobile Number already Exist.Try different Mobile Number"));
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        logger.debug("Signup complete");
        logger.info("signup details. username = "+user.getUserName()+" mobile number = "+user.getMobileNumber());

        return ResponseEntity.ok(new MessageResponse("Signup Complete"));
    }


    //Method for sign in user
    public ResponseEntity<?> signInUser(SignInRequestBody signInRequestBody){

        try{
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequestBody.getUsername(),signInRequestBody.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails=this.customUserDetailsService.loadUserByUsername(signInRequestBody.getUsername());
            String jwt =this.jwtUtil.generateToken(userDetails);

            logger.debug("Sign in complete");
            logger.info("sign in details. Username = "+signInRequestBody.getUsername());

            return ResponseEntity.ok(new SignInResponseBody(jwt));
        }

        catch (Exception exception) {
            logger.error("Invalid credentials");
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Invalid credentials!"));
        }
    }
}
