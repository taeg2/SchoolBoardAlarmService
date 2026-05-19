package com.example.schoolalarmservice.crawling.entity;

import com.example.schoolalarmservice.crawling.model.UnivStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class UserUniv {
    @Id
    @GeneratedValue
    @Column(name = "USER_UNIV_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne
    @JoinColumn(name = "UNIV_ID")
    private Univ univ;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "IS_ACTIVE")
    private UnivStatus isActive;
}
