package com.fastcode.example.addons.reporting.domain.dashboardrole;

import com.fastcode.example.domain.core.authorization.role.Role;
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

@Repository("dashboardroleRepositoryCustomImpl")
@SuppressWarnings({ "unchecked", "rawtypes" })
public class IDashboardroleRepositoryCustomImpl implements IDashboardroleRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private Environment env;

    @Override
    public Page<Role> getAvailableDashboardrolesList(Long dashboardId, String search, Pageable pageable) {
        String schema = env.getProperty("spring.jpa.properties.hibernate.default_schema");
        String qlString = String.format(
            "" +
            "SELECT * " +
            "FROM %s.role r " +
            "WHERE r.id NOT IN " +
            "    (SELECT role_id " +
            "     FROM %s.dashboardrole " +
            "     WHERE dashboard_id = :dashboardId " +
            "       AND owner_sharing_status = true) " +
            "  AND (cast(:search as varchar) IS NULL " +
            "       OR r.name ILIKE cast(:search as varchar))",
            schema,
            schema
        );

        Query query = entityManager
            .createNativeQuery(qlString, Role.class)
            .setParameter("dashboardId", dashboardId)
            .setParameter("search", "%" + search + "%")
            .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
            .setMaxResults(pageable.getPageSize());
        List results = query.getResultList();
        int totalRows = results.size();

        Page<Role> result = new PageImpl<Role>(results, pageable, totalRows);

        return result;
    }

    @Override
    public Page<Role> getDashboardrolesList(Long dashboardId, String search, Pageable pageable) {
        String schema = env.getProperty("spring.jpa.properties.hibernate.default_schema");
        String qlString = String.format(
            "" +
            "SELECT * " +
            "FROM %s.role r " +
            "WHERE r.id IN " +
            "    (SELECT role_id " +
            "     FROM %s.dashboardrole " +
            "     WHERE dashboard_id = :dashboardId " +
            "       AND owner_sharing_status = true) " +
            "  AND (cast(:search as varchar) IS NULL " +
            "       OR r.name ILIKE cast(:search as varchar))",
            schema,
            schema
        );

        Query query = entityManager
            .createNativeQuery(qlString, Role.class)
            .setParameter("dashboardId", dashboardId)
            .setParameter("search", "%" + search + "%")
            .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
            .setMaxResults(pageable.getPageSize());

        List results = query.getResultList();
        int totalRows = results.size();

        Page<Role> result = new PageImpl<Role>(results, pageable, totalRows);

        return result;
    }
}
