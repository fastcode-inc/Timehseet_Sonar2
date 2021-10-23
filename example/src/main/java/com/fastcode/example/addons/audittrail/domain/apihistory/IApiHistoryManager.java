package com.fastcode.example.addons.audittrail.domain.apihistory;

import com.fastcode.example.addons.audittrail.domain.model.ApiHistoryEntity;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IApiHistoryManager {
    // CRUD Operations
    ApiHistoryEntity create(ApiHistoryEntity apiHistory);

    ApiHistoryEntity update(ApiHistoryEntity apiHistory);

    ApiHistoryEntity findById(Integer id);

    Page<ApiHistoryEntity> findAll(Predicate predicate, Pageable pageable);
}
