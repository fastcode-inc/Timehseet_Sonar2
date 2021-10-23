package com.fastcode.example.addons.reporting.domain.dashboardversion;

import java.time.*;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository("dashboardversionRepository")
public interface IDashboardversionRepository
    extends JpaRepository<Dashboardversion, DashboardversionId>, QuerydslPredicateExecutor<Dashboardversion> {
    @Query("select r from Dashboardversion r where r.id = ?1 and r.users.id=?2 ")
    Dashboardversion findByDashboardversionIdAndUsersId(Long dashboardversionId, Long userId);

    @Query("select r from Dashboardversion r where r.users.id=?1 ")
    List<Dashboardversion> findByUsersId(Long id);
}
