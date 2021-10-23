package com.fastcode.example.addons.reporting.domain.reportuser;

import com.fastcode.example.domain.core.authorization.users.Users;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository("reportuserRepositoryCustomImpl")
@SuppressWarnings({ "unchecked", "rawtypes" })
public class IReportuserRepositoryCustomImpl implements IReportuserRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private Environment env;

    @Override
    public Page<Users> getAvailableReportusersList(Long reportId, String search, Pageable pageable) {
        String schema = env.getProperty("spring.jpa.properties.hibernate.default_schema");
        String qlString = String.format(
            "" +
            "SELECT * " +
            "FROM %s.users e WHERE " +
            " e.id NOT IN " +
            "    (SELECT " +
            " user_id" +
            "     FROM %s.reportuser ru" +
            "     WHERE report_id = :reportId " +
            "       AND ru.owner_sharing_status = TRUE ) " +
            "  AND e.id NOT IN " +
            "    (SELECT " +
            " owner_id" +
            "     FROM %s.report " +
            "     WHERE id = :reportId)" +
            "  AND (cast(:search as varchar) IS NULL " +
            "       OR e.username ILIKE cast(:search as varchar))",
            schema,
            schema,
            schema
        );

        Query query = entityManager
            .createNativeQuery(qlString, Users.class)
            .setParameter("reportId", reportId)
            .setParameter("search", "%" + search + "%")
            .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
            .setMaxResults(pageable.getPageSize());
        List results = query.getResultList();
        int totalRows = results.size();

        Page<Users> result = new PageImpl<Users>(results, pageable, totalRows);

        return result;
    }

    @Override
    public Page<Users> getReportusersList(Long reportId, String search, Pageable pageable) {
        String schema = env.getProperty("spring.jpa.properties.hibernate.default_schema");
        String qlString = String.format(
            "" +
            "SELECT * " +
            "FROM %s.users e WHERE " +
            " e.id IN " +
            "    (SELECT " +
            "user_id" +
            "     FROM %s.reportuser ru" +
            "     WHERE report_id = :reportId " +
            "       AND ru.owner_sharing_status = TRUE ) " +
            "  AND (cast(:search as varchar) IS NULL " +
            "       OR e.username ILIKE cast(:search as varchar))",
            schema,
            schema
        );

        Query query = entityManager
            .createNativeQuery(qlString, Users.class)
            .setParameter("reportId", reportId)
            .setParameter("search", "%" + search + "%")
            .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
            .setMaxResults(pageable.getPageSize());

        List results = query.getResultList();
        int totalRows = results.size();

        Page<Users> result = new PageImpl<Users>(results, pageable, totalRows);

        return result;
    }
}
