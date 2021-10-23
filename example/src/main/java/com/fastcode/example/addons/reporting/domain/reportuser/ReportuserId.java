package com.fastcode.example.addons.reporting.domain.reportuser;

import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReportuserId implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long reportId;
    private Long userId;

    public ReportuserId(Long reportId, Long userId) {
        super();
        this.reportId = reportId;
        this.userId = userId;
    }
}
