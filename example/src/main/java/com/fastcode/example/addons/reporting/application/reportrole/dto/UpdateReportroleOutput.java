package com.fastcode.example.addons.reporting.application.reportrole.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateReportroleOutput {

    private Long roleId;
    private Long reportId;
    private String roleDescriptiveField;
}
