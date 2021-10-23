package com.fastcode.example.domain.core.authorization.rolepermission;

import java.time.*;
import java.util.*;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@JaversSpringDataAuditable
@Repository("rolepermissionRepository")
public interface IRolepermissionRepository
    extends JpaRepository<Rolepermission, RolepermissionId>, QuerydslPredicateExecutor<Rolepermission> {
    List<Rolepermission> findByRoleId(Long value);
}
