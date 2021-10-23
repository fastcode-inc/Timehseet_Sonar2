package com.fastcode.example.addons.reporting.domain.report;

import java.time.*;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository("reportRepository")
public interface IReportRepository
    extends JpaRepository<Report, Long>, QuerydslPredicateExecutor<Report>, IReportRepositoryCustom {
    @Query("select r from Report r where r.id = ?1 and r.users.id=?2 ")
    Report findByReportIdAndUsersId(Long reportId, Long userId);

    @Query("select r from Report r where r.users.id=?1")
    List<Report> findByUsersId(Long id);
}
