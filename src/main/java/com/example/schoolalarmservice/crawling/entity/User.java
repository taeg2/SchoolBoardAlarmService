package com.example.schoolalarmservice.crawling.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    @Column(name = "USER_ID")
    private Long id;

    private Long teleId;
}
