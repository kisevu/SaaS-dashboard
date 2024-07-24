package com.oscaris.kitchen.user.entity;
/*
*
@author ameda
@project kitchen-reader
*
*/

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "passwordResetToken")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PasswordResetToken {

    @Id
    private Long id;
    public boolean reset = false;
    private String token;
    @DBRef
    private Users user;
    private String email;
}
