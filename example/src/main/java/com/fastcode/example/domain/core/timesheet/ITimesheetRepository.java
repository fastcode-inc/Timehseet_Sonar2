package com.fastcode.example.domain.core.timesheet;

import java.time.*;
import java.util.*;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@JaversSpringDataAuditable
@Repository("timesheetRepository")
public interface ITimesheetRepository extends JpaRepository<Timesheet, Long>, QuerydslPredicateExecutor<Timesheet> {}
