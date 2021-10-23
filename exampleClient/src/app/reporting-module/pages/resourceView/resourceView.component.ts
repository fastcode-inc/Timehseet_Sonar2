import { Component, OnInit } from '@angular/core';
import { AccessOptions } from '../myreports/myreports.component';
import { AuthenticationService } from 'src/app/core/services/authentication.service';
import { Router, ActivatedRoute } from '@angular/router';
import { ReportPasswordComponent } from '../myreports/report-password/report-password.component';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { ResourceViewService } from './resourceView.service';
import * as _ from 'lodash';

@Component({
  selector: 'app-resource-view',
  templateUrl: './resourceView.component.html',
  styleUrls: ['./resourceView.component.scss'],
})
export class ResourceViewComponent implements OnInit {
  resource: any;
  accessPassword: string;
  showResourceView = false;
  resourceViewId: any;
  selectedChart: string = '';
  query: any;
  queryParam: any;
  chartType: any;
  timeFilterBy: any;
  interval: any;

  passwordDialogRef: MatDialogRef<ReportPasswordComponent>;
  constructor(
    private authenticationService: AuthenticationService,
    private router: Router,
    public dialog: MatDialog,
    public resourceViewService: ResourceViewService,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.resourceViewId = this.route.snapshot.paramMap.get('id');
    this.resourceViewService.getResource(this.resourceViewId).subscribe((resource) => {
      if (resource.resourceInfo.resource == 'report') {
        this.selectedChart = resource.data.reportType;
      }
      this.resource = resource;
      this.buildQuery();
      this.interval = setInterval(this.checkreload, this.resource.resourceInfo.refreshRate * 1000);
      this.checkAccessOption();
    });
  }

  checkreload = () => {
    if (!this.resource) {
      this.interval.clear();
    }
    this.reload();
  };

  buildQuery() {
    this.query = _.clone(this.resource.data.query);
  }

  reload() {
    this.buildQuery();
  }

  showChart(type, chart, icon) {
    this.resource.data.reportType = type;
    this.chartType = type;
    this.selectedChart = chart;
    this.resetDimentions(chart);
  }

  viewresource() {
    this.showResourceView = true;
  }

  checkAccessOption() {
    switch (this.resource.resourceInfo.authentication) {
      case AccessOptions.Login:
        if (this.authenticationService.token) {
          this.viewresource();
        } else {
          this.router.navigate(['/login'], { queryParams: { returnUrl: this.router.url } });
          return false;
        }
        break;

      case AccessOptions.noLogin:
        this.viewresource();
        break;

      case AccessOptions.Password:
        this.passwordDialog();
        break;
    }
  }

  passwordDialog() {
    this.passwordDialogRef = this.dialog.open(ReportPasswordComponent, {
      disableClose: true,
      panelClass: 'fc-modal-dialog',
      data: {
        type: 'Password',
      },
    });
    this.passwordDialogRef.afterClosed().subscribe((action) => {
      if (action.confirm) {
        this.accessPassword = action.password;
        if (this.accessPassword == this.resource.resourceInfo.password) {
          this.viewresource();
        } else {
          this.passwordDialog();
        }
      } else {
        this.accessPassword = '';
      }
    });
  }

  resetDimentions(chart) {
    this.selectedChart = chart;
    if (this.resource.data.query.timeDimensions.length > 0) {
      if (this.chartType === 'singleValue') {
        this.resource.data.query.timeDimensions = [{ dimension: this.resource.data.query.timeDimensions[0].dimension }];
        this.timeFilterBy = 'Week';
      } else {
        let gran = 'week';
        if (this.resource.data.query.timeDimensions[0].granularity) {
          gran = this.resource.data.query.timeDimensions[0].granularity;
        }
        this.resource.data.query.timeDimensions = [
          { dimension: this.resource.data.query.timeDimensions[0].dimension, granularity: gran },
        ];
      }
    }
    this.buildQuery();
  }
}
