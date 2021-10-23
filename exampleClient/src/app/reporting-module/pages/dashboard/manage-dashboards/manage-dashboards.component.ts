import { Component, OnInit, OnDestroy } from '@angular/core';
import { DashboardService } from '../dashboard.service';
import { MatDialogRef, MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { TranslateService } from '@ngx-translate/core';
import { ConfirmDialogComponent } from 'src/app/common/shared';
import { take } from 'rxjs/operators';

@Component({
  selector: 'app-manage-dashboards',
  templateUrl: './manage-dashboards.component.html',
  styleUrls: ['./manage-dashboards.component.scss'],
})
export class ManageDashboardsComponent implements OnInit, OnDestroy {
  allDashboardsData = [];
  deleteDialogRef: MatDialogRef<ConfirmDialogComponent>;
  constructor(
    private dashboardService: DashboardService,
    private _snackBar: MatSnackBar,
    private translate: TranslateService,
    private dialog: MatDialog
  ) {}

  ngOnInit() {
    this.dashboardService.getAll([], 0, 1000).subscribe((res) => {
      this.allDashboardsData = res;
    });
  }

  deleteDashboard(id) {
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
          this.dashboardService.delete(id).subscribe((res) => {
            this.allDashboardsData = this.allDashboardsData.filter((v) => v.id !== id);
            this.showMessage(this.translate.instant('REPORTING.MESSAGES.DASHBOARD.DELETED'));
          });
        }
      });
  }

  ngOnDestroy() {}

  showMessage(msg): void {
    this._snackBar.open(msg, 'close', {
      duration: 2000,
    });
  }
}
