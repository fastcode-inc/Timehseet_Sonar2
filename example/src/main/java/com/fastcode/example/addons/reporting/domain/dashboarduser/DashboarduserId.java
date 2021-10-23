package com.fastcode.example.addons.reporting.domain.dashboarduser;

import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DashboarduserId implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long dashboardId;
    private Long userId;

    public DashboarduserId(Long dashboardId, Long userId) {
        super();
        this.dashboardId = dashboardId;
        this.userId = userId;
    }
}
