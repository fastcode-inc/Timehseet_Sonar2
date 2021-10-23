package com.fastcode.example.addons.reporting.application.permalink;

import com.fastcode.example.addons.reporting.application.permalink.dto.*;
import com.fastcode.example.commons.search.SearchCriteria;
import com.querydsl.core.BooleanBuilder;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface IPermalinkAppService {
    public CreatePermalinkOutput create(CreatePermalinkInput permalink);

    public void delete(UUID id);

    public UpdatePermalinkOutput update(UUID id, UpdatePermalinkInput input);

    public FindPermalinkByIdOutput findById(UUID id);

    public List<FindPermalinkByIdOutput> find(SearchCriteria search, Pageable pageable) throws Exception;

    public BooleanBuilder search(SearchCriteria search) throws Exception;
}
