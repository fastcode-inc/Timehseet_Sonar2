package com.fastcode.example.addons.reporting.domain.reportversion;

import java.time.*;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository("reportversionRepository")
public interface IReportversionRepository
    extends JpaRepository<Reportversion, ReportversionId>, QuerydslPredicateExecutor<Reportversion> {
    @Query("select r from Reportversion r where r.users.id=?1  and r.reportVersion = ?2")
    Reportversion findByReportversionAndUsersId(Long userId, String version);

    @Query("select r from Reportversion r where r.users.id=?1  and r.report.id = ?2 and r.reportVersion = ?3")
    Reportversion findByReportIdAndVersionAndUsersId(Long userId, Long reportId, String version);

    @Query("select r from Reportversion r where r.users.id=?1 ")
    List<Reportversion> findByUsersId(Long userId);
}
