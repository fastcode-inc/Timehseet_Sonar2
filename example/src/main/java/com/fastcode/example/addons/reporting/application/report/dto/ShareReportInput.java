package com.fastcode.example.addons.reporting.application.report.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShareReportInput {

    private List<ShareReportInputByRole> rolesList;
    private List<ShareReportInputByUser> usersList;
}
