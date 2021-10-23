import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { MatDialogRef, MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import * as _ from 'lodash';
import { AddExReportsToDashboardComponent } from 'src/app/reporting-module/modalDialogs/addExReportsToDashboard/addExReportsToDashboard.component';
import {
  ConfirmDialogComponent,
  BaseListComponent,
  PickerDialogService,
  ISearchField,
  operatorType,
  PickerComponent,
  listProcessingType,
  ServiceUtils,
} from 'src/app/common/shared';
import { Globals } from 'src/app/core/services/globals';
import { ErrorService } from 'src/app/core/services/error.service';
import { ReportService } from 'src/app/reporting-module/pages/myreports/report.service';

import { IReport } from 'src/app/reporting-module/pages/myreports/ireport';
import { DashboardService } from 'src/app/reporting-module/pages/dashboard/dashboard.service';
import { sharingType } from '../share/ishare-config';
import { ShareComponent } from '../share/share.component';
import { ReportPasswordComponent } from './report-password/report-password.component';
import { PermalinkComponent } from '../permalink/permalink.component';
import { AuthenticationService } from 'src/app/core/services/authentication.service';
import { Observable, ReplaySubject } from 'rxjs';

import { UsersExtendedService } from 'src/app/extended/admin/user-management/users/index';
import { takeUntil } from 'rxjs/operators';
import { TranslateService } from '@ngx-translate/core';

export enum AccessOptions {
  Login = 'Login',
  noLogin = 'noLogin',
  Password = 'Password',
}

@Component({
  selector: 'app-myreports',
  templateUrl: './myreports.component.html',
  styleUrls: ['./myreports.component.scss'],
})
export class MyreportsComponent extends BaseListComponent<IReport> implements OnInit {
  isMediumDeviceOrLess: boolean;
  showList: boolean = true;
  listFlexWidth = 30;
  detailsFlexWidth = 70;
  confirmDialogRef: MatDialogRef<ConfirmDialogComponent>;
  isLoading: boolean = true;
  loading = false;
  userDialogRef: MatDialogRef<PickerComponent>;
  permalinkDialogRef: MatDialogRef<PermalinkComponent>;
  dialogRef: MatDialogRef<ShareComponent>;

  private destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

  manageScreenResizing() {
    this.global.isMediumDeviceOrLess$.subscribe((value) => {
      this.isMediumDeviceOrLess = value;
      if (this.isMediumDeviceOrLess) {
        this.listFlexWidth = 100;
        this.detailsFlexWidth = 100;
      } else {
        this.listFlexWidth = 30;
        this.detailsFlexWidth = 70;
      }
    });
  }

  items: IReport[] = [];
  selectedReport: IReport;
  selectedReportRunningVersion: IReport;
  selectedReportPublishedVersion: IReport;
  selectedVersion: string = 'running';
  reportUnderAction: IReport;
  allDashboardsList = [];
  searchText: string = '';

  constructor(
    public _snackBar: MatSnackBar,
    public dialog: MatDialog,
    public router: Router,
    public global: Globals,
    public reportService: ReportService,
    public dashboardService: DashboardService,
    public route: ActivatedRoute,
    public changeDetectorRefs: ChangeDetectorRef,
    public pickerDialogService: PickerDialogService,
    public errorService: ErrorService,
    public usersExtendedService: UsersExtendedService,
    public translate: TranslateService,
    public authService: AuthenticationService
  ) {
    super(router, route, dialog, global, changeDetectorRefs, pickerDialogService, reportService, errorService);
  }

  ngOnInit() {
    this.reportService.getAllReports().subscribe((items) => {
      this.initializePageInfo();
      this.isLoading = false;
      this.items = items;
      this.updatePageInfo(items);
    });

    this.dashboardService.getAll([], 0, 1000).subscribe((res) => {
      this.allDashboardsList = res.map((v) => {
        return {
          id: v.id,
          title: v.title,
        };
      });
    });

    this.manageScreenResizing();
  }

  /**
   * Gets field based on which table is
   * currently sorted and sort direction
   * from matSort.
   * @returns String containing sort information.
   */
  getSortValue(): string {
    let sortVal = '';
    return sortVal;
  }

  applyFilters() {
    this.isLoadingResults = true;
    this.initializePageInfo();
    let sortVal = this.getSortValue();
    this.itemsObservable = this.reportService.getAllReports(
      this.searchText,
      this.currentPage * this.pageSize,
      this.pageSize,
      sortVal
    );
    this.processListObservable(this.itemsObservable, listProcessingType.Replace);
  }

  /**
   * Loads more item data when list is
   * scrolled to the bottom (virtual scrolling).
   */
  onTableScroll() {
    if (!this.isLoadingResults && this.hasMoreRecords && this.lastProcessedOffset < this.items.length) {
      this.isLoadingResults = true;
      let sortVal = this.getSortValue();
      this.itemsObservable = this.reportService.getAllReports(
        this.searchText,
        this.currentPage * this.pageSize,
        this.pageSize,
        sortVal
      );
      this.processListObservable(this.itemsObservable, listProcessingType.Append);
    }
  }

  switchVersion(event) {
    let version = event.value;
    if (version == 'published') {
      if (this.selectedReportPublishedVersion) {
        this.selectedReport = this.selectedReportPublishedVersion;
      } else {
        this.reportService.getPublishedVersion(this.selectedReport.id).subscribe((report) => {
          this.selectedReportPublishedVersion = report;
          this.selectedReport = report;
        });
      }
    } else {
      this.selectedReport = this.selectedReportRunningVersion;
    }
  }

  viewReport(report: IReport) {
    this.selectedVersion = 'running';
    this.selectedReportRunningVersion = _.clone(report);
    this.selectedReport = _.clone(report);
    this.selectedReportPublishedVersion = null;
    this.showList = false;
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
        this.loading = true;
        this.reportService.refresh(id).subscribe((res) => {
          this.selectedReport = res;
          this.selectedReportRunningVersion = res;
          this.selectedVersion = 'running';
          this.loading = false;
          this.showMessage(this.translate.instant('REPORTING.MESSAGES.REPORT.REFRESHED'));
        });
      }
    });
  }

  publishReport(id) {
    this.confirmDialogRef = this.dialog.open(ConfirmDialogComponent, {
      disableClose: true,
      data: {
        confirmationType: 'confirm',
      },
    });
    this.confirmDialogRef.afterClosed().subscribe((action) => {
      if (action) {
        this.loading = true;
        this.reportService.publish(id).subscribe((res) => {
          this.items[this.items.findIndex((x) => x.id == id)] = res;
          this.selectedReport = res;
          this.selectedReportRunningVersion = res;
          this.selectedReportPublishedVersion = null;
          this.loading = false;
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
        this.loading = true;
        this.reportService.delete(report.id).subscribe((res) => {
          this.items = this.items.filter((v) => v.id !== report.id);
          this.loading = false;
          this.showMessage(this.translate.instant('REPORTING.MESSAGES.REPORT.DELETED'));
        });
      }
    });
  }

  showMessage(msg: string): void {
    this._snackBar.open(msg, 'close', {
      duration: 2000,
    });
  }

  refreshChart() {
    this.selectedReport.query = _.clone(this.selectedReport.query);
  }

  addReporttoDashboardDialog(report: IReport): void {
    const dialogRef = this.dialog.open(AddExReportsToDashboardComponent, {
      panelClass: 'fc-modal-dialog',
      data: this.allDashboardsList,
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.loading = true;
        if (result.type === 'new') {
          const dashboardDetails = {
            usersId: report.usersId,
            title: result.title,
            description: result.description,
            reportDetails: [
              {
                id: report.id,
                reportWidth: result.chartSize,
              },
            ],
          };
          this.dashboardService.addExistingReportToNewDashboard(dashboardDetails).subscribe((res) => {
            this.loading = false;
            this.showMessage(`${this.translate.instant('REPORTING.MESSAGES.REPORT.ADDED-TO')} ${res.title}`);
          });
        } else {
          const dashboardDetails = {
            id: result.id,
            usersId: report.usersId,
            reportDetails: [
              {
                id: report.id,
                reportWidth: result.chartSize,
              },
            ],
          };
          this.dashboardService.addExistingReportToExistingDashboard(dashboardDetails).subscribe((res) => {
            this.loading = false;
            this.showMessage(`${this.translate.instant('REPORTING.MESSAGES.REPORT.ADDED-TO')} ${res.title}`);
          });
        }
      }
    });
  }
  share(report: IReport) {
    this.dialogRef = this.dialog.open(ShareComponent, {
      data: {
        resource: 'report',
        id: report.id,
        type: sharingType.Share,
      },
      disableClose: true,
      height: '80%',
      width: '50%',
      maxWidth: 'none',
    });
    this.dialogRef.afterClosed().subscribe((data) => {
      if (data) {
        this.reportService.share(report.id, data).subscribe((data) => {
          this.showMessage(this.translate.instant('REPORTING.MESSAGES.REPORT.SHARED'));
        });
      }
    });
  }

  shareExternally(report: IReport) {
    this.permalinkDialogRef = this.dialog.open(PermalinkComponent, {
      disableClose: true,
      width: '50%',
      height: '80%',
      data: {
        resource: 'report',
        resourceId: report.id,
      },
      panelClass: 'fc-modal-dialog',
    });
    this.permalinkDialogRef.afterClosed().subscribe((action) => {
      if (action.confirm) {
        // option set;
      } else {
        // option not set
      }
    });
  }

  manageSharing(report: IReport) {
    this.dialogRef = this.dialog.open(ShareComponent, {
      data: {
        resource: 'report',
        id: report.id,
        type: sharingType.Unshare,
      },
      disableClose: true,
      height: '80%',
      width: '50%',
      maxWidth: 'none',
    });
    this.dialogRef.afterClosed().subscribe((data) => {
      if (data) {
        this.reportService.unshare(report.id, data).subscribe((data) => {
          this.showMessage(this.translate.instant('REPORTING.MESSAGES.REPORT.SHARING-UPDATED'));
        });
      }
    });
  }
  changeOwner(report: IReport) {
    this.reportUnderAction = report;
    this.userDialogRef = this.pickerDialogService.open({
      Title: this.translate.instant('REPORTING.LABELS.REPORT.SELECT-NEW-OWNER'),
      IsSingleSelection: true,
      DisplayField: 'username',
    });
    this.userDialogRef.afterClosed().subscribe((result) => {
      if (result) {
        let userPks = ['id'];
        let userId = ServiceUtils.encodeIdByObject(result, userPks);
        this.reportService.changeOwner(report.id, userId).subscribe((res) => {
          if (res) {
            this.items = this.items.filter((v) => v.id !== report.id);
            this.showMessage(
              `${this.translate.instant('REPORTING.MESSAGES.REPORT.OWNERSHIP-TRANSFERRED')} ${result.userName}`
            );
          }
        });
      }
    });

    this.userDialogRef.componentInstance.onScroll.pipe(takeUntil(this.destroyed$)).subscribe((res) => {
      this.onPickerScroll();
    });

    this.userDialogRef.componentInstance.onSearch.pipe(takeUntil(this.destroyed$)).subscribe((searchText) => {
      this.onPickerSearch(searchText);
    });

    this.onPickerSearch('');
  }

  isLoadingPickerResults = true;
  currentPickerPage: number;
  pickerPageSize: number = 30;
  lastProcessedOffsetPicker: number;
  hasMoreRecordsPicker: boolean;

  searchValuePicker: ISearchField[] = [];
  pickerItemsObservable: Observable<any>;

  /**
   * Initializes/Resets paging information of data list
   * of association showing in autocomplete options.
   */
  initializePickerPageInfo() {
    this.hasMoreRecordsPicker = true;
    this.pickerPageSize = 30;
    this.lastProcessedOffsetPicker = -1;
    this.currentPickerPage = 0;
  }

  /**
   * Manages paging for virtual scrolling for data list
   * of association showing in autocomplete options.
   * @param data Item data from the last service call.
   */
  updatePickerPageInfo(data) {
    if (data.length > 0) {
      this.currentPickerPage++;
      this.lastProcessedOffsetPicker += data.length;
    } else {
      this.hasMoreRecordsPicker = false;
    }
  }

  /**
   * Loads more data of given association when
   * list is scrolled to the bottom (virtual scrolling).
   * @param association
   */
  onPickerScroll() {
    if (
      !this.isLoadingPickerResults &&
      this.hasMoreRecordsPicker &&
      this.lastProcessedOffsetPicker < this.userDialogRef.componentInstance.items.length + 1
    ) {
      this.isLoadingPickerResults = true;
      let userObs: Observable<any[]>;
      userObs = this.usersExtendedService.getAll(
        this.searchValuePicker,
        this.currentPickerPage * this.pickerPageSize,
        this.pickerPageSize
      );
      userObs.pipe(takeUntil(this.destroyed$)).subscribe(
        (items) => {
          this.isLoadingPickerResults = false;
          this.userDialogRef.componentInstance.items = this.userDialogRef.componentInstance.items.concat(
            items.filter((item) => item.username != this.authService.decodeToken().sub)
          );
          this.updatePickerPageInfo(items);
        },
        (error) => {}
      );
    }
  }

  /**
   * Loads the data meeting given criteria of given association.
   * @param searchValue Filters to be applied.
   * @param association
   */
  onPickerSearch(searchValue: string) {
    if (!searchValue) {
      this.searchValuePicker = [];
    } else {
      let searchField: ISearchField = {
        fieldName: 'userName',
        operator: operatorType.Contains,
        searchValue: searchValue ? searchValue : '',
      };
      this.searchValuePicker = [searchField];
    }

    this.initializePickerPageInfo();
    this.usersExtendedService
      .getAll(this.searchValuePicker, this.currentPickerPage * this.pickerPageSize, this.pickerPageSize)
      .pipe(takeUntil(this.destroyed$))
      .subscribe((items) => {
        this.userDialogRef.componentInstance.items = items.filter(
          (item) => item.username != this.authService.decodeToken().sub
        );
        this.updatePickerPageInfo(items);
        this.isLoadingPickerResults = false;
      });
  }

  ngOnDestroy() {
    this.destroyed$.next(true);
    this.destroyed$.unsubscribe();
  }
}
