package com.example.schoolalarmservice.crawling.repostiory;

import com.example.schoolalarmservice.crawling.entity.Univ;
import com.example.schoolalarmservice.crawling.entity.User;
import com.example.schoolalarmservice.crawling.entity.UserUniv;
import com.example.schoolalarmservice.crawling.model.UnivStatus;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.aggregator.ArgumentAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@DataJpaTest
public class UserUnivRepositoryTest {
    @Autowired private UserRepository userRepository;
    @Autowired private UnivRepository univRepository;
    @Autowired private UserUnivRepository userUnivRepository;

    private Univ konkukUniv;

    @BeforeEach
    void dataSetting() {
        // 1. 유저 생성 및 저장
        User user1 = new User();
        user1.setTeleId(111L); // String 리턴 타입에 맞게 세팅
        userRepository.save(user1);

        User user2 = new User();
        user2.setTeleId(222L);
        userRepository.save(user2);

        // 2. 대학교 생성 및 저장
        konkukUniv = new Univ();
        konkukUniv.setUnivName("건국대학교");
        univRepository.save(konkukUniv);

        // 3. 매핑 데이터(UserUniv) 생성 및 저장
        UserUniv userUniv1 = new UserUniv();
        userUniv1.setUser(user1);
        userUniv1.setUniv(konkukUniv);
        userUniv1.setIsActive(UnivStatus.ENROLLED);
        userUnivRepository.save(userUniv1);

        UserUniv userUniv2 = new UserUniv();
        userUniv2.setUser(user2);
        userUniv2.setUniv(konkukUniv);
        userUniv2.setIsActive(UnivStatus.ENROLLED); // 취소된 회원
        userUnivRepository.save(userUniv2);
    }


    @Test
    @Transactional
    public void 구독중인_회원_teleID_조회(){
        //given


        //when
        List<String> result = userUnivRepository.findChatIdsByUnivId(konkukUniv.getId());

        //then
        Assertions.assertThat(result).hasSize(2);
        org.junit.jupiter.api.Assertions.assertTrue(result.contains("111"), "리스트에 올바른 값이 포함됨");
        org.junit.jupiter.api.Assertions.assertTrue(result.contains("222"), "리스트에 올바른 값이 포함됨");
    }
}