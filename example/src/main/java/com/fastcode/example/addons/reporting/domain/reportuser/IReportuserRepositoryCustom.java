package com.fastcode.example.addons.reporting.domain.reportuser;

import com.fastcode.example.domain.core.authorization.users.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IReportuserRepositoryCustom {
    Page<Users> getAvailableReportusersList(Long reportId, String search, Pageable pageable);

    Page<Users> getReportusersList(Long reportId, String search, Pageable pageable);
}
