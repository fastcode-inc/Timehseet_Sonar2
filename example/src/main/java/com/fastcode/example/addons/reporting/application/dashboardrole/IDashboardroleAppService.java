package com.fastcode.example.addons.reporting.application.dashboardrole;

import com.fastcode.example.addons.reporting.application.dashboardrole.dto.*;
import com.fastcode.example.addons.reporting.domain.dashboard.Dashboard;
import com.fastcode.example.addons.reporting.domain.dashboardrole.DashboardroleId;
import com.fastcode.example.addons.reporting.domain.dashboardrole.QDashboardrole;
import com.fastcode.example.commons.search.SearchCriteria;
import com.fastcode.example.commons.search.SearchFields;
import com.fastcode.example.domain.core.authorization.role.Role;
import com.querydsl.core.BooleanBuilder;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface IDashboardroleAppService {
    public CreateDashboardroleOutput create(CreateDashboardroleInput dashboardrole);

    public Boolean addDashboardsToRole(Role role, List<Dashboard> dashboardsList);

    public void delete(DashboardroleId dashboardroleId);

    public UpdateDashboardroleOutput update(DashboardroleId dashboardroleId, UpdateDashboardroleInput input);

    public FindDashboardroleByIdOutput findById(DashboardroleId dashboardroleId);

    public List<FindDashboardroleByIdOutput> find(SearchCriteria search, Pageable pageable) throws Exception;

    public BooleanBuilder search(SearchCriteria search) throws Exception;

    public void checkProperties(List<String> list) throws Exception;

    public BooleanBuilder searchKeyValuePair(
        QDashboardrole dashboardrole,
        Map<String, SearchFields> map,
        Map<String, String> joinColumns
    );

    public DashboardroleId parseDashboardroleKey(String keysString);

    //Dashboard
    public GetDashboardOutput getDashboard(DashboardroleId dashboardroleId);
}
