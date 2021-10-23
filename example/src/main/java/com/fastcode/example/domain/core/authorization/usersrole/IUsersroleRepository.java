package com.fastcode.example.domain.core.authorization.usersrole;

import java.time.*;
import java.util.*;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@JaversSpringDataAuditable
@Repository("usersroleRepository")
public interface IUsersroleRepository
    extends JpaRepository<Usersrole, UsersroleId>, QuerydslPredicateExecutor<Usersrole> {
    List<Usersrole> findByUsersId(Long usersId);

    List<Usersrole> findByRoleId(Long roleId);
}
