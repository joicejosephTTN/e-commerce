package com.ttn.ecommerce.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class NotificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "token_gen")
    @SequenceGenerator(name="token_gen", sequenceName = "token_seq", initialValue = 1, allocationSize = 1)
    private long id;

    private String token;


    private LocalDateTime creationTime;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public NotificationToken(User user) {
        this.user = user;
        this.creationTime = LocalDateTime.now();
        this.token = UUID.randomUUID().toString();
    }


}
