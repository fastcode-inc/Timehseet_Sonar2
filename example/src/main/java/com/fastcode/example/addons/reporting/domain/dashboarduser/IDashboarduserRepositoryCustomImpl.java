package com.fastcode.example.addons.reporting.domain.dashboarduser;

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

@Repository("dashboarduserRepositoryCustomImpl")
@SuppressWarnings({ "unchecked", "rawtypes" })
public class IDashboarduserRepositoryCustomImpl implements IDashboarduserRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private Environment env;

    @Override
    public Page<Users> getAvailableDashboardusersList(Long dashboardId, String search, Pageable pageable) {
        String schema = env.getProperty("spring.jpa.properties.hibernate.default_schema");
        String qlString = String.format(
            "" +
            "SELECT * " +
            " FROM %s.users e WHERE " +
            " e.id NOT IN " +
            "    (SELECT " +
            "user_id" +
            " FROM %s.dashboarduser " +
            "WHERE dashboard_id = :dashboardId " +
            "AND owner_sharing_status = TRUE ) " +
            "  AND e.id NOT IN " +
            "    (SELECT " +
            "owner_id" +
            "     FROM %s.dashboard " +
            "     WHERE id = :dashboardId)" +
            "  AND (cast(:search as varchar) IS NULL " +
            "       OR e.username ILIKE cast(:search as varchar))",
            schema,
            schema,
            schema
        );

        Query query = entityManager
            .createNativeQuery(qlString, Users.class)
            .setParameter("dashboardId", dashboardId)
            .setParameter("search", "%" + search + "%")
            .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
            .setMaxResults(pageable.getPageSize());
        List results = query.getResultList();
        int totalRows = results.size();

        Page<Users> result = new PageImpl<Users>(results, pageable, totalRows);

        return result;
    }

    @Override
    public Page<Users> getDashboardusersList(Long dashboardId, String search, Pageable pageable) {
        String schema = env.getProperty("spring.jpa.properties.hibernate.default_schema");
        String qlString = String.format(
            "" +
            "SELECT * " +
            " FROM %s.users e WHERE " +
            " e.id IN " +
            "    (SELECT " +
            " user_id" +
            "     FROM %s.dashboarduser du" +
            "     WHERE dashboard_id = :dashboardId " +
            "       AND du.owner_sharing_status = TRUE ) " +
            "  AND (cast(:search as varchar) IS NULL " +
            "       OR e.username ILIKE cast(:search as varchar))",
            schema,
            schema
        );

        Query query = entityManager
            .createNativeQuery(qlString, Users.class)
            .setParameter("dashboardId", dashboardId)
            .setParameter("search", "%" + search + "%")
            .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
            .setMaxResults(pageable.getPageSize());

        List results = query.getResultList();
        int totalRows = results.size();

        Page<Users> result = new PageImpl<Users>(results, pageable, totalRows);

        return result;
    }
}
