import { Component, OnInit, ChangeDetectorRef, ViewChild } from '@angular/core';
import { ITrigger } from '../trigger';

import { of as observableOf } from 'rxjs';
import { catchError, map, startWith, switchMap } from 'rxjs/operators';
import { TriggerService } from '../trigger.service';
import { MatTableDataSource } from '@angular/material/table';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { MatSort } from '@angular/material/sort';
import { TriggerNewComponent } from '../new/trigger-new.component';
import { ErrorService } from 'src/app/core/services/error.service';
import { Globals } from 'src/app/core/services/globals';
import { FormGroup } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';

import { IListColumn, ListColumnType, ISearchField, ConfirmDialogComponent } from 'src/app/common/shared';

@Component({
  selector: 'app-triggers',
  templateUrl: './triggers.component.html',
  styleUrls: ['./triggers.component.scss'],
})
export class TriggersComponent implements OnInit {
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  // table data for triggers
  columns: IListColumn[] = [
    {
      column: 'triggerName',
      label: this.translate.instant('TRIGGERS.FIELDS.NAME'),
      sort: true,
      filter: true,
      type: ListColumnType.String,
    },
    {
      column: 'type',
      label: this.translate.instant('TRIGGERS.FIELDS.TYPE'),
      sort: true,
      filter: true,
      type: ListColumnType.String,
    },
    {
      column: 'triggerGroup',
      label: this.translate.instant('TRIGGERS.FIELDS.GROUP'),
      sort: false,
      filter: true,
      type: ListColumnType.String,
    },
    {
      column: 'jobName',
      label: this.translate.instant('TRIGGERS.FIELDS.JOB-NAME'),
      sort: false,
      filter: true,
      type: ListColumnType.String,
    },
    {
      column: 'jobGroup',
      label: this.translate.instant('TRIGGERS.FIELDS.JOB-GROUP'),
      sort: false,
      filter: true,
      type: ListColumnType.String,
    },
    {
      column: 'startTime',
      label: this.translate.instant('TRIGGERS.FIELDS.START-TIME'),
      sort: false,
      filter: true,
      type: ListColumnType.Date,
    },
    {
      column: 'endTime',
      label: this.translate.instant('TRIGGERS.FIELDS.END-TIME'),
      sort: false,
      filter: true,
      type: ListColumnType.Date,
    },
    {
      column: 'lastExecutionTime',
      label: this.translate.instant('TRIGGERS.FIELDS.LAST-EXECUTION-TIME'),
      sort: false,
      filter: true,
      type: ListColumnType.Date,
    },
    {
      column: 'nextExecutionTime',
      label: this.translate.instant('TRIGGERS.FIELDS.NEXT-EXECUTION-TIME'),
      sort: false,
      filter: true,
      type: ListColumnType.Date,
    },
  ];
  selectedColumns = this.columns;
  allDisplayedColumns: string[] = [
    'triggerName',
    'triggerGroup',
    'type',
    'jobName',
    'jobGroup',
    'startTime',
    'endTime',
    'lastExecutionTime',
    'nextExecutionTime',
    'actions',
  ];
  displayedColumns: string[] = this.allDisplayedColumns;
  userId: number;
  triggers: ITrigger[] = [];
  errorMessage = '';
  filterSelect: string = '';
  isLoadingResults = true;

  currentPage: number;
  pageSize: number;
  lastProcessedOffset: number;
  hasMoreRecords = true;
  searchValue: ISearchField[] = [];

  filterForm: FormGroup;
  loading = false;
  submitted = false;
  filterList: any = {};
  dataSource;
  sortedData: ITrigger[];

  isMediumDeviceOrLess: boolean;
  dialogRef: MatDialogRef<any>;
  confirmDialogRef: MatDialogRef<any>;

  mediumDeviceOrLessDialogSize: string = '100%';
  largerDeviceDialogWidthSize: string = '75%';
  largerDeviceDialogHeightSize: string = '90%';

  constructor(
    private global: Globals,
    private triggerService: TriggerService,
    public dialog: MatDialog,
    private changeDetectorRefs: ChangeDetectorRef,
    private translate: TranslateService,
    private errorService: ErrorService
  ) {}

  ngOnInit() {
    this.manageScreenResizing();
    this.setSort();
    this.filterList = this.selectedColumns.filter((items) => {
      if (items.filter) {
        return items;
      }
    });
  }

  selectedFilter: String;
  selctFilter(event) {
    this.filterSelect = event;
    this.selectedFilter = event;
  }
  applyFilter1(filterValue: string) {
    if (filterValue !== '') {
    }
  }

  setSort() {
    this.sort.sortChange
      .pipe(
        startWith({}),
        switchMap(() => {
          console.log('DFS');
          this.isLoadingResults = true;
          this.initializePageInfo();
          return this.triggerService.getAll(this.searchValue, 0, this.pageSize, this.getSortValue());
        }),
        map((data) => {
          // Flip flag to show that loading has finished.
          this.isLoadingResults = false;
          return data;
        }),
        catchError(() => {
          this.isLoadingResults = false;
          // Catch if some error occurred. Return empty data.
          return observableOf([]);
        })
      )
      .subscribe((data) => {
        this.triggers = data;
        this.dataSource = new MatTableDataSource(this.triggers);
        //manage pages for virtual scrolling
        this.updatePageInfo(data);
      });
  }

  manageScreenResizing() {
    this.global.isMediumDeviceOrLess$.subscribe((value) => {
      this.isMediumDeviceOrLess = value;
      // if (value) {
      //   this.selectedColumns = this.columns.slice(0, 3);
      //   this.displayedColumns = this.allDisplayedColumns.slice(0,3)
      // }
      // else {
      //   this.selectedColumns = this.columns;
      //   this.displayedColumns = this.allDisplayedColumns;
      // }

      if (this.dialogRef)
        this.dialogRef.updateSize(
          value ? this.mediumDeviceOrLessDialogSize : this.largerDeviceDialogWidthSize,
          value ? this.mediumDeviceOrLessDialogSize : this.largerDeviceDialogHeightSize
        );
    });
  }
  add() {
    this.openDialog();
  }

  openDialog() {
    this.dialogRef = this.dialog.open(TriggerNewComponent, {
      disableClose: true,
      height: this.isMediumDeviceOrLess ? this.mediumDeviceOrLessDialogSize : this.largerDeviceDialogHeightSize,
      width: this.isMediumDeviceOrLess ? this.mediumDeviceOrLessDialogSize : this.largerDeviceDialogWidthSize,
      maxWidth: 'none',
      panelClass: 'fc-modal-dialog',
    });
    this.dialogRef.afterClosed().subscribe((result) => {
      console.log(`Dialog result: ${result}`);
      if (result) {
        this.errorService.showError(this.translate.instant('TRIGGERS.MESSAGES.CREATED'));
        this.setSort();
      }
    });
  }

  pauseTrigger(trigger): void {
    this.openConfirmMessage(
      this.translate.instant('TRIGGERS.MESSAGES.CONFIRM.PAUSE'),
      this.pauseTriggerActionResult,
      trigger
    );
  }

  pauseTriggerActionResult = (action, trigger) => {
    if (action) {
      this.triggerService.pauseTrigger(trigger.triggerName, trigger.triggerGroup).subscribe((resp) => {
        if (resp) {
          trigger.triggerState = 'PAUSED';
          this.errorService.showError(this.translate.instant('TRIGGERS.MESSAGES.PAUSED'));
        }
      });
      console.log(trigger, 'trigger paused');
    }
  };

  resumeTrigger(trigger): void {
    this.openConfirmMessage(
      this.translate.instant('TRIGGERS.MESSAGES.CONFIRM.RESUME'),
      this.resumeTriggerActionResult,
      trigger
    );
  }

  resumeTriggerActionResult = (action, trigger) => {
    if (action) {
      this.triggerService.resumeTrigger(trigger.triggerName, trigger.triggerGroup).subscribe((resp) => {
        if (resp) {
          trigger.triggerState = 'NORMAL';
          this.errorService.showError(this.translate.instant('TRIGGERS.MESSAGES.RESUMED'));
        }
      });
      console.log(trigger, 'trigger paused');
    }
  };

  deleteTrigger(trigger): void {
    this.openConfirmMessage(
      this.translate.instant('TRIGGERS.MESSAGES.CONFIRM.DELETE'),
      this.deleteTriggerActionResult,
      trigger
    );
  }

  deleteTriggerActionResult = (action, trigger) => {
    if (action) {
      this.triggerService.delete(trigger.triggerName, trigger.triggerGroup).subscribe(
        (resp) => {
          this.triggers.splice(this.triggers.indexOf(trigger), 1);
          this.errorService.showError(this.translate.instant('TRIGGERS.MESSAGES.DELETED'));
          this.dataSource = new MatTableDataSource(this.triggers);
          this.changeDetectorRefs.detectChanges();
        },
        (error) => (this.errorMessage = <any>error)
      );
    }
  };

  openConfirmMessage(message, callback, trigger): void {
    this.confirmDialogRef = this.dialog.open(ConfirmDialogComponent, {
      disableClose: true,
      data: {
        message: message,
      },
    });
    this.confirmDialogRef.afterClosed().subscribe((action) => {
      if (action) {
        callback(action, trigger);
      }
    });
  }

  applyFilter(searchValue) {
    this.initializePageInfo();
    this.searchValue = searchValue;
    this.isLoadingResults = true;
    this.triggerService
      .getAll(this.searchValue, this.currentPage * this.pageSize, this.pageSize, this.getSortValue())
      .subscribe(
        (data) => {
          this.isLoadingResults = false;
          this.triggers = data;
          this.dataSource = new MatTableDataSource(this.triggers);
          this.updatePageInfo(data);
        },
        (error) => (this.errorMessage = <any>error)
      );
  }

  initializePageInfo() {
    this.pageSize = 10;
    this.lastProcessedOffset = -1;
    this.currentPage = 0;
  }

  //manage pages for virtual scrolling
  updatePageInfo(data) {
    if (data.length > 0) {
      this.currentPage++;
      this.lastProcessedOffset += data.length;
    } else {
      this.hasMoreRecords = false;
    }
  }

  onTableScroll() {
    if (!this.isLoadingResults && this.hasMoreRecords && this.lastProcessedOffset < this.triggers.length) {
      this.isLoadingResults = true;
      this.triggerService
        .getAll(this.searchValue, this.currentPage * this.pageSize, this.pageSize, this.getSortValue())
        .subscribe(
          (data) => {
            this.isLoadingResults = false;
            this.triggers = this.triggers.concat(data);
            this.dataSource = new MatTableDataSource(this.triggers);

            this.updatePageInfo(data);
          },
          (error) => (this.errorMessage = <any>error)
        );
    }
  }

  getSortValue(): string {
    let sortVal = '';
    if (this.sort.active && this.sort.direction) {
      sortVal = this.sort.active + ',' + this.sort.direction;
    }
    return sortVal;
  }
}
