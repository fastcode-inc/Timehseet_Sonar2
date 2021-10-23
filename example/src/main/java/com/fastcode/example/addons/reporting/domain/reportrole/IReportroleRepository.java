package com.fastcode.example.addons.reporting.domain.reportrole;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository("reportroleRepository")
public interface IReportroleRepository
    extends
        JpaRepository<Reportrole, ReportroleId>, QuerydslPredicateExecutor<Reportrole>, IReportroleRepositoryCustom {
    @Query("select r from Reportrole r where r.role.id = ?1")
    List<Reportrole> findByRoleId(Long id);
}
