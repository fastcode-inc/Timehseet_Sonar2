package com.fastcode.example.addons.reporting.domain.dashboardversion;

import com.fastcode.example.addons.reporting.domain.dashboard.Dashboard;
import com.fastcode.example.addons.reporting.domain.dashboardversionreport.Dashboardversionreport;
import com.fastcode.example.domain.core.abstractentity.AbstractEntity;
import com.fastcode.example.domain.core.authorization.users.Users;
import java.time.*;
import java.util.HashSet;
import java.util.Set;
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
@Table(name = "dashboardversion")
@IdClass(DashboardversionId.class)
public class Dashboardversion extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "dashboard_id", nullable = false)
    private Long dashboardId;

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "dashboard_version", nullable = false, length = 255)
    private String dashboardVersion;

    @Basic
    @Column(name = "description", nullable = true, length = 255)
    private String description;

    @Basic
    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Basic
    @Column(name = "is_refreshed", nullable = true)
    private Boolean isRefreshed;

    @ManyToOne
    @JoinColumn(name = "dashboard_id", insertable = false, updatable = false)
    private Dashboard dashboard;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Users users;

    @OneToMany(mappedBy = "dashboardversionEntity", cascade = CascadeType.ALL)
    private Set<Dashboardversionreport> dashboardversionreportSet = new HashSet<Dashboardversionreport>();

    public void addDashboardversionreport(Dashboardversionreport dashboardversionreport) {
        dashboardversionreportSet.add(dashboardversionreport);
        dashboardversionreport.setDashboardversionEntity(this);
    }

    public void removeDashboardversionreport(Dashboardversionreport dashboardversionreport) {
        dashboardversionreportSet.remove(dashboardversionreport);
        dashboardversionreport.setDashboardversionEntity(null);
    }
}
