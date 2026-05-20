package com.example.schoolalarmservice.crawling.repostiory;

import com.example.schoolalarmservice.crawling.entity.Univ;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UnivRepository extends JpaRepository<Univ, Long> {

    Optional<Univ> findByUnivName(String univName);
}
