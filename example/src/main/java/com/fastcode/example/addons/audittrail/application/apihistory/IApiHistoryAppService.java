package com.fastcode.example.addons.audittrail.application.apihistory;

import com.fastcode.example.addons.audittrail.domain.model.ApiHistoryEntity;
import com.fastcode.example.commons.search.SearchCriteria;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface IApiHistoryAppService {
    ApiHistoryEntity create(ApiHistoryEntity types);

    ApiHistoryEntity update(Integer id, ApiHistoryEntity input);

    ApiHistoryEntity findById(Integer id);

    List<ApiHistoryEntity> find(SearchCriteria search, Pageable pageable) throws Exception;
}
