package org.example.web_mng_authentication.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserInfo, Long>, UserRepositoryCustom {
    Optional<UserInfo> findByUserId(String userId);
    Page<UserInfo> findByUserName(String userId, Pageable pageable);
    @Query(value = "select ui1_0.userNo, ui1_0.emailAddr, ui1_0.lastLoginDate, ui1_0.loginFailCount, ui1_0.refreshToken," +
            "        ui1_0.userId, ui1_0.userName, ui1_0.userPassword, ui1_0.userStateCode " +
            "    from UserInfo ui1_0 " +
            "    where ui1_0.userId=:userId")
    UserInfo findByUser(@Param("userId") String userId);


}
