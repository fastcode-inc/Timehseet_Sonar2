package com.fastcode.example.addons.reporting.domain.dashboardrole;

import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DashboardroleId implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long dashboardId;
    private Long roleId;

    public DashboardroleId(Long dashboardId, Long roleId) {
        super();
        this.dashboardId = dashboardId;
        this.roleId = roleId;
    }
}
