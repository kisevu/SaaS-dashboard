package com.oscaris.kitchen.user.auth;
/*
*
@author ameda
@project kitchen-reader
*
*/

import com.oscaris.kitchen.user.entity.Role;
import com.oscaris.kitchen.user.entity.Users;
import com.oscaris.kitchen.user.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthenticationService authenticationService;


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> allUsers(){
        return new ResponseEntity<>(authenticationService.allUsers(), HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> userWithId(@PathVariable("userId") String userId){
        Users user = authenticationService.userById(userId);
        if(user != null){
            return new ResponseEntity<>(user,HttpStatus.OK);
        }else{
            return new ResponseEntity<>(null,HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/user/admin")
    public ResponseEntity<?> allAdmins(){
        return new ResponseEntity<>(authenticationService.usersByRole(Role.ADMIN),HttpStatus.OK);
    }
    @GetMapping("/user/recruiter")
    public ResponseEntity<?> allRecruiters(){
        return new ResponseEntity<>(authenticationService.usersByRole(Role.USER),HttpStatus.OK);
    }


}
