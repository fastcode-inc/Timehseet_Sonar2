import { Component, OnInit } from '@angular/core';
import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';
import { ActivatedRoute, Router } from '@angular/router';
import * as _ from 'lodash';
import { MatDialogRef, MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { DashboardService } from '../dashboard.service';
import { sharingType } from '../../share/ishare-config';
import { ShareComponent } from '../../share/share.component';
import { ReportPasswordComponent } from 'src/app/reporting-module/pages/myreports/report-password/report-password.component';
import { PermalinkComponent } from 'src/app/reporting-module/pages/permalink/permalink.component';
import { IDashboard } from '../idashboard';
import { UpdateDashboardComponent } from 'src/app/reporting-module/modalDialogs/update-dashboard/update-dashboard.component';
import { IReport } from 'src/app/reporting-module/pages/myreports/ireport';
import {
  ConfirmDialogComponent,
  PickerComponent,
  PickerDialogService,
  ISearchField,
  operatorType,
  ServiceUtils,
} from 'src/app/common/shared';
import { UsersExtendedService } from 'src/app/extended/admin/user-management/users/index';
import { Observable, ReplaySubject } from 'rxjs';
import { takeUntil, take } from 'rxjs/operators';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-dashboard',
  templateUrl: './editdashboard.component.html',
  styleUrls: ['./editdashboard.component.scss'],
})
export class EditDashboardComponent implements OnInit {
  confirmDialogRef: MatDialogRef<ConfirmDialogComponent>;
  private destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);
  dashboard: IDashboard;
  selectedDashboardRunningVersion: IDashboard;
  selectedDashboardPublishedVersion: IDashboard;
  selectedVersion: string = 'running';
  loading = false;
  dialogRef: MatDialogRef<any>;
  userDialogRef: MatDialogRef<PickerComponent>;
  shareReport: IDashboard;
  accessPassword: any;
  passwordDialogRef: MatDialogRef<ReportPasswordComponent>;
  permalinkDialogRef: MatDialogRef<PermalinkComponent>;
  currentElementsPositions = {};
  constructor(
    private route: ActivatedRoute,
    private _snackBar: MatSnackBar,
    private dialog: MatDialog,
    private router: Router,
    private dashboardService: DashboardService,
    public pickerDialogService: PickerDialogService,
    public usersExtendedService: UsersExtendedService,
    public translate: TranslateService
  ) {}

  ngOnInit() {
    this.route.paramMap.subscribe((params) => {
      const id = params.get('id');
      this.dashboardService.getById(id).subscribe((res) => {
        this.dashboard = res;
        this.selectedDashboardRunningVersion = res;
      });
    });
  }

  switchVersion(event) {
    let version = event.value;
    if (version == 'published') {
      if (this.selectedDashboardPublishedVersion) {
        this.dashboard = this.selectedDashboardPublishedVersion;
      } else {
        this.dashboardService.getPublishedVersion(this.dashboard.id).subscribe((dashboard) => {
          this.selectedDashboardPublishedVersion = dashboard;
          this.dashboard = dashboard;
        });
      }
    } else {
      this.dashboard = this.selectedDashboardRunningVersion;
    }
  }

  deleteReport(dashboard_id, report_id) {
    this.confirmDialogRef = this.dialog.open(ConfirmDialogComponent, {
      disableClose: true,
      data: {
        confirmationType: 'delete',
      },
    });
    this.confirmDialogRef.afterClosed().subscribe((action) => {
      if (action) {
        this.loading = true;
        this.dashboardService.removeReport(dashboard_id, report_id).subscribe((res) => {
          this.dashboard.reportDetails = this.dashboard.reportDetails.filter((v) => v.id !== report_id);
          this.loading = false;
          this.showMessage(this.translate.instant('REPORTING.MESSAGES.DASHBOARD.REPORT-REMOVED'));
        });
      }
    });
  }

  shareExternally(dashboard: IDashboard) {
    // this.externalShareView = true;

    this.permalinkDialogRef = this.dialog.open(PermalinkComponent, {
      // disableClose: true,
      width: '50%',
      height: '80%',
      data: {
        resource: 'dashboard',
        resourceId: dashboard.id,
      },
      panelClass: 'fc-modal-dialog',
    });
    this.permalinkDialogRef.afterClosed().subscribe((action) => {
      if (action.confirm) {
        //options set
      } else {
        //options not set
      }
    });
  }

  resetDashboard(id) {
    this.confirmDialogRef = this.dialog.open(ConfirmDialogComponent, {
      disableClose: true,
      data: {
        confirmationType: 'confirm',
      },
    });
    this.confirmDialogRef.afterClosed().subscribe((action) => {
      if (action) {
        this.dashboardService.reset(id).subscribe((res) => {
          this.dashboard = res;
        });
      }
    });
  }

  share() {
    this.dialogRef = this.dialog.open(ShareComponent, {
      data: {
        resource: 'dashboard',
        id: this.dashboard.id,
        type: sharingType.Share,
      },
      disableClose: true,
      height: '80%',
      width: '50%',
      maxWidth: 'none',
    });
    this.dialogRef.afterClosed().subscribe((data) => {
      if (data) {
        this.dashboardService.share(this.dashboard.id, data).subscribe((data) => {
          this.showMessage(this.translate.instant('REPORTING.MESSAGES.DASHBOARD.SHARED'));
        });
      }
    });
  }

  manageSharing() {
    this.dialogRef = this.dialog.open(ShareComponent, {
      data: {
        resource: 'dashboard',
        id: this.dashboard.id,
        type: sharingType.Unshare,
      },
      disableClose: true,
      height: '80%',
      width: '50%',
      maxWidth: 'none',
    });
    this.dialogRef.afterClosed().subscribe((data) => {
      if (data) {
        this.dashboardService.unshare(this.dashboard.id, data).subscribe((data) => {
          this.showMessage(this.translate.instant('REPORTING.MESSAGES.DASHBOARD.SHARING-UPDATED'));
        });
      }
    });
  }

  changeOwner() {
    this.userDialogRef = this.pickerDialogService.open({
      Title: this.translate.instant('REPORTING.LABELS.DASHBOARD.SELECT-NEW-OWNER'),
      IsSingleSelection: true,
      DisplayField: 'username',
    });
    this.userDialogRef
      .afterClosed()
      .pipe(take(1))
      .subscribe((result) => {
        if (result) {
          let userPks = ['id'];
          let userId = ServiceUtils.encodeIdByObject(result, userPks);
          this.dashboardService.changeOwner(this.dashboard.id, userId).subscribe((res) => {
            if (res) {
              this.showMessage(
                `${this.translate.instant('REPORTING.MESSAGES.DASHBOARD.OWNERSHIP-TRANSFERRED')} ${result.userName}`
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
      this.lastProcessedOffsetPicker < this.userDialogRef.componentInstance.items.length
    ) {
      this.isLoadingPickerResults = true;
      let userObs: Observable<any[]>;
      userObs = this.usersExtendedService.getAll(
        this.searchValuePicker,
        this.currentPickerPage * this.pickerPageSize,
        this.pickerPageSize
      );
      userObs.subscribe(
        (items) => {
          this.isLoadingPickerResults = false;
          this.userDialogRef.componentInstance.items = this.userDialogRef.componentInstance.items.concat(items);
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
      .subscribe((items) => {
        this.userDialogRef.componentInstance.items = items;
        this.updatePickerPageInfo(items);
        this.isLoadingPickerResults = false;
      });
  }

  updateDashboard() {
    var updated_reports_array = [];
    this.loading = true;
    for (var i = 0; i < this.dashboard.reportDetails.length; i++) {
      updated_reports_array.push({
        id: this.dashboard.reportDetails[i].id,
        reportWidth: this.dashboard.reportDetails[i].reportWidth,
      });
    }
    const dashboard = {
      id: this.dashboard.id,
      title: this.dashboard.title,
      description: this.dashboard.description,
      usersId: this.dashboard.usersId,
      reportDetails: updated_reports_array,
    };
    this.dashboardService.update(this.dashboard, this.dashboard.id).subscribe((res) => {
      this.dashboard = res;
      this.selectedDashboardRunningVersion = res;
      this.selectedDashboardPublishedVersion = null;
      this.loading = false;
      this.showMessage(this.translate.instant('REPORTING.MESSAGES.DASHBOARD.UPDATED'));
    });
  }

  editReport(report: IReport) {
    this.router.navigate([`reporting/reports/${report.id}`]);
  }

  editDashboard(): void {
    const dialogRef = this.dialog.open(UpdateDashboardComponent, {
      panelClass: 'fc-modal-dialog',
      data: {
        title: this.dashboard.title,
        description: this.dashboard.description,
      },
    });
    dialogRef.afterClosed().subscribe((result) => {
      this.dashboard.title = result.dashboardTitle;
      this.dashboard.description = result.dashboarddescription;
      if (result.type != 'close') {
        this.updateDashboard();
      }
    });
  }

  previewDashboard(id) {
    this.router.navigate([`reporting/dashboard/preview/${id}`]);
  }

  refreshDashboard(id) {
    this.confirmDialogRef = this.dialog.open(ConfirmDialogComponent, {
      disableClose: true,
      data: {
        confirmationType: 'confirm',
      },
    });
    this.confirmDialogRef.afterClosed().subscribe((action) => {
      if (action) {
        this.loading = true;
        this.dashboardService.refresh(id).subscribe((res) => {
          this.selectedDashboardRunningVersion = res;
          this.dashboard = res;
          this.selectedVersion = 'running';
          this.loading = false;
          this.showMessage(this.translate.instant('REPORTING.MESSAGES.DASHBOARD.REFRESHED'));
        });
      }
    });
  }

  publishDashboard(id) {
    this.confirmDialogRef = this.dialog.open(ConfirmDialogComponent, {
      disableClose: true,
      data: {
        confirmationType: 'confirm',
      },
    });
    this.confirmDialogRef.afterClosed().subscribe((action) => {
      if (action) {
        this.loading = true;
        this.dashboardService.publish(id).subscribe((res) => {
          this.dashboard = res;
          this.selectedDashboardRunningVersion = res;
          this.selectedDashboardPublishedVersion = null;
          this.loading = false;
        });
      }
    });
  }

  deleteDashboard(dashboard: IDashboard) {
    this.confirmDialogRef = this.dialog.open(ConfirmDialogComponent, {
      disableClose: true,
      data: {
        confirmationType: 'delete',
      },
    });

    this.confirmDialogRef
      .afterClosed()
      .pipe(take(1))
      .subscribe((action) => {
        if (action) {
          this.loading = true;
          this.dashboardService
            .delete(dashboard.id)
            .pipe(take(1))
            .subscribe((res) => {
              this.loading = true;
              this.showMessage(this.translate.instant('REPORTING.MESSAGES.DASHBOARD.DELETED'));
              this.router.navigate(['/reporting/dashboard']);
            });
        }
      });
  }

  refreshChart(report_id) {
    this.dashboard.reportDetails[this.dashboard.reportDetails.findIndex((x) => x.id == report_id)] = _.clone(
      this.dashboard.reportDetails[this.dashboard.reportDetails.findIndex((x) => x.id == report_id)]
    );
  }

  showMessage(msg): void {
    this._snackBar.open(msg, 'close', {
      duration: 2000,
    });
  }

  drop(event: CdkDragDrop<string[]>) {
    moveItemInArray(this.dashboard.reportDetails, event.previousIndex, event.currentIndex);
  }

  setSize(size, index) {
    this.dashboard.reportDetails[index].reportWidth = size;
  }

  ngOnDestroy() {
    this.destroyed$.next(true);
    this.destroyed$.unsubscribe();
  }
}
