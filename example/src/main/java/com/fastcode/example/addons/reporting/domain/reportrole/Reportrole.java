package com.fastcode.example.addons.reporting.domain.reportrole;

import com.fastcode.example.addons.reporting.domain.report.Report;
import com.fastcode.example.domain.core.abstractentity.AbstractEntity;
import com.fastcode.example.domain.core.authorization.role.Role;
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
@Table(name = "reportrole")
@IdClass(ReportroleId.class)
public class Reportrole extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "report_id", nullable = false)
    private Long reportId;

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @Basic
    @Column(name = "editable", nullable = false)
    private Boolean editable;

    @Basic
    @Column(name = "owner_sharing_status", nullable = false)
    private Boolean ownerSharingStatus;

    @ManyToOne
    @JoinColumn(name = "report_id", insertable = false, updatable = false)
    private Report report;

    @ManyToOne
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    private Role role;
}
