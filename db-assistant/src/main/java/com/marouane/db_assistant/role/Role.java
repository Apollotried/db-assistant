package com.marouane.db_assistant.role;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.marouane.db_assistant.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Role {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = true)
    private String name;

    @ManyToMany(mappedBy = "roles")
    @JsonIgnore
    private List<User> users;

}
