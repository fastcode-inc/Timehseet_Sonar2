package com.fastcode.example.addons.reporting.application.dashboarduser;

import com.fastcode.example.addons.reporting.application.dashboarduser.dto.*;
import com.fastcode.example.addons.reporting.domain.dashboard.Dashboard;
import com.fastcode.example.addons.reporting.domain.dashboarduser.DashboarduserId;
import com.fastcode.example.addons.reporting.domain.dashboarduser.QDashboarduser;
import com.fastcode.example.commons.search.SearchCriteria;
import com.fastcode.example.commons.search.SearchFields;
import com.fastcode.example.domain.core.authorization.users.Users;
import com.querydsl.core.BooleanBuilder;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface IDashboarduserAppService {
    public CreateDashboarduserOutput create(CreateDashboarduserInput dashboarduser);

    public void delete(DashboarduserId dashboarduserId);

    public Boolean addDashboardsToUsers(Users users, List<Dashboard> dashboardsList);

    public UpdateDashboarduserOutput update(DashboarduserId dashboarduserId, UpdateDashboarduserInput input);

    public FindDashboarduserByIdOutput findById(DashboarduserId dashboarduserId);

    public List<FindDashboarduserByIdOutput> find(SearchCriteria search, Pageable pageable) throws Exception;

    public GetDashboardOutput getDashboard(DashboarduserId dashboarduserId);

    public BooleanBuilder search(SearchCriteria search) throws Exception;

    public void checkProperties(List<String> list) throws Exception;

    public BooleanBuilder searchKeyValuePair(
        QDashboarduser dashboarduser,
        Map<String, SearchFields> map,
        Map<String, String> joinColumns
    );

    public DashboarduserId parseDashboarduserKey(String keysString);
}
