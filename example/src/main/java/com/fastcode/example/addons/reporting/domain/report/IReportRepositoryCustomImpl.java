package com.fastcode.example.addons.reporting.domain.report;

import com.fastcode.example.addons.reporting.application.report.dto.ReportDetailsOutput;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository("reportRepositoryCustomImpl")
@SuppressWarnings({ "unchecked" })
public class IReportRepositoryCustomImpl implements IReportRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private Environment env;

    @Override
    public Page<ReportDetailsOutput> getAllReportsByUsersId(Long userId, String search, Pageable pageable)
        throws Exception {
        String schema = env.getProperty("spring.jpa.properties.hibernate.default_schema");

        String qlString =
            "" +
            "SELECT rep.id, rep.report_version, rep.ctype, rep.description, rep.query, rep.report_type, rep.title, rep.is_published, rep.is_assigned_by_dashboard,rep.shared_with_others, rep.shared_with_me," +
            "       ru.is_resetted, " +
            "       ru.owner_sharing_status, " +
            "       ru.recipient_sharing_status, " +
            "       ru.editable, " +
            "       ru.is_assigned_by_role, " +
            "       ru.is_refreshed, " +
            " us.username," +
            "rep.user_id, rep.owner_id " +
            " FROM ((" +
            schema +
            ".reportuser ru " +
            " RIGHT OUTER JOIN " +
            "  (SELECT rv.*, r.id, r.is_published," +
            "r.owner_id," +
            "          (CASE " +
            "               WHEN rv.report_id NOT IN " +
            "                      (SELECT report_id " +
            "                       FROM " +
            schema +
            ".reportuser ru " +
            "                       WHERE ru.report_id = rv.report_id) THEN 0 " +
            "               ELSE 1 " +
            "           END) AS shared_with_others, " +
            "          (CASE " +
            "               WHEN " +
            "                      (SELECT count(*) " +
            "                       FROM " +
            schema +
            ".reportuser ru WHERE" +
            " 				ru.user_id =rv.user_id" +
            "                         AND ru.report_id = rv.report_id) >0 THEN 1 " +
            "               ELSE 0 " +
            "           END) AS shared_with_me " +
            "   FROM " +
            schema +
            ".report r, " +
            schema +
            ".reportversion rv " +
            "   WHERE rv.report_id = r.id " +
            "   AND rv.user_id = :userId" +
            "     AND (cast(:search as varchar) is null OR rv.title like cast(:search as varchar)) " +
            "     AND rv.report_version = 'running' AND rv.is_assigned_by_dashboard = false ) AS rep ON ru.report_id = rep.id " +
            " and ru.user_id = rep.user_id" +
            ") JOIN  ( " +
            " SELECT id  ,username FROM " +
            schema +
            ".users where id = :userId ) AS us ON us.id = rep.user_id )" +
            " WHERE (ru.recipient_sharing_status IS NULL OR ru.recipient_sharing_status=true)";

        Query query = entityManager
            .createNativeQuery(qlString)
            .setParameter("userId", userId)
            .setParameter("search", "%" + search + "%")
            .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
            .setMaxResults(pageable.getPageSize());
        List<Object[]> results = query.getResultList();
        List<ReportDetailsOutput> finalResults = new ArrayList<>();

        for (Object[] obj : results) {
            ReportDetailsOutput reportDetails = new ReportDetailsOutput();

            // Here you manually obtain value from object and map to your pojo setters
            reportDetails.setId(obj[0] != null ? Long.parseLong(obj[0].toString()) : null);
            reportDetails.setReportVersion(obj[1] != null ? (obj[1].toString()) : null);
            reportDetails.setCtype(obj[2] != null ? (obj[2].toString()) : null);
            reportDetails.setDescription(obj[3] != null ? (obj[3].toString()) : null);

            JSONParser parser = new JSONParser();
            JSONObject json;
            try {
                json = (JSONObject) parser.parse(obj[4].toString());
                reportDetails.setQuery(json);
            } catch (ParseException e) {
                e.printStackTrace();
                throw new Exception("Error occured while parsing query");
            }

            reportDetails.setReportType(obj[5] != null ? (obj[5].toString()) : null);
            reportDetails.setTitle(obj[6] != null ? (obj[6].toString()) : null);

            reportDetails.setIsPublished(obj[7] != null && obj[7].toString().equals("true"));
            reportDetails.setIsAssignedByDashboard(obj[8] != null && obj[8].toString().equals("true"));
            reportDetails.setSharedWithOthers(Integer.parseInt(obj[9].toString()) == 0 ? false : true);
            reportDetails.setSharedWithMe(Integer.parseInt(obj[10].toString()) == 0 ? false : true);

            reportDetails.setIsResetted(obj[11] != null && obj[11].toString().equals("true"));
            reportDetails.setOwnerSharingStatus(obj[12] != null && obj[12].toString().equals("true"));
            reportDetails.setRecipientSharingStatus(obj[13] != null && obj[13].toString().equals("true"));
            reportDetails.setEditable(obj[14] != null && obj[14].toString().equals("true") ? true : false);
            reportDetails.setIsAssignedByRole(obj[15] != null && obj[15].toString().equals("true"));
            reportDetails.setIsRefreshed(obj[16] != null && obj[16].toString().equals("true"));
            reportDetails.setOwnerDescriptiveField(obj[17] != null ? (obj[17].toString()) : null);
            reportDetails.setUserId(obj[18] != null ? Long.parseLong(obj[18].toString()) : null);
            reportDetails.setOwnerId(obj[19] != null ? Long.parseLong(obj[19].toString()) : null);

            finalResults.add(reportDetails);
        }

        int totalRows = results.size();
        Page<ReportDetailsOutput> result = new PageImpl<ReportDetailsOutput>(finalResults, pageable, totalRows);

        return result;
    }

    @Override
    public Page<ReportDetailsOutput> getSharedReportsByUsersId(Long userId, String search, Pageable pageable)
        throws Exception {
        String schema = env.getProperty("spring.jpa.properties.hibernate.default_schema");

        String qlString =
            "SELECT rv.report_id, rv.report_version, rv.ctype, rv.description, rv.is_assigned_by_dashboard, rv.query, rv.report_type, rv.title," +
            "u.editable," +
            "u.is_assigned_by_role," +
            "u.is_refreshed," +
            "u.is_resetted," +
            "u.owner_sharing_status," +
            "u.recipient_sharing_status," +
            "rv.user_id, r.owner_id " +
            " FROM " +
            schema +
            ".reportversion rv, " +
            schema +
            ".reportuser u, " +
            schema +
            ".report r " +
            "WHERE rv.report_id IN " +
            "(SELECT report_id" +
            " FROM " +
            schema +
            ".reportuser WHERE" +
            " user_id =rv.user_id and " +
            " rv.report_id= report_id) " +
            " and rv.report_id = u.report_id " +
            " and rv.report_id = r.id " +
            " and rv.user_id =:userId " +
            " and rv.report_version = 'running' " +
            "AND rv.is_assigned_by_dashboard = false " +
            "AND " +
            "(cast(:search as varchar) is null OR rv.title like cast(:search as varchar))";

        Query query = entityManager
            .createNativeQuery(qlString)
            .setParameter("userId", userId)
            .setParameter("search", "%" + search + "%")
            .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
            .setMaxResults(pageable.getPageSize());
        List<Object[]> results = query.getResultList();
        List<ReportDetailsOutput> finalResults = new ArrayList<>();
        for (Object[] obj : results) {
            ReportDetailsOutput reportDetails = new ReportDetailsOutput();

            // Here you manually obtain value from object and map to your pojo setters
            reportDetails.setId(obj[0] != null ? Long.parseLong(obj[0].toString()) : null);
            reportDetails.setReportVersion(obj[1] != null ? (obj[1].toString()) : null);

            reportDetails.setCtype(obj[2] != null ? (obj[2].toString()) : null);
            reportDetails.setDescription(obj[3] != null ? (obj[3].toString()) : null);
            reportDetails.setIsAssignedByDashboard(obj[4] != null && obj[4].toString().equals("true"));

            JSONParser parser = new JSONParser();
            JSONObject json;
            try {
                json = (JSONObject) parser.parse(obj[5].toString());
                reportDetails.setQuery(json);
            } catch (ParseException e) {
                e.printStackTrace();
                throw new Exception("Error occured while parsing query");
            }

            reportDetails.setReportType(obj[6] != null ? (obj[6].toString()) : null);
            reportDetails.setTitle(obj[7] != null ? (obj[7].toString()) : null);
            reportDetails.setEditable(obj[8].toString().equals("true"));
            reportDetails.setIsAssignedByRole(obj[9].toString().equals("true"));
            reportDetails.setIsRefreshed(obj[10].toString().equals("true"));
            reportDetails.setIsResetted(obj[11].toString().equals("true"));
            reportDetails.setOwnerSharingStatus(obj[12].toString().equals("true"));
            reportDetails.setRecipientSharingStatus(obj[13].toString().equals("true"));
            reportDetails.setSharedWithMe(true);
            reportDetails.setUserId(obj[14] != null ? Long.parseLong(obj[14].toString()) : null);
            reportDetails.setOwnerId(obj[15] != null ? Long.parseLong(obj[15].toString()) : null);

            finalResults.add(reportDetails);
        }

        int totalRows = results.size();
        Page<ReportDetailsOutput> result = new PageImpl<ReportDetailsOutput>(finalResults, pageable, totalRows);

        return result;
    }
}
