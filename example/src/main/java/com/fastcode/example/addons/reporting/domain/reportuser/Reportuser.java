package com.fastcode.example.addons.reporting.domain.reportuser;

import com.fastcode.example.addons.reporting.domain.report.Report;
import com.fastcode.example.domain.core.abstractentity.AbstractEntity;
import com.fastcode.example.domain.core.authorization.users.Users;
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
@Table(name = "reportuser")
@IdClass(ReportuserId.class)
public class Reportuser extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "report_id", nullable = false)
    private Long reportId;

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Basic
    @Column(name = "editable", nullable = false)
    private Boolean editable;

    @Basic
    @Column(name = "is_resetted", nullable = false)
    private Boolean isResetted;

    @Basic
    @Column(name = "is_refreshed", nullable = false)
    private Boolean isRefreshed;

    @Basic
    @Column(name = "owner_sharing_status", nullable = false)
    private Boolean ownerSharingStatus;

    @Basic
    @Column(name = "recipient_sharing_status", nullable = false)
    private Boolean recipientSharingStatus;

    @Basic
    @Column(name = "is_assigned_by_role", nullable = false)
    private Boolean isAssignedByRole;

    @ManyToOne
    @JoinColumn(name = "report_id", insertable = false, updatable = false)
    private Report report;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Users users;
}
