package com.socialuni.social.sdk.repository;

import com.socialuni.social.entity.model.DO.AppConfigDO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppConfigRepository extends JpaRepository<AppConfigDO, Integer> {
    List<AppConfigDO> findAllByStatusOrderByCreateTimeDesc(String status);
}

