package com.fastcode.example.addons.reporting.domain.dashboardrole;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository("dashboardroleRepository")
public interface IDashboardroleRepository
    extends
        JpaRepository<Dashboardrole, DashboardroleId>,
        QuerydslPredicateExecutor<Dashboardrole>,
        IDashboardroleRepositoryCustom {
    @Query("select r from Dashboardrole r where r.role.id = ?1")
    List<Dashboardrole> findByRoleId(Long id);
}
