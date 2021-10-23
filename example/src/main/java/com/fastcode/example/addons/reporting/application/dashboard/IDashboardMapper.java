package com.fastcode.example.addons.reporting.application.dashboard;

import com.fastcode.example.addons.reporting.application.dashboard.dto.*;
import com.fastcode.example.addons.reporting.application.dashboardversion.dto.CreateDashboardversionInput;
import com.fastcode.example.addons.reporting.application.dashboardversion.dto.CreateDashboardversionOutput;
import com.fastcode.example.addons.reporting.application.dashboardversion.dto.UpdateDashboardversionInput;
import com.fastcode.example.addons.reporting.application.dashboardversion.dto.UpdateDashboardversionOutput;
import com.fastcode.example.addons.reporting.application.report.dto.CreateReportInput;
import com.fastcode.example.addons.reporting.domain.dashboard.Dashboard;
import com.fastcode.example.addons.reporting.domain.dashboarduser.Dashboarduser;
import com.fastcode.example.addons.reporting.domain.dashboardversion.Dashboardversion;
import com.fastcode.example.addons.reporting.domain.report.Report;
import com.fastcode.example.domain.core.authorization.role.Role;
import com.fastcode.example.domain.core.authorization.users.Users;
import java.time.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface IDashboardMapper {
    @Mappings({ @Mapping(source = "ownerId", target = "userId") })
    CreateDashboardversionInput creatDashboardInputToCreateDashboardversionInput(CreateDashboardInput dashboardDto);

    Dashboard createDashboardInputToDashboard(CreateDashboardInput dashboardDto);

    CreateDashboardInput addNewReportToNewDashboardInputTocreatDashboardInput(AddNewReportToNewDashboardInput input);

    CreateDashboardInput addExistingReportToNewDashboardInputTocreatDashboardInput(
        AddExistingReportToNewDashboardInput input
    );

    Dashboard addExistingReportToNewDashboardInputToDashboard(AddExistingReportToNewDashboardInput input);

    Report createDashboardAndReportInputToReport(CreateReportInput reportDto);

    @Mappings(
        {
            @Mapping(source = "entity.id", target = "id"),
            @Mapping(source = "entity.users.id", target = "ownerId"),
            @Mapping(source = "entity.users.username", target = "ownerDescriptiveField"),
        }
    )
    CreateDashboardOutput dashboardAndCreateDashboardversionOutputToCreateDashboardOutput(
        Dashboard entity,
        CreateDashboardversionOutput dashboardversion
    );

    @Mappings(
        {
            @Mapping(source = "dashboardversion.dashboardId", target = "id"),
            @Mapping(source = "dashboardversion.userId", target = "ownerId"),
            @Mapping(source = "dashboardversion.users.username", target = "ownerDescriptiveField"),
        }
    )
    CreateDashboardOutput dashboardAndDashboardversionToCreateDashboardOutput(
        Dashboard dashboard,
        Dashboardversion dashboardversion
    );

    UpdateDashboardversionInput updateDashboardInputToUpdateDashboardversionInput(UpdateDashboardInput dashboardDto);

    @Mappings(
        {
            @Mapping(source = "entity.id", target = "id"),
            @Mapping(source = "entity.users.id", target = "ownerId"),
            @Mapping(source = "entity.users.username", target = "ownerDescriptiveField"),
        }
    )
    UpdateDashboardOutput dashboardAndUpdateDashboardversionOutputToUpdateDashboardOutput(
        Dashboard entity,
        UpdateDashboardversionOutput dashboardversion
    );

    Dashboard updateDashboardInputToDashboard(UpdateDashboardInput dashboardDto);

    @Mappings({ @Mapping(source = "users.id", target = "ownerId") })
    UpdateDashboardOutput dashboardToUpdateDashboardOutput(Dashboard entity);

    @Mappings(
        {
            @Mapping(source = "users.id", target = "ownerId"),
            @Mapping(source = "users.username", target = "ownerDescriptiveField"),
        }
    )
    FindDashboardByIdOutput dashboardToFindDashboardByIdOutput(Dashboard entity);

    @Mappings({ @Mapping(source = "ownerId", target = "userId") })
    FindDashboardByIdOutput dashboardOutputToFindDashboardByIdOutput(CreateDashboardOutput entity);

    @Mappings(
        {
            @Mapping(source = "dashboardversion.userId", target = "userId"),
            @Mapping(source = "dashboard.users.id", target = "ownerId"),
            @Mapping(source = "dashboardversion.dashboardId", target = "id"),
            @Mapping(source = "dashboardversion.isRefreshed", target = "isRefreshed"),
            @Mapping(source = "dashboard.versiono", target = "versiono"),
        }
    )
    FindDashboardByIdOutput dashboardEntitiesToFindDashboardByIdOutput(
        Dashboard dashboard,
        Dashboardversion dashboardversion,
        Dashboarduser dashboarduser
    );

    @Mappings(
        {
            @Mapping(source = "dashboardversion.userId", target = "userId"),
            @Mapping(source = "dashboard.users.id", target = "ownerId"),
            @Mapping(source = "dashboardversion.isRefreshed", target = "isRefreshed"),
            @Mapping(source = "dashboardversion.dashboardId", target = "id"),
        }
    )
    DashboardDetailsOutput dashboardEntitiesToDashboardDetailsOutput(
        Dashboard dashboard,
        Dashboardversion dashboardversion,
        Dashboarduser dashboarduser
    );

    @Mappings(
        { @Mapping(source = "users.id", target = "id"), @Mapping(source = "dashboard.id", target = "dashboardId") }
    )
    GetUsersOutput usersToGetUsersOutput(Users users, Dashboard dashboard);

    @Mappings(
        { @Mapping(source = "role.id", target = "id"), @Mapping(source = "dashboard.id", target = "dashboardId") }
    )
    GetRoleOutput roleToGetRoleOutput(Role role, Dashboard dashboard);
}
