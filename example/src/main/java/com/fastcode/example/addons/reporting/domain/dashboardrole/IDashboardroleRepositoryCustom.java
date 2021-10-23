package com.fastcode.example.addons.reporting.domain.dashboardrole;

import com.fastcode.example.domain.core.authorization.role.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IDashboardroleRepositoryCustom {
    Page<Role> getAvailableDashboardrolesList(Long dashboardId, String search, Pageable pageable);

    Page<Role> getDashboardrolesList(Long dashboardId, String search, Pageable pageable);
}
