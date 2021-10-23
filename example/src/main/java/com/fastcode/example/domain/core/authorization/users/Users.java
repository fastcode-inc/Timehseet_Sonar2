package com.fastcode.example.domain.core.authorization.users;

import com.fastcode.example.addons.reporting.domain.dashboard.Dashboard;
import com.fastcode.example.addons.reporting.domain.dashboardversion.Dashboardversion;
import com.fastcode.example.addons.reporting.domain.report.Report;
import com.fastcode.example.addons.reporting.domain.reportversion.Reportversion;
import com.fastcode.example.domain.core.abstractentity.AbstractEntity;
import com.fastcode.example.domain.core.authorization.tokenverification.Tokenverification;
import com.fastcode.example.domain.core.authorization.userspermission.Userspermission;
import com.fastcode.example.domain.core.authorization.userspreference.Userspreference;
import com.fastcode.example.domain.core.authorization.usersrole.Usersrole;
import com.fastcode.example.domain.core.timesheet.Timesheet;
import com.fastcode.example.domain.core.usertask.Usertask;
import com.querydsl.core.annotations.Config;
import java.time.*;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.TypeDefs;
import org.javers.core.metamodel.annotation.ShallowReference;

@Entity
@Config(defaultVariableName = "usersEntity")
@Table(name = "users")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@TypeDefs({})
public class Users extends AbstractEntity {

    @Basic
    @Column(name = "emailaddress", nullable = false, length = 255)
    private String emailaddress;

    @Basic
    @Column(name = "firstname", nullable = false, length = 255)
    private String firstname;

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Basic
    @Column(name = "isactive", nullable = false)
    private Boolean isactive;

    @Basic
    @Column(name = "isemailconfirmed", nullable = false)
    private Boolean isemailconfirmed;

    @Basic
    @Column(name = "join_date", nullable = true)
    private LocalDate joinDate;

    @Basic
    @Column(name = "lastname", nullable = false, length = 255)
    private String lastname;

    @Basic
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Basic
    @Column(name = "username", nullable = false, length = 255)
    private String username;

    @ShallowReference
    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Dashboard> dashboardsSet = new HashSet<Dashboard>();

    public void addDashboards(Dashboard dashboards) {
        dashboardsSet.add(dashboards);

        dashboards.setUsers(this);
    }

    public void removeDashboards(Dashboard dashboards) {
        dashboardsSet.remove(dashboards);

        dashboards.setUsers(null);
    }

    @ShallowReference
    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Dashboardversion> dashboardversionsSet = new HashSet<Dashboardversion>();

    public void addDashboardversions(Dashboardversion dashboardversions) {
        dashboardversionsSet.add(dashboardversions);

        dashboardversions.setUsers(this);
    }

    public void removeDashboardversions(Dashboardversion dashboardversions) {
        dashboardversionsSet.remove(dashboardversions);

        dashboardversions.setUsers(null);
    }

    @ShallowReference
    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Report> reportsSet = new HashSet<Report>();

    public void addReports(Report reports) {
        reportsSet.add(reports);

        reports.setUsers(this);
    }

    public void removeReports(Report reports) {
        reportsSet.remove(reports);

        reports.setUsers(null);
    }

    @ShallowReference
    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Reportversion> reportversionsSet = new HashSet<Reportversion>();

    public void addReportversions(Reportversion reportversions) {
        reportversionsSet.add(reportversions);

        reportversions.setUsers(this);
    }

    public void removeReportversions(Reportversion reportversions) {
        reportversionsSet.remove(reportversions);

        reportversions.setUsers(null);
    }

    @ShallowReference
    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Timesheet> timesheetsSet = new HashSet<Timesheet>();

    public void addTimesheets(Timesheet timesheets) {
        timesheetsSet.add(timesheets);

        timesheets.setUsers(this);
    }

    public void removeTimesheets(Timesheet timesheets) {
        timesheetsSet.remove(timesheets);

        timesheets.setUsers(null);
    }

    @ShallowReference
    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Tokenverification> tokenverificationsSet = new HashSet<Tokenverification>();

    public void addTokenverifications(Tokenverification tokenverifications) {
        tokenverificationsSet.add(tokenverifications);

        tokenverifications.setUsers(this);
    }

    public void removeTokenverifications(Tokenverification tokenverifications) {
        tokenverificationsSet.remove(tokenverifications);

        tokenverifications.setUsers(null);
    }

    @ShallowReference
    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Userspermission> userspermissionsSet = new HashSet<Userspermission>();

    public void addUserspermissions(Userspermission userspermissions) {
        userspermissionsSet.add(userspermissions);

        userspermissions.setUsers(this);
    }

    public void removeUserspermissions(Userspermission userspermissions) {
        userspermissionsSet.remove(userspermissions);

        userspermissions.setUsers(null);
    }

    @ShallowReference
    @OneToOne(mappedBy = "users", cascade = CascadeType.MERGE)
    private Userspreference userspreference;

    public void setUserspreference(Userspreference userspreference) {
        if (userspreference == null) {
            if (this.userspreference != null) {
                this.userspreference.setUsers(null);
            }
        } else {
            userspreference.setUsers(this);
        }
        this.userspreference = userspreference;
    }

    @ShallowReference
    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Usersrole> usersrolesSet = new HashSet<Usersrole>();

    public void addUsersroles(Usersrole usersroles) {
        usersrolesSet.add(usersroles);

        usersroles.setUsers(this);
    }

    public void removeUsersroles(Usersrole usersroles) {
        usersrolesSet.remove(usersroles);

        usersroles.setUsers(null);
    }

    @ShallowReference
    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Usertask> usertasksSet = new HashSet<Usertask>();

    public void addUsertasks(Usertask usertasks) {
        usertasksSet.add(usertasks);

        usertasks.setUsers(this);
    }

    public void removeUsertasks(Usertask usertasks) {
        usertasksSet.remove(usertasks);

        usertasks.setUsers(null);
    }
}
