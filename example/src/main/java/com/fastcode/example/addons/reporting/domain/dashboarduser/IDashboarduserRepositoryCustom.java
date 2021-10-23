package com.fastcode.example.addons.reporting.domain.dashboarduser;

import com.fastcode.example.domain.core.authorization.users.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IDashboarduserRepositoryCustom {
    Page<Users> getAvailableDashboardusersList(Long dashboardId, String search, Pageable pageable);

    Page<Users> getDashboardusersList(Long dashboardId, String search, Pageable pageable);
}
