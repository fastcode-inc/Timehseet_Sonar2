package com.fastcode.example.addons.reporting.application.report;

import com.fastcode.example.addons.reporting.application.report.dto.*;
import com.fastcode.example.addons.reporting.application.reportversion.dto.CreateReportversionInput;
import com.fastcode.example.addons.reporting.application.reportversion.dto.CreateReportversionOutput;
import com.fastcode.example.addons.reporting.application.reportversion.dto.UpdateReportversionInput;
import com.fastcode.example.addons.reporting.application.reportversion.dto.UpdateReportversionOutput;
import com.fastcode.example.addons.reporting.domain.report.Report;
import com.fastcode.example.addons.reporting.domain.reportuser.Reportuser;
import com.fastcode.example.addons.reporting.domain.reportversion.Reportversion;
import com.fastcode.example.domain.core.authorization.role.Role;
import com.fastcode.example.domain.core.authorization.users.Users;
import java.time.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface IReportMapper {
    Report createReportInputToReport(CreateReportInput reportDto);

    @Mappings({ @Mapping(source = "ownerId", target = "userId") })
    CreateReportversionInput createReportInputToCreateReportversionInput(CreateReportInput reportDto);

    @Mappings(
        {
            @Mapping(source = "entity.id", target = "id"),
            @Mapping(source = "entity.users.id", target = "ownerId"),
            @Mapping(source = "entity.users.username", target = "ownerDescriptiveField"),
        }
    )
    CreateReportOutput reportAndCreateReportversionOutputToCreateReportOutput(
        Report entity,
        CreateReportversionOutput reportversionOutput
    );

    @Mappings(
        {
            @Mapping(source = "report.id", target = "id"),
            @Mapping(source = "report.users.id", target = "ownerId"),
            @Mapping(source = "report.users.username", target = "ownerDescriptiveField"),
        }
    )
    CreateReportOutput reportAndReportversionToCreateReportOutput(Report report, Reportversion reportversion);

    @Mappings({ @Mapping(source = "role.id", target = "id"), @Mapping(source = "report.id", target = "reportId") })
    GetRoleOutput roleToGetRoleOutput(Role role, Report report);

    @Mappings(
        {
            @Mapping(source = "reportuser.users.username", target = "ownerDescriptiveField"),
            @Mapping(source = "reportuser.isRefreshed", target = "isRefreshed"),
        }
    )
    FindReportByIdOutput createReportOutputToFindReportByIdOutput(CreateReportOutput report, Reportuser reportuser);

    Report updateReportInputToReport(UpdateReportInput reportDto);

    UpdateReportversionInput updateReportInputToUpdateReportversionInput(UpdateReportInput reportDto);

    @Mappings(
        {
            @Mapping(source = "entity.id", target = "id"),
            @Mapping(source = "entity.users.id", target = "ownerId"),
            @Mapping(source = "entity.users.username", target = "ownerDescriptiveField"),
        }
    )
    UpdateReportOutput reportAndUpdateReportversionOutputToUpdateReportOutput(
        Report entity,
        UpdateReportversionOutput reportversion
    );

    @Mappings(
        {
            @Mapping(source = "reportversion.userId", target = "userId"),
            @Mapping(source = "report.users.id", target = "ownerId"),
            @Mapping(source = "reportuser.isRefreshed", target = "isRefreshed"),
            @Mapping(source = "reportversion.reportId", target = "id"),
            @Mapping(source = "report.versiono", target = "versiono"),
        }
    )
    FindReportByIdOutput reportEntitiesToFindReportByIdOutput(
        Report report,
        Reportversion reportversion,
        Reportuser reportuser
    );

    @Mappings(
        {
            @Mapping(source = "entity.users.id", target = "ownerId"),
            @Mapping(source = "entity.versiono", target = "versiono"),
        }
    )
    FindReportByIdOutput reportToFindReportByIdOutput(Report entity, Reportversion reportversion);

    @Mappings({ @Mapping(source = "users.id", target = "id"), @Mapping(source = "report.id", target = "reportId") })
    GetUsersOutput usersToGetUsersOutput(Users users, Report report);

    @Mappings(
        {
            @Mapping(source = "reportversion.userId", target = "userId"),
            @Mapping(source = "report.users.id", target = "ownerId"),
            @Mapping(source = "reportuser.isRefreshed", target = "isRefreshed"),
            @Mapping(source = "reportversion.reportId", target = "id"),
        }
    )
    ReportDetailsOutput reportEntitiesToReportDetailsOutput(
        Report report,
        Reportversion reportversion,
        Reportuser reportuser
    );
}
