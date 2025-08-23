package com.marouane.db_assistant.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.marouane.db_assistant.message.ChatMessage;
import com.marouane.db_assistant.role.Role;
import com.marouane.db_assistant.sql.QueryHistory;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "_user")
public class User implements UserDetails, Principal {
    @Id
    @GeneratedValue
    private Integer id;
    private String firstName;
    private String lastName;

    @Column(unique = true)
    private String email;
    private String password;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<QueryHistory> queryHistory = new ArrayList<>();


    @ManyToMany(fetch = FetchType.EAGER)
    private List<Role> roles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<ChatMessage> messages;


    @Override
    public String getName() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .toList();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    @Override
    public String getUsername() {
        return email;
    }

    public String fullName(){
        return firstName + " " + lastName;
    }

}
