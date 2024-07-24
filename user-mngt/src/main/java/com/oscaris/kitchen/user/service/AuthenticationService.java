package com.oscaris.kitchen.user.service;
/*
*
@author ameda
@project kitchen-reader
*
*/

import com.oscaris.kitchen.user.DTO.request.AuthenticationRequest;
import com.oscaris.kitchen.user.DTO.request.RegisterRequest;
import com.oscaris.kitchen.user.DTO.response.AuthenticationResponse;
import com.oscaris.kitchen.user.DTO.response.RegisterResponse;
import com.oscaris.kitchen.user.config.JwtService;
import com.oscaris.kitchen.user.entity.PasswordResetToken;
import com.oscaris.kitchen.user.entity.Role;
import com.oscaris.kitchen.user.entity.Users;
import com.oscaris.kitchen.user.entity.VerificationToken;
import com.oscaris.kitchen.user.exceptions.InvalidCredentialsException;
import com.oscaris.kitchen.user.repository.PasswordResetTokenRepository;
import com.oscaris.kitchen.user.repository.UserRepository;
import com.oscaris.kitchen.user.repository.VerificationTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private MailService mailService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private HttpServletRequest servletRequest;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    public RegisterResponse register(RegisterRequest registerRequest) {
        Optional<Users> fetchedUser = userRepository.findByEmail(registerRequest.getEmail());
        if (fetchedUser.isPresent()) {
            return RegisterResponse.builder()
                    .message("Registration was unsuccessful")
                    .build();
        }else{
            var user= Users.builder()
                    .userId(setId())
                    .name(registerRequest.getName())
                    .email(registerRequest.getEmail())
                    .password(passwordEncoder.encode(registerRequest.getPassword()))
                    .role(Role.USER)
                    .build();
            userRepository.save(user);
            var jwtToken=jwtService.generateToken(user);
            return RegisterResponse.builder()
                    .message("Registration was successful")
                    .build();
        }
    }

    public AuthenticationResponse login(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getEmail(),
                        authenticationRequest.getPassword()
                )
        );
        try{
            var user=userRepository.findByEmail(authenticationRequest.getEmail())
                    .orElseThrow(()->new UsernameNotFoundException("user with designated mail not found."));
            var jwtToken = jwtService.generateToken(user);
            return AuthenticationResponse.builder()
                    .jwtToken(jwtToken)
                    .message("successfully logged in")
                    .build();
        } catch (InvalidCredentialsException invalidCredentialsException){
            return AuthenticationResponse.builder()
                    .jwtToken(null)
                    .message("invalid credentials passed.")
                    .build();
        }
    }
    public String getEmail(AuthenticationRequest authenticationRequest){
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(),
                        authenticationRequest.getPassword()));
        Users  user = (Users) authentication.getPrincipal();
        return user.getEmail();
    }
    public Users getUser(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(
                        ()-> new UsernameNotFoundException("cannot establish the user with passed username."));
    }
    private String setId(){
        return UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 12);
    }

    public RegisterResponse registerAdmin(RegisterRequest request) {
        Optional<Users>fetchedUser = userRepository.findByEmail(request.getEmail());
        if (fetchedUser.isPresent()) {
            return RegisterResponse.builder()
                    .message("unsuccessful")
                    .build();
        }else{
            var user= Users.builder()
                    .userId(setId())
                    .name(request.getName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(user);
            var jwtToken=jwtService.generateToken(user);
            return RegisterResponse.builder()
                    .message("successful")
                    .build();
        }
    }

    /*
     * When a user registers, generate and send the token via their email...
     *
     * */

    public void sendVerificationEmail(Users user){
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(user)
                .build();
        verificationTokenRepository.save(verificationToken);

        String recipientEmail = user.getEmail();
        String subject = "Account Verification";
        String confirmationUrl = "/confirm?token=" + token;
        String message = "To verify your account, please click here: "+ confirmationUrl;
        mailService.sendSimpleMessage(recipientEmail,subject,message);
    }
    public void processForgotPassword(String email,String oldPassword,String newPassword){
        Users user = userRepository.findByEmail(email).get();
        if(user!=null){
            // generate token and send it...
            String resetToken = UUID.randomUUID().toString();
            PasswordResetToken passwordResetToken = PasswordResetToken.builder()
                    .reset(false)
                    .token(resetToken)
                    .user(user)
                    .email(email)
                    .build();
            String subject = "Change Your Password by clicking the below link";
            String confirmationUrl =
                    "http://"+ servletRequest.getServerName() +":"+servletRequest.getServerPort() +
                            "/api/auth/reset-password?email=" + email +"&oldPassword="+oldPassword+
                            "&newPassword="+newPassword+"&resetToken="+resetToken;
            String message = "Click to reset your password: "+ confirmationUrl;
            mailService.sendSimpleMessage(email,subject,message);
            passwordResetTokenRepository.save(passwordResetToken);
        }
    }
    public String resetPassword(String email,String newPassword,String resetToken){
        Users user = getUser(email);
        PasswordResetToken token = passwordResetTokenRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("user cannot be established."));
        if(token!=null){
            token.reset = true;
            passwordResetTokenRepository.save(token);
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return "successful";
        } else {
            return "unsuccessful";
        }
    }

    private String mobileNumberPlusCountryCode(String mobileNumber){
        String countryCode = "+91";
        // Append country code with the passed mobile number...
        StringBuilder fullMobileNumber = new StringBuilder(countryCode);
        fullMobileNumber.append(mobileNumber);
        //return complete number plus country code...
        return fullMobileNumber.toString();
    }

    public List<Users> allUsers() {
        return userRepository.findAll();
    }

    public Users userById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(()-> new UsernameNotFoundException("user could not be found with passed id."));
    }
    public List<Users> usersByRole(Role role){
        return userRepository.findByRole(role);
    }
}
