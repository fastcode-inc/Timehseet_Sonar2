package com.fastcode.example.addons.reporting.application.dashboardrole;

import com.fastcode.example.addons.reporting.application.dashboardrole.dto.*;
import com.fastcode.example.addons.reporting.domain.dashboard.Dashboard;
import com.fastcode.example.addons.reporting.domain.dashboardrole.Dashboardrole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface IDashboardroleMapper {
    Dashboardrole createDashboardroleInputToDashboardrole(CreateDashboardroleInput dashboardroleDto);

    @Mappings(
        {
            @Mapping(source = "roleId", target = "roleId"),
            @Mapping(source = "dashboardId", target = "dashboardId"),
            @Mapping(source = "role.name", target = "roleDescriptiveField"),
            //	@Mapping(source = "dashboard.title", target = "dashboardDescriptiveField"),
        }
    )
    CreateDashboardroleOutput dashboardroleToCreateDashboardroleOutput(Dashboardrole entity);

    Dashboardrole updateDashboardroleInputToDashboardrole(UpdateDashboardroleInput dashboardroleDto);

    @Mappings(
        {
            @Mapping(source = "roleId", target = "roleId"),
            @Mapping(source = "dashboardId", target = "dashboardId"),
            @Mapping(source = "role.name", target = "roleDescriptiveField"),
            //	@Mapping(source = "dashboard.title", target = "dashboardDescriptiveField"),
        }
    )
    UpdateDashboardroleOutput dashboardroleToUpdateDashboardroleOutput(Dashboardrole entity);

    @Mappings(
        {
            @Mapping(source = "roleId", target = "roleId"),
            @Mapping(source = "dashboardId", target = "dashboardId"),
            @Mapping(source = "role.name", target = "roleDescriptiveField"),
            //	@Mapping(source = "dashboard.title", target = "dashboardDescriptiveField"),
        }
    )
    FindDashboardroleByIdOutput dashboardroleToFindDashboardroleByIdOutput(Dashboardrole entity);

    @Mappings(
        {
            @Mapping(source = "dashboardrole.roleId", target = "dashboardroleRoleId"),
            //	@Mapping(source = "dashboardrole.dashboardId", target = "dashboardroleDashboardId"),
        }
    )
    GetDashboardOutput dashboardToGetDashboardOutput(Dashboard dashboard, Dashboardrole dashboardrole);
}
