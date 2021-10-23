package com.fastcode.example.addons.reporting.domain.dashboard;

import com.fastcode.example.addons.reporting.application.dashboard.dto.DashboardDetailsOutput;
import java.time.*;
import java.util.ArrayList;
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

@Repository("dashboardRepositoryCustomImpl")
@SuppressWarnings({ "unchecked" })
public class IDashboardRepositoryCustomImpl implements IDashboardRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private Environment env;

    @Override
    public Page<DashboardDetailsOutput> getAllDashboardsByUsersId(Long userId, String search, Pageable pageable) {
        String schema = env.getProperty("spring.jpa.properties.hibernate.default_schema");
        String qlString = String.format(
            "" +
            "SELECT rep.id, rep.dashboard_version, rep.description, rep.title, rep.is_published, " +
            "rep.is_shareable,rep.shared_with_others, rep.shared_with_me," +
            "       ru.is_resetted, " +
            "       ru.owner_sharing_status, " +
            "       ru.recipient_sharing_status, " +
            "       ru.editable, " +
            "       ru.is_assigned_by_role, " +
            "       ru.is_refreshed, " +
            " us.username," +
            "rep.user_id, rep.owner_id " +
            " FROM ((%s.dashboarduser ru " +
            " RIGHT OUTER JOIN " +
            "  (SELECT rv.*, r.id, r.is_published, r.is_shareable," +
            "r.owner_id," +
            "          (CASE " +
            "               WHEN rv.dashboard_id NOT IN " +
            "                      (SELECT dashboard_id " +
            "                       FROM %s.dashboarduser ru " +
            "                       WHERE ru.dashboard_id = rv.dashboard_id) THEN 0 " +
            "               ELSE 1 " +
            "           END) AS shared_with_others, " +
            "          (CASE " +
            "               WHEN " +
            "                      (SELECT count(*)" +
            "                       FROM %s.dashboarduser ru WHERE" +
            " 				ru.user_id =rv.user_id " +
            "                         AND ru.dashboard_id = rv.dashboard_id) > 0 THEN 1 " +
            "               ELSE 0 " +
            "           END) AS shared_with_me " +
            "   FROM %s.dashboard r, " +
            "        %s.dashboardversion rv " +
            "   WHERE rv.dashboard_id = r.id " +
            "   AND rv.user_id = :userId" +
            "     AND (cast(:search as varchar) is null OR rv.title like cast(:search as varchar)) " +
            "     AND rv.dashboard_version = 'running' ) AS rep ON ru.dashboard_id = rep.id " +
            " and ru.user_id = rep.user_id" +
            " ) JOIN  ( " +
            " SELECT id  ,username FROM %s.users where id = :userId ) AS us ON us.id = rep.user_id )" +
            " WHERE (ru.recipient_sharing_status IS NULL OR ru.recipient_sharing_status=true)",
            schema,
            schema,
            schema,
            schema,
            schema,
            schema
        );
        Query query = entityManager
            .createNativeQuery(qlString)
            .setParameter("userId", userId)
            .setParameter("search", "%" + search + "%")
            .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
            .setMaxResults(pageable.getPageSize());
        List<Object[]> results = query.getResultList();
        List<DashboardDetailsOutput> finalResults = new ArrayList<>();

        for (Object[] obj : results) {
            DashboardDetailsOutput dashboardDetails = new DashboardDetailsOutput();

            // Here you manually obtain value from object and map to your pojo setters
            dashboardDetails.setId(obj[0] != null ? Long.parseLong(obj[0].toString()) : null);
            dashboardDetails.setDashboardVersion(obj[1] != null ? (obj[1].toString()) : null);
            dashboardDetails.setDescription(obj[2] != null ? (obj[2].toString()) : null);
            dashboardDetails.setTitle(obj[3] != null ? (obj[3].toString()) : null);

            dashboardDetails.setIsPublished(obj[4] != null && obj[4].toString().equals("true"));
            dashboardDetails.setIsShareable(obj[5] != null && obj[5].toString().equals("true"));
            dashboardDetails.setSharedWithOthers(Integer.parseInt(obj[6].toString()) == 0 ? false : true);
            dashboardDetails.setSharedWithMe(Integer.parseInt(obj[7].toString()) == 0 ? false : true);

            dashboardDetails.setIsResetted(obj[8] != null && obj[8].toString().equals("true"));
            dashboardDetails.setOwnerSharingStatus(obj[9] != null && obj[9].toString().equals("true"));
            dashboardDetails.setRecipientSharingStatus(obj[10] != null && obj[10].toString().equals("true"));
            dashboardDetails.setEditable(obj[11] != null && obj[11].toString().equals("true"));
            dashboardDetails.setIsAssignedByRole(obj[12] != null && obj[12].toString().equals("true"));
            dashboardDetails.setIsRefreshed(obj[13] != null && obj[13].toString().equals("true"));
            dashboardDetails.setOwnerDescriptiveField(obj[14] != null ? (obj[14].toString()) : null);

            dashboardDetails.setUserId(obj[15] != null ? Long.parseLong(obj[15].toString()) : null);
            dashboardDetails.setOwnerId(obj[16] != null ? Long.parseLong(obj[16].toString()) : null);
            finalResults.add(dashboardDetails);
        }
        int totalRows = results.size();

        Page<DashboardDetailsOutput> result = new PageImpl<DashboardDetailsOutput>(finalResults, pageable, totalRows);

        return result;
    }

    @Override
    public Page<DashboardDetailsOutput> getSharedDashboardsByUsersId(Long userId, String search, Pageable pageable)
        throws Exception {
        String schema = env.getProperty("spring.jpa.properties.hibernate.default_schema");

        String qlString =
            "SELECT rv.dashboard_id, rv.dashboard_version, rv.description, rv.title," +
            "u.editable," +
            "u.is_assigned_by_role," +
            "u.is_refreshed," +
            "u.is_resetted," +
            "u.owner_sharing_status," +
            "u.recipient_sharing_status," +
            "r.is_shareable, " +
            "				 rv.user_id, r.owner_id" +
            " FROM " +
            schema +
            ".dashboardversion rv, " +
            schema +
            ".dashboarduser u, " +
            schema +
            ".dashboard r " +
            "WHERE rv.dashboard_id IN " +
            "(SELECT dashboard_id " +
            "FROM " +
            schema +
            ".dashboarduser WHERE" +
            " user_id =rv.user_id and" +
            " rv.dashboard_id= dashboard_id) " +
            " and rv.dashboard_id = u.dashboard_id " +
            " and rv.dashboard_id = r.id " +
            " and rv.user_id =:userId" +
            " and rv.dashboard_version = 'running' " +
            "AND " +
            "(cast(:search as varchar) is null OR rv.title ilike cast(:search as varchar))";

        Query query = entityManager
            .createNativeQuery(qlString)
            .setParameter("userId", userId)
            .setParameter("search", "%" + search + "%")
            .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
            .setMaxResults(pageable.getPageSize());
        List<Object[]> results = query.getResultList();
        List<DashboardDetailsOutput> finalResults = new ArrayList<>();
        for (Object[] obj : results) {
            DashboardDetailsOutput dashboardDetails = new DashboardDetailsOutput();

            // Here you manually obtain value from object and map to your pojo setters
            dashboardDetails.setId(obj[0] != null ? Long.parseLong(obj[0].toString()) : null);
            dashboardDetails.setDashboardVersion(obj[1] != null ? (obj[1].toString()) : null);
            dashboardDetails.setDescription(obj[2] != null ? (obj[2].toString()) : null);
            dashboardDetails.setTitle(obj[3] != null ? (obj[3].toString()) : null);
            dashboardDetails.setEditable(obj[4].toString().equals("true"));
            dashboardDetails.setIsAssignedByRole(obj[5].toString().equals("true"));
            dashboardDetails.setIsRefreshed(obj[6].toString().equals("true"));
            dashboardDetails.setIsResetted(obj[7].toString().equals("true"));
            dashboardDetails.setOwnerSharingStatus(obj[8].equals("true"));
            dashboardDetails.setRecipientSharingStatus(obj[9].toString().equals("true"));
            dashboardDetails.setIsShareable(obj[10].toString().equals("true"));
            dashboardDetails.setSharedWithMe(true);
            dashboardDetails.setUserId(obj[11] != null ? Long.parseLong(obj[11].toString()) : null);
            dashboardDetails.setOwnerId(obj[12] != null ? Long.parseLong(obj[12].toString()) : null);
            finalResults.add(dashboardDetails);
        }

        int totalRows = results.size();

        Page<DashboardDetailsOutput> result = new PageImpl<DashboardDetailsOutput>(finalResults, pageable, totalRows);

        return result;
    }

    @Override
    public Page<DashboardDetailsOutput> getAvailableDashboardsByUsersId(
        Long userId,
        Long reportId,
        String search,
        Pageable pageable
    ) {
        String schema = env.getProperty("spring.jpa.properties.hibernate.default_schema");
        String qlString =
            "" +
            "SELECT d.id, dv.dashboard_version, dv.description, dv.title , d.is_published, d.is_shareable, " +
            "				dv.user_id, d.owner_id" +
            " FROM " +
            schema +
            ".dashboard d, " +
            schema +
            ".dashboardversion dv " +
            "	WHERE dv.dashboard_id = d.id " +
            " AND dv.user_id =:userId " +
            "	 AND dv.dashboard_id NOT IN " +
            "	(SELECT dashboard_id AS id " +
            "	FROM " +
            schema +
            ".dashboardversionreport " +
            "	WHERE report_id = :reportId " +
            "	GROUP BY (report_id, dashboard_id)) " +
            "	AND dv.dashboard_id NOT IN " +
            "	(SELECT dashboard_id AS id FROM " +
            schema +
            ".dashboarduser WHERE " +
            " user_id =:userId AND " +
            " editable = false) AND dv.dashboard_version = 'running' " +
            "	   AND (cast(:search as varchar) is null OR dv.title like cast(:search as varchar))";
        Query query = entityManager
            .createNativeQuery(qlString)
            .setParameter("userId", userId)
            .setParameter("reportId", reportId)
            .setParameter("search", "%" + search + "%")
            .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
            .setMaxResults(pageable.getPageSize());
        List<Object[]> results = query.getResultList();
        List<DashboardDetailsOutput> finalResults = new ArrayList<>();

        for (Object[] obj : results) {
            DashboardDetailsOutput dashboardDetails = new DashboardDetailsOutput();

            // Here you manually obtain value from object and map to your pojo setters
            dashboardDetails.setId(obj[0] != null ? Long.parseLong(obj[0].toString()) : null);
            dashboardDetails.setDashboardVersion(obj[1] != null ? (obj[1].toString()) : null);
            dashboardDetails.setDescription(obj[2] != null ? (obj[2].toString()) : null);
            dashboardDetails.setTitle(obj[3] != null ? (obj[3].toString()) : null);

            dashboardDetails.setIsPublished(obj[4].toString().equals("true"));
            dashboardDetails.setIsShareable(obj[5].toString().equals("true"));
            dashboardDetails.setUserId(obj[6] != null ? Long.parseLong(obj[6].toString()) : null);
            dashboardDetails.setOwnerId(obj[7] != null ? Long.parseLong(obj[7].toString()) : null);
            finalResults.add(dashboardDetails);
        }

        int totalRows = results.size();
        Page<DashboardDetailsOutput> result = new PageImpl<DashboardDetailsOutput>(finalResults, pageable, totalRows);

        return result;
    }
}
