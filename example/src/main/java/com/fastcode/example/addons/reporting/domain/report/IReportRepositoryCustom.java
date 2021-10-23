package com.fastcode.example.addons.reporting.domain.report;

import com.fastcode.example.addons.reporting.application.report.dto.ReportDetailsOutput;
import java.time.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IReportRepositoryCustom {
    Page<ReportDetailsOutput> getAllReportsByUsersId(Long userId, String search, Pageable pageable) throws Exception;

    Page<ReportDetailsOutput> getSharedReportsByUsersId(Long userId, String search, Pageable pageable) throws Exception;
}
