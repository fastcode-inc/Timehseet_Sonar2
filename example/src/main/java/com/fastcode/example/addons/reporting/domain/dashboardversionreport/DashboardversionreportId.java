package com.fastcode.example.addons.reporting.domain.dashboardversionreport;

import java.io.Serializable;
import java.time.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DashboardversionreportId implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long dashboardId;
    private Long userId;
    private String dashboardVersion;
    private Long reportId;

    public DashboardversionreportId(Long dashboardId, Long userId, String dashboardVersion, Long reportId) {
        this.dashboardId = dashboardId;
        this.userId = userId;
        this.dashboardVersion = dashboardVersion;
        this.reportId = reportId;
    }
}
