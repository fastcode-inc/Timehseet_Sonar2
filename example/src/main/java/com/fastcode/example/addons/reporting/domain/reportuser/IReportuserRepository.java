package com.fastcode.example.addons.reporting.domain.reportuser;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("reportuserRepository")
public interface IReportuserRepository
    extends
        JpaRepository<Reportuser, ReportuserId>, QuerydslPredicateExecutor<Reportuser>, IReportuserRepositoryCustom {
    @Query("select r from Reportuser r where r.users.id=?1")
    List<Reportuser> findByUsersId(Long userId);

    @Query("select r from Reportuser r where r.report.id = ?1")
    List<Reportuser> findByReportId(Long id);

    @Transactional
    @Modifying
    @Query("UPDATE Reportuser r SET r.isRefreshed = ?1 WHERE r.reportId = ?2")
    List<Reportuser> updateRefreshFlag(Boolean refresh, Long reportId);

    @Transactional
    @Modifying
    @Query("UPDATE Reportuser r SET r.ownerSharingStatus = ?1 WHERE r.reportId = ?2")
    List<Reportuser> updateOwnerSharingStatus(Boolean status, Long reportId);
}
