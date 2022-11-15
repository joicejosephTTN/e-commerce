package com.ttn.ecommerce.entity;

import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_gen")
    @SequenceGenerator(name = "user_gen", sequenceName = "user_seq", initialValue = 1, allocationSize=1)
    private long id;

    private String email;

    private String firstName;

    private String middleName;

    private String lastName;

    private String password;

    private boolean isDeleted;

    private boolean isActive;

    private boolean isExpired;

    private boolean isLocked;

    private int invalidAttemptCount;

    private Date passwordUpdateDate;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Seller seller;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Customer customer;

    @OneToOne
    @JoinTable(name = "user_role",
            joinColumns ={@JoinColumn(name="user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name="role_id", referencedColumnName = "id")})
    private Role role;


}
