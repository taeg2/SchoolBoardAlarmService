package com.example.schoolalarmservice.crawling.repostiory;


import com.example.schoolalarmservice.crawling.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsByTeleId(Long teleId);


    Optional<User> findByTeleId(Long teleId);
}
