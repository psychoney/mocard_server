package com.boringland.mocardserver.entity.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
public interface UserFewshotRepository extends JpaRepository<UserFewshot, Integer>, JpaSpecificationExecutor<UserFewshot>{
    List<UserFewshot> findByDeviceIdAndDeleteFlagOrderByCreateTimeDesc(String deviceId, Integer deleteFlag);

    @Modifying
    @Transactional
    @Query("UPDATE UserFewshot u SET u.deleteFlag = 1 WHERE u.deviceId = ?1")
    int setDeleteFlagForDeviceId(String deviceId);
}
