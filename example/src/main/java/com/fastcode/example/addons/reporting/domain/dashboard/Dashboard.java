package com.fastcode.example.addons.reporting.domain.dashboard;

import com.fastcode.example.addons.reporting.domain.dashboardrole.Dashboardrole;
import com.fastcode.example.addons.reporting.domain.dashboarduser.Dashboarduser;
import com.fastcode.example.addons.reporting.domain.dashboardversion.Dashboardversion;
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
@Table(name = "dashboard")
public class Dashboard extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Basic
    @Column(name = "is_published", nullable = false)
    private Boolean isPublished;

    @Basic
    @Column(name = "is_shareable", nullable = false)
    private Boolean isShareable;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Users users;

    @OneToMany(mappedBy = "dashboard", cascade = CascadeType.ALL)
    private Set<Dashboardversion> dashboardversionSet = new HashSet<Dashboardversion>();

    @OneToMany(mappedBy = "dashboard", cascade = CascadeType.ALL)
    private Set<Dashboarduser> dashboarduserSet = new HashSet<Dashboarduser>();

    @OneToMany(mappedBy = "dashboard", cascade = CascadeType.ALL)
    private Set<Dashboardrole> dashboardroleSet = new HashSet<Dashboardrole>();

    public void addDashboardrole(Dashboardrole dashboardrole) {
        dashboardroleSet.add(dashboardrole);
        dashboardrole.setDashboard(this);
    }

    public void removeDashboardrole(Dashboardrole dashboardrole) {
        dashboardroleSet.remove(dashboardrole);
        dashboardrole.setDashboard(null);
    }

    public void addDashboarduser(Dashboarduser dashboarduser) {
        dashboarduserSet.add(dashboarduser);
        dashboarduser.setDashboard(this);
    }

    public void removeDashboarduser(Dashboarduser dashboarduser) {
        dashboarduserSet.remove(dashboarduser);
        dashboarduser.setDashboard(null);
    }

    public void addDashboardversion(Dashboardversion dashboardversion) {
        dashboardversionSet.add(dashboardversion);
        dashboardversion.setDashboard(this);
    }

    public void removeDashboardversion(Dashboardversion dashboardversion) {
        dashboardversionSet.remove(dashboardversion);
        dashboardversion.setDashboard(null);
    }
}
