package com.example.schoolalarmservice.crawling.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;


@Entity
@Getter
@Setter
public class Univ {


    @Id
    @GeneratedValue
    @Column(name = "UNIV_ID")
    private Long id;

    private String univName;

    private String URL;

    private Long latestPostNumber;


    public void updateLatestPostNumber(Long maxNumber) {
        latestPostNumber = maxNumber;
    }

}
