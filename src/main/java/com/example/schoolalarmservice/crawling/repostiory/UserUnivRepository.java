package com.example.schoolalarmservice.crawling.repostiory;


import com.example.schoolalarmservice.crawling.entity.Univ;
import com.example.schoolalarmservice.crawling.entity.User;
import com.example.schoolalarmservice.crawling.entity.UserUniv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserUnivRepository extends JpaRepository<UserUniv, Long> {
    @Query(
            "select us.teleId from UserUniv uu "+
                    "join uu.user us "+
                    "where uu.univ.id  = :univId "+
                    "and uu.isActive = 'ENROLLED'"
    )
    List<Long> findChatIdsByUnivId(@Param("univId") Long univId);


    Boolean existsByUserAndUniv(User user, Univ univ);

    List<UserUniv> findAllByUser(User user);

    Optional<UserUniv> findByUserAndUniv(User user, Univ univ);
}
