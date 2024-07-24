package com.oscaris.kitchen.user.entity;/*
*
@author ameda
@project kitchen-reader
*
*/

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Document
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Users implements UserDetails, Serializable {
    @Serial
    @Transient
    private static final long serialVersionUID =
            UUID.randomUUID().getLeastSignificantBits();
    @Id
    private String userId;
    @NotNull
    private String name;
    @NotNull
    private String email;
    @NotNull
    private String password;
    public Role role;
    @NotNull
    @Builder.Default
    public boolean verified = false;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
