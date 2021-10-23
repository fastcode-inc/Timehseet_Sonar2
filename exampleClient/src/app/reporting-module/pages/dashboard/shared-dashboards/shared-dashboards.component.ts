import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialogRef } from '@angular/material/dialog';
import { MainService } from '../../../services/main.service';
import { DashboardService } from '../dashboard.service';
import { IDashboard } from '../idashboard';
import { take } from 'rxjs/operators';
import { ErrorService } from 'src/app/core/services/error.service';
import { ConfirmDialogComponent } from 'src/app/common/shared';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-shared-dashboards',
  templateUrl: './shared-dashboards.component.html',
  styleUrls: ['./shared-dashboards.component.scss'],
})
export class SharedDashboardsComponent implements OnInit {
  deleteDialogRef: MatDialogRef<ConfirmDialogComponent>;
  dashboards: IDashboard[];
  isLoading: boolean = true;
  displayedColumns: string[] = [
    'Title',
    'Description',
    'Owner',
    'OwnerSharingStatus',
    'RecipientSharingStatus',
    'Actions',
  ];
  constructor(
    private router: Router,
    public dialog: MatDialog,
    public service: MainService,
    public dashboardService: DashboardService,
    private errorService: ErrorService,
    private translate: TranslateService,
    private _snackBar: MatSnackBar
  ) {}

  ngOnInit() {
    this.dashboardService.getShared('', 0, 1000, '').subscribe((reports) => {
      this.isLoading = false;
      this.dashboards = reports;
    });
  }

  dashboardDetails(dashboard: IDashboard) {
    this.router.navigate([`reporting/dashboard/edit/${dashboard.id}`]);
  }

  deleteDashboard(dashboard: IDashboard) {
    this.deleteDialogRef = this.dialog.open(ConfirmDialogComponent, {
      disableClose: true,
      data: {
        confirmationType: 'delete',
      },
    });

    this.deleteDialogRef
      .afterClosed()
      .pipe(take(1))
      .subscribe((action) => {
        if (action) {
          this.dashboardService.delete(dashboard.id).subscribe((res) => {
            this.dashboards = this.dashboards.filter((v) => v.id !== dashboard.id);
            this.showMessage(this.translate.instant('REPORTING.MESSAGES.DASHBOARD.DELETED'));
          });
        }
      });
  }

  showMessage(msg): void {
    this._snackBar.open(msg, 'close', {
      duration: 2000,
    });
  }

  changeRecipientSharingStatus(dashboard: IDashboard) {
    this.dashboardService
      .updateRecipientSharingStatus(dashboard.id, dashboard.recipientSharingStatus)
      .pipe(take(1))
      .subscribe((res) => {
        if (res) {
          this.errorService.showError(this.translate.instant('REPORTING.MESSAGES.STATUS-CHANGED'));
        } else {
          this.errorService.showError(this.translate.instant('REPORTING.MESSAGES.ERROR-OCCURRED'));
        }
      });
  }
}
