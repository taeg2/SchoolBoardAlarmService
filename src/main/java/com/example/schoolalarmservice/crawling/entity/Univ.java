package com.example.schoolalarmservice.crawling.entity;

import com.example.schoolalarmservice.crawling.model.UnivCode;
import jakarta.persistence.*;
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

    @Enumerated(value = EnumType.STRING)
    private UnivCode univCode;


    public void updateLatestPostNumber(Long maxNumber) {
        latestPostNumber = maxNumber;
    }

}
