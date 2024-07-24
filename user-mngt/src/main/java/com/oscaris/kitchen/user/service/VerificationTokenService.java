package com.oscaris.kitchen.user.service;
/*
*
@author ameda
@project kitchen-reader
*
*/

import com.oscaris.kitchen.user.entity.Users;
import com.oscaris.kitchen.user.entity.VerificationToken;
import com.oscaris.kitchen.user.repository.UserRepository;
import com.oscaris.kitchen.user.repository.VerificationTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class VerificationTokenService {
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private HttpServletRequest servletRequest;

    public String generateVerificationToken(){
        return UUID.randomUUID().toString();
    }

    private void sendTokenToUserEmail(String userEmail, String token){
        sendSimpleMessage(userEmail,"Verify your email address please","Your verification token is: "+token);
        log.info("sending token to user",userEmail);
    }
    private void sendSimpleMessage(String to, String subject, String text){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
    }
    public void sendTokenToUser(Users user){
        String token = generateVerificationToken();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(user)
                .expirationTime(LocalDateTime.now().plusMinutes(5)) // expiring in 5 mins from now...
                .build();
        // send to user email and persist
        String confirmationUrl =
                "http://"+ servletRequest.getServerName() +":"+servletRequest.getServerPort() +
                        "/api/auth/verify-account?token="+token;

        sendTokenToUserEmail(user.getEmail(),confirmationUrl);
        // set the verify state of the user to true from false...
        user.verified = true;
        userRepository.save(user);
        verificationTokenRepository.save(verificationToken);
    }

    public boolean verifyToken(String token){
        Optional<VerificationToken> optionalVerificationToken =
                verificationTokenRepository.findByToken(token);
        if(optionalVerificationToken.isPresent()){
            VerificationToken verificationToken = optionalVerificationToken.get();
            return !verificationToken.getExpirationTime().isBefore(LocalDateTime.now());
        }
        return false;
    }

}
