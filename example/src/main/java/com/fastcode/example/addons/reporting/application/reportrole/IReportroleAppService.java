package com.fastcode.example.addons.reporting.application.reportrole;

import com.fastcode.example.addons.reporting.application.reportrole.dto.*;
import com.fastcode.example.addons.reporting.domain.report.Report;
import com.fastcode.example.addons.reporting.domain.reportrole.QReportrole;
import com.fastcode.example.addons.reporting.domain.reportrole.ReportroleId;
import com.fastcode.example.commons.search.SearchCriteria;
import com.fastcode.example.commons.search.SearchFields;
import com.fastcode.example.domain.core.authorization.role.Role;
import com.querydsl.core.BooleanBuilder;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface IReportroleAppService {
    public CreateReportroleOutput create(CreateReportroleInput reportrole);

    public void delete(ReportroleId reportroleId);

    public Boolean addReportsToRole(Role role, List<Report> reportsList);

    public UpdateReportroleOutput update(ReportroleId reportroleId, UpdateReportroleInput input);

    public FindReportroleByIdOutput findById(ReportroleId reportroleId);

    public List<FindReportroleByIdOutput> find(SearchCriteria search, Pageable pageable) throws Exception;

    public BooleanBuilder search(SearchCriteria search) throws Exception;

    public void checkProperties(List<String> list) throws Exception;

    public BooleanBuilder searchKeyValuePair(
        QReportrole reportrole,
        Map<String, SearchFields> map,
        Map<String, String> joinColumns
    );

    public ReportroleId parseReportroleKey(String keysString);

    //Report
    public GetReportOutput getReport(ReportroleId reportroleId);
}
