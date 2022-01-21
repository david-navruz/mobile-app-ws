package com.appsdeveloperblog.app.ws.io.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "users")
public class UserEntity implements Serializable {

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false, length = 120)
    private String email;

    @Column(nullable = false)
    private String encryptedPassword;

    private String emailVerificationToken;

    @Column(nullable = true, columnDefinition = "boolean default false")
    private Boolean emailVerificationStatus;

    @OneToMany(mappedBy = "userDetails", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<AddressEntity> addresses;

}
