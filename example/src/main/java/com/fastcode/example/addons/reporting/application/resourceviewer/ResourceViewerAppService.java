package com.fastcode.example.addons.reporting.application.resourceviewer;

import com.fastcode.example.addons.reporting.application.dashboard.IDashboardAppService;
import com.fastcode.example.addons.reporting.application.dashboard.dto.FindDashboardByIdOutput;
import com.fastcode.example.addons.reporting.application.permalink.dto.*;
import com.fastcode.example.addons.reporting.application.report.IReportAppService;
import com.fastcode.example.addons.reporting.application.report.dto.FindReportByIdOutput;
import com.fastcode.example.addons.reporting.application.resourceviewer.dto.ResourceOutput;
import com.fastcode.example.application.extended.authorization.users.IUsersAppServiceExtended;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class ResourceViewerAppService implements IResourceViewerAppService {

    @Autowired
    @Qualifier("usersAppServiceExtended")
    private IUsersAppServiceExtended _usersAppService;

    @Autowired
    private PasswordEncoder pEncoder;

    @Autowired
    @Qualifier("reportAppService")
    private IReportAppService _reportAppService;

    @Autowired
    @Qualifier("dashboardAppService")
    private IDashboardAppService _dashboardAppService;

    public ResourceOutput getData(FindPermalinkByIdOutput permalink) {
        ResourceOutput data = new ResourceOutput();
        data.setResourceInfo(permalink);

        if (permalink.getResource().equals("report")) {
            FindReportByIdOutput report = _reportAppService.findById(permalink.getResourceId());
            if (report == null) {
                return null;
            }
            data.setData(report);
        } else if (permalink.getResource().equals("dashboard")) {
            FindDashboardByIdOutput dashboard = _dashboardAppService.findById(permalink.getResourceId());
            if (dashboard == null) {
                return null;
            }
            dashboard.setReportDetails(
                _dashboardAppService.setReportsList(permalink.getResourceId(), permalink.getUserId(), "published")
            );
            data.setData(dashboard);
        }
        return data;
    }

    public boolean isAuthorized(FindPermalinkByIdOutput output, String password) {
        if (output.getAuthentication().equals("login")) {
            if (_usersAppService.getUsers() == null) {
                return false;
            }
        } else if (output.getAuthentication().equals("password") && (!pEncoder.matches(password, output.getPassword()))) {
                return false;
        }

        return true;
    }
}
