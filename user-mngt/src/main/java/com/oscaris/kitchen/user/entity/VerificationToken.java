package com.oscaris.kitchen.user.entity;

/*
*
@author ameda
@project kitchen-reader
*
*/

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class VerificationToken {
    @Id
    private Long id;
    private String token;
    @DBRef
    private Users user;
    private LocalDateTime expirationTime;
}
