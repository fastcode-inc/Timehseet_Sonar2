import { Component, OnInit } from '@angular/core';
import { MatDialogRef, MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router, ActivatedRoute } from '@angular/router';
import { IReport } from 'src/app/reporting-module/pages/myreports/ireport';
import { ReportService } from 'src/app/reporting-module/pages/myreports/report.service';
import { ConfirmDialogComponent } from 'src/app/common/shared';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-report-details',
  templateUrl: './report-details.component.html',
  styleUrls: ['./report-details.component.scss'],
})
export class ReportDetailsComponent implements OnInit {
  report_id;
  report: IReport;
  allDashboardsList = [];

  isMediumDeviceOrLess: boolean;
  showList: boolean = false;
  listFlexWidth = 30;
  detailsFlexWidth = 70;
  confirmDialogRef: MatDialogRef<ConfirmDialogComponent>;
  isLoading: boolean = true;

  items: IReport[] = [];
  selectedReport: IReport;
  reportUnderAction: IReport;
  searchText: string = '';

  constructor(
    private reportService: ReportService,
    private _snackBar: MatSnackBar,
    public dialog: MatDialog,
    private route: ActivatedRoute,
    private router: Router,
    private translate: TranslateService
  ) {}

  ngOnInit() {
    this.report_id = +this.route.snapshot.paramMap.get('id');
    if (this.report_id >= 0) {
      this.reportService.getById(this.report_id).subscribe((report) => {
        this.report = report;
      });
    }
  }

  editReport(id) {
    this.router.navigate([`reporting/reports/${id}`]);
  }
  resetReport(id) {
    this.confirmDialogRef = this.dialog.open(ConfirmDialogComponent, {
      disableClose: true,
      data: {
        confirmationType: 'confirm',
      },
    });
    this.confirmDialogRef.afterClosed().subscribe((action) => {
      if (action) {
        this.reportService.reset(id).subscribe((res) => {
          this.items[this.items.findIndex((x) => x.id == id)] = res;
        });
      }
    });
  }

  refreshReport(id) {
    this.confirmDialogRef = this.dialog.open(ConfirmDialogComponent, {
      disableClose: true,
      data: {
        confirmationType: 'confirm',
      },
    });
    this.confirmDialogRef.afterClosed().subscribe((action) => {
      if (action) {
        this.reportService.refresh(id).subscribe((res) => {
          this.showMessage(this.translate.instant('REPORTING.MESSAGES.REPORT.REFRESHED'));
        });
      }
    });
  }

  deleteReport(report: IReport) {
    this.confirmDialogRef = this.dialog.open(ConfirmDialogComponent, {
      disableClose: true,
      data: {
        confirmationType: 'delete',
      },
    });
    this.confirmDialogRef.afterClosed().subscribe((action) => {
      if (action) {
        this.reportService.delete(report.id).subscribe((res) => {
          this.items = this.items.filter((v) => v.id !== report.id);
          this.showMessage(this.translate.instant('REPORTING.MESSAGES.REPORT.DELETED'));
          this.router.navigate(['reporting/myreports']);
        });
      }
    });
  }

  showMessage(msg: string): void {
    this._snackBar.open(msg, 'close', {
      duration: 2000,
    });
  }
}
