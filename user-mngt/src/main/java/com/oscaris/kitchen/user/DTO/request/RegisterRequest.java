package com.oscaris.kitchen.user.DTO.request;
/*
*
@author ameda
@project kitchen-reader
*
*/

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {

    private String name;
    private String email;
    private String password;
    private String mobile;
    private String country;
    private String city;

}
