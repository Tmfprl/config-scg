package org.example.web_mng_authentication.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserInfo, Long>{
    Optional<UserInfo> findByUserId(String userId);
    Page<UserInfo> findByUserName(String userId, Pageable pageable);

}
