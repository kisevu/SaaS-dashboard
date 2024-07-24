package com.oscaris.kitchen.user.auth;

import com.oscaris.kitchen.user.DTO.request.AuthenticationRequest;
import com.oscaris.kitchen.user.DTO.request.RegisterRequest;
import com.oscaris.kitchen.user.entity.Users;
import com.oscaris.kitchen.user.repository.UserRepository;
import com.oscaris.kitchen.user.service.AuthenticationService;
import com.oscaris.kitchen.user.service.VerificationTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/*
*
@author ameda
@project kitchen-reader
*
*/

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final VerificationTokenService verificationTokenService;
    private final UserRepository userRepository;

    @PostMapping("/user/sign-up")
    public ResponseEntity<?> registerRecruiter(@RequestBody @Valid RegisterRequest request, BindingResult result){
        if(result.hasErrors()){
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        var response = authenticationService.register(request);
        if(response.getMessage().equals("successful")){
            return new ResponseEntity<>("user exists",HttpStatus.CONFLICT);
        }else{
            return new ResponseEntity<>("user created successfully.", HttpStatus.CREATED);
        }
    }
    @PostMapping("/admin/sign-up")
    public ResponseEntity<?> registerAdmin(@RequestBody @Valid RegisterRequest request, BindingResult result){
        if(result.hasErrors()){
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        var response = authenticationService.registerAdmin(request);
        if(response.getMessage().equals("successful")){
            return new ResponseEntity<>("user created successfully.",HttpStatus.CREATED);
        }else{
            return new ResponseEntity<>("user exists",HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest authenticationRequest){
        return ResponseEntity.ok(authenticationService.login(authenticationRequest));
    }
    @PostMapping("/generate-token")
    public ResponseEntity<?> generateToken(@RequestParam("email") String email){
        Users user = authenticationService.getUser(email);
//        return new ResponseEntity<>(verificationTokenService.sendTokenToUser(user),HttpStatus.CREATED);
        if(user!=null){
            //user is not null...
            verificationTokenService.sendTokenToUser(user);
            return new ResponseEntity<>("Check your email for token to verify your email in the next step",HttpStatus.CREATED);
        }else{
            //user could not be found therefore null...
            return new ResponseEntity<>("Please go ahead and register, your email could not be found.",HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/verify-account")
    public ResponseEntity<?> verifyToken(@RequestParam("token") String token){
        if(verificationTokenService.verifyToken(token)){
            return ResponseEntity.ok("Token is valid");
        }else{
            return ResponseEntity.badRequest().body("Token is invalid / expired.");
        }
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam("email") String email,
                                            @RequestParam("oldPassword") String oldPassword,
                                            @RequestParam("newPassword") String newPassword){
        authenticationService.processForgotPassword(email,oldPassword,newPassword);
        return new ResponseEntity<>("reset token sent to: "+email,HttpStatus.OK);
    }
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam("email") String email,
                                           @RequestParam("newPassword")String newPassword,
                                           @RequestParam("resetToken") String resetToken){
        if(authenticationService.resetPassword(email,newPassword,resetToken).equals("successful")){
            return new ResponseEntity<>("your password reset was successful.",HttpStatus.OK);
        }else{
            return new ResponseEntity<>("your password reset was not successful.",HttpStatus.BAD_GATEWAY);
        }
    }
}
