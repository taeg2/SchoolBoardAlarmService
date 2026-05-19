package com.example.schoolalarmservice.crawling.repostiory;


import com.example.schoolalarmservice.crawling.entity.UserUniv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserUnivRepository extends JpaRepository<UserUniv, Long> {
    @Query(
            "select us.teleId from UserUniv uu "+
                    "join uu.user us "+
                    "where uu.univ.id  = :univId "+
                    "and uu.isActive = 'ENROLLED'"
    )
    List<String> findChatIdsByUnivId(@Param("univId") Long univId);
}
