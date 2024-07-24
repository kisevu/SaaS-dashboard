package com.oscaris.kitchen.user.DTO.response;

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
public class AuthenticationResponse {
    private String jwtToken;
    private String message;
}
