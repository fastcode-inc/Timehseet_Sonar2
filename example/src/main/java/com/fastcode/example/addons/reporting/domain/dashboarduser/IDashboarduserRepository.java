package com.fastcode.example.addons.reporting.domain.dashboarduser;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("dashboarduserRepository")
public interface IDashboarduserRepository
    extends
        JpaRepository<Dashboarduser, DashboarduserId>,
        QuerydslPredicateExecutor<Dashboarduser>,
        IDashboarduserRepositoryCustom {
    @Query("select r from Dashboarduser r where r.users.id=?1")
    List<Dashboarduser> findByUsersId(Long id);

    @Query("select r from Dashboarduser r where r.dashboard.id = ?1")
    List<Dashboarduser> findByDashboardId(Long id);

    @Transactional
    @Modifying
    @Query("UPDATE Dashboarduser r SET r.isRefreshed = ?1 WHERE r.dashboardId = ?2")
    List<Dashboarduser> updateRefreshFlag(Boolean refresh, Long dashboardId);

    @Transactional
    @Modifying
    @Query("UPDATE Dashboarduser r SET r.ownerSharingStatus = ?1 WHERE r.dashboardId = ?2")
    List<Dashboarduser> updateOwnerSharingStatus(Boolean status, Long dashboardId);
}
