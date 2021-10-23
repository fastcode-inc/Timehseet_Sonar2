package com.fastcode.example.addons.reporting.domain.permalink;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository("permalinkRepository")
public interface IPermalinkRepository extends JpaRepository<Permalink, UUID>, QuerydslPredicateExecutor<Permalink> {}
