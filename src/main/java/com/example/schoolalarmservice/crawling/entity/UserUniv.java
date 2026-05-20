package com.example.schoolalarmservice.crawling.entity;

import com.example.schoolalarmservice.crawling.model.UnivStatus;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    // UserUniv.java 내부에 추가
    public void cancelSubscription() {
        this.isActive = UnivStatus.CANCEL; // 상태값 타입에 맞게 수정 (예: Status.CANCEL)
    }

    public void enrollSubscription() {
        this.isActive = UnivStatus.ENROLLED; // 상태값 타입에 맞게 수정
    }
}
