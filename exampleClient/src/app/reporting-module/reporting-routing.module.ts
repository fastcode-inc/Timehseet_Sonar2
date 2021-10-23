import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { SchemaComponent } from './pages/schema/schema.component';
import { GenerateReportComponent } from './pages/myreports/generate-report/generate-reports.component';
import { EditDashboardComponent } from './pages/dashboard/editdashboard/editdashboard.component';
import { PreviewComponent } from './pages/dashboard/preview/preview.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { MyreportsMainComponent } from './pages/myreports/myreports-main/myreports-main.component';
import { ReportDetailsComponent } from './pages/myreports/report-details/report-details.component';
import { AuthGuard } from 'src/app/core/guards/auth.guard';
import { DashboardMainComponent } from './pages/dashboard/dashboard-main/dashboard-main.component';
import { ShareComponent } from './pages/share/share.component';
const routes: Routes = [
  {
    path: '',
    canActivate: [AuthGuard],
    children: [
      { path: 'schema', component: SchemaComponent },
      //{ path: 'dashboards', component: DashboardMainComponent },
      { path: 'reports/:id', component: GenerateReportComponent },
      {
        path: 'myreports',
        component: MyreportsMainComponent,
      },
      { path: 'myreports/:id', component: ReportDetailsComponent },
      {
        path: 'dashboard',
        component: DashboardComponent,
        children: [
          { path: '', component: DashboardMainComponent },
          { path: 'edit/:id', component: EditDashboardComponent },
          { path: 'preview/:id', component: PreviewComponent },
        ],
      },
      { path: 'share', component: ShareComponent },
      { path: '', redirectTo: 'schema', pathMatch: 'full' },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ReportingRoutingModule {}
