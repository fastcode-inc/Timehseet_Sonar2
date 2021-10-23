package com.fastcode.example.addons.reporting.domain.reportrole;

import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReportroleId implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long reportId;
    private Long roleId;

    public ReportroleId(Long reportId, Long roleId) {
        super();
        this.reportId = reportId;
        this.roleId = roleId;
    }
}
