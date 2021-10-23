package com.fastcode.example.addons.audittrail.domain.irepository;

import com.fastcode.example.addons.audittrail.domain.model.ApiHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface IApiHistoryRepository
    extends JpaRepository<ApiHistoryEntity, Integer>, QuerydslPredicateExecutor<ApiHistoryEntity> {
    ApiHistoryEntity getByCorrelation(String correlation);
}
