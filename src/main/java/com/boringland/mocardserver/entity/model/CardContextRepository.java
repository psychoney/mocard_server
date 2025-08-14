package com.boringland.mocardserver.entity.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;


@Repository
public interface CardContextRepository extends JpaRepository<CardContext, Integer>, JpaSpecificationExecutor<CardContext>{
    List<CardContext> findByPidOrderByOrderIndex(Integer cardId);
}
