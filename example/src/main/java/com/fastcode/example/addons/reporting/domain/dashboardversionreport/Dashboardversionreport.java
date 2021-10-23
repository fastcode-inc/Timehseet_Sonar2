package com.fastcode.example.addons.reporting.domain.dashboardversionreport;

import com.fastcode.example.addons.reporting.domain.dashboardversion.Dashboardversion;
import com.fastcode.example.addons.reporting.domain.report.Report;
import com.fastcode.example.domain.core.abstractentity.AbstractEntity;
import java.time.*;
import javax.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "dashboardversionreport")
@IdClass(DashboardversionreportId.class)
public class Dashboardversionreport extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "dashboard_id", nullable = false)
    private Long dashboardId;

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "dashboard_version", nullable = false)
    private String dashboardVersion;

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "report_id", nullable = false)
    private Long reportId;

    @Basic
    @Column(name = "report_width", nullable = false, length = 255)
    private String reportWidth;

    @Basic
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @ManyToOne
    @JoinColumns(
        {
            @JoinColumn(
                name = "dashboard_id",
                referencedColumnName = "dashboard_id",
                insertable = false,
                updatable = false
            ),
            @JoinColumn(
                name = "dashboard_version",
                referencedColumnName = "dashboard_version",
                insertable = false,
                updatable = false
            ),
            @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false),
        }
    )
    private Dashboardversion dashboardversionEntity;

    @ManyToOne
    @JoinColumn(name = "report_id", insertable = false, updatable = false)
    private Report report;
}
