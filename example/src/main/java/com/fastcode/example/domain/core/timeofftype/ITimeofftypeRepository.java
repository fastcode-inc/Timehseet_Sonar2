package com.fastcode.example.domain.core.timeofftype;

import java.time.*;
import java.util.*;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@JaversSpringDataAuditable
@Repository("timeofftypeRepository")
public interface ITimeofftypeRepository
    extends JpaRepository<Timeofftype, Long>, QuerydslPredicateExecutor<Timeofftype> {}
