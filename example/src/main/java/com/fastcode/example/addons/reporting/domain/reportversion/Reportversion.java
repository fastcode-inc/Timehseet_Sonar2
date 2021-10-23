package com.fastcode.example.addons.reporting.domain.reportversion;

import com.fastcode.example.addons.reporting.JSONObjectConverter;
import com.fastcode.example.addons.reporting.domain.report.Report;
import com.fastcode.example.domain.core.abstractentity.AbstractEntity;
import com.fastcode.example.domain.core.authorization.users.Users;
import java.time.*;
import javax.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.simple.JSONObject;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "reportversion")
@IdClass(ReportversionId.class)
public class Reportversion extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "report_id", nullable = false)
    private Long reportId;

    @Basic
    @Column(name = "ctype", nullable = true, length = 255)
    private String ctype;

    @Basic
    @Column(name = "description", nullable = true, length = 255)
    private String description;

    @Column(columnDefinition = "TEXT", name = "query", nullable = true, length = 255)
    @Convert(converter = JSONObjectConverter.class)
    private JSONObject query;

    @Basic
    @Column(name = "report_type", nullable = true, length = 255)
    private String reportType;

    @Basic
    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Basic
    @Column(name = "is_refreshed", nullable = true)
    private Boolean isRefreshed;

    @Id
    @Column(name = "report_version", nullable = false, length = 255)
    private String reportVersion;

    @Basic
    @Column(name = "is_assigned_by_dashboard", nullable = false)
    private Boolean isAssignedByDashboard;

    @ManyToOne
    @JoinColumn(name = "report_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Report report;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Users users;
}
