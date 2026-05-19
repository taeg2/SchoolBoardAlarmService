package com.example.schoolalarmservice.crawling.repostiory;


import com.example.schoolalarmservice.crawling.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
