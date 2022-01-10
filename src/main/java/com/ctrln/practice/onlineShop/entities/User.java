package com.ctrln.practice.onlineShop.entities;

import com.ctrln.practice.onlineShop.enums.Roles;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String username;
    private String password;
    private String surname;
    private String firstname;
    @Embedded
    private Address address;

    @ElementCollection
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "roles")
    @Enumerated(value = EnumType.STRING)
    private Collection<Roles> roles;

}
