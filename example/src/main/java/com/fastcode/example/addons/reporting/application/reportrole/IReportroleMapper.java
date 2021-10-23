package com.fastcode.example.addons.reporting.application.reportrole;

import com.fastcode.example.addons.reporting.application.reportrole.dto.*;
import com.fastcode.example.addons.reporting.domain.report.Report;
import com.fastcode.example.addons.reporting.domain.reportrole.Reportrole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface IReportroleMapper {
    Reportrole createReportroleInputToReportrole(CreateReportroleInput reportroleDto);

    @Mappings(
        {
            @Mapping(source = "roleId", target = "roleId"),
            @Mapping(source = "reportId", target = "reportId"),
            @Mapping(source = "role.name", target = "roleDescriptiveField"),
        }
    )
    CreateReportroleOutput reportroleToCreateReportroleOutput(Reportrole entity);

    Reportrole updateReportroleInputToReportrole(UpdateReportroleInput reportroleDto);

    @Mappings(
        {
            @Mapping(source = "roleId", target = "roleId"),
            @Mapping(source = "reportId", target = "reportId"),
            @Mapping(source = "role.name", target = "roleDescriptiveField"),
        }
    )
    UpdateReportroleOutput reportroleToUpdateReportroleOutput(Reportrole entity);

    @Mappings(
        {
            @Mapping(source = "roleId", target = "roleId"),
            @Mapping(source = "reportId", target = "reportId"),
            @Mapping(source = "role.name", target = "roleDescriptiveField"),
        }
    )
    FindReportroleByIdOutput reportroleToFindReportroleByIdOutput(Reportrole entity);

    @Mappings(
        {
            @Mapping(source = "reportrole.roleId", target = "reportroleRoleId"),
            @Mapping(source = "reportrole.reportId", target = "reportroleReportId"),
        }
    )
    GetReportOutput reportToGetReportOutput(Report report, Reportrole reportrole);
}
