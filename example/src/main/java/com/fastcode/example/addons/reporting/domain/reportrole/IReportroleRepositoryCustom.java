package com.fastcode.example.addons.reporting.domain.reportrole;

import com.fastcode.example.domain.core.authorization.role.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IReportroleRepositoryCustom {
    Page<Role> getAvailableReportrolesList(Long reportId, String search, Pageable pageable);

    Page<Role> getReportrolesList(Long reportId, String search, Pageable pageable);
}
