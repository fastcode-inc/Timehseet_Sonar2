import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatDialog } from '@angular/material/dialog';
import { MatSort } from '@angular/material/sort';
import { of as observableOf } from 'rxjs';
import { catchError, map, startWith, switchMap } from 'rxjs/operators';
import { IExecutionHistory } from './executionHistory';
import { JobService } from '../jobs/job.service';
import { IListColumn, ListColumnType, ISearchField } from 'src/app/common/shared';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-execution-history',
  templateUrl: './execution-history.component.html',
  styleUrls: ['./execution-history.component.scss'],
})
export class ExecutionHistoryComponent implements OnInit {
  @ViewChild(MatSort, { static: true }) sort: MatSort;
  isLoadingResults = true;

  currentPage: number;
  pageSize: number;
  lastProcessedOffset: number;
  hasMoreRecords = true;

  columns: IListColumn[] = [
    {
      column: 'triggerName',
      label: this.translate.instant('EXECUTION-HISTORY.FIELDS.TRIGGER-NAME'),
      sort: true,
      filter: true,
      type: ListColumnType.String,
    },
    {
      column: 'triggerGroup',
      label: this.translate.instant('EXECUTION-HISTORY.FIELDS.TRIGGER-GROUP'),
      sort: false,
      filter: true,
      type: ListColumnType.String,
    },
    {
      column: 'jobName',
      label: this.translate.instant('EXECUTION-HISTORY.FIELDS.JOB-NAME'),
      sort: false,
      filter: true,
      type: ListColumnType.String,
    },
    {
      column: 'jobGroup',
      label: this.translate.instant('EXECUTION-HISTORY.FIELDS.JOB-GROUP'),
      sort: false,
      filter: true,
      type: ListColumnType.String,
    },
    {
      column: 'jobClass',
      label: this.translate.instant('EXECUTION-HISTORY.FIELDS.JOB-CLASS'),
      sort: false,
      filter: true,
      type: ListColumnType.String,
    },
    {
      column: 'jobStatus',
      label: this.translate.instant('EXECUTION-HISTORY.FIELDS.STATUS'),
      sort: false,
      filter: true,
      type: ListColumnType.String,
    },
    {
      column: 'duration',
      label: this.translate.instant('EXECUTION-HISTORY.FIELDS.DURATION'),
      sort: false,
      filter: true,
      type: ListColumnType.String,
    },
    {
      column: 'firedTime',
      label: this.translate.instant('EXECUTION-HISTORY.FIELDS.FIRE-TIME'),
      sort: false,
      filter: true,
      type: ListColumnType.Date,
    },
    {
      column: 'finishedTime',
      label: this.translate.instant('EXECUTION-HISTORY.FIELDS.FINISHED-TIME'),
      sort: false,
      filter: true,
      type: ListColumnType.Date,
    },
  ];

  selectedColumns = this.columns;
  displayedColumnsExecutionHistory: string[] = this.columns.map((obj) => {
    return obj.column;
  });

  public searchValue: ISearchField[] = [];
  public dataSource;
  sortedData: IExecutionHistory[];

  userId: number;
  executionHistory: IExecutionHistory[] = [];
  errorMessage = '';
  constructor(private jobService: JobService, public dialog: MatDialog, private translate: TranslateService) {}

  ngOnInit() {
    this.setSort();
  }

  setSort() {
    this.sort.sortChange
      .pipe(
        startWith({}),
        switchMap(() => {
          this.isLoadingResults = true;
          this.initializePageInfo();
          return this.jobService.getJobExecutionHistory(this.searchValue, 0, this.pageSize, this.getSortValue());
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
        this.executionHistory = data;
        this.dataSource = new MatTableDataSource(this.executionHistory);
        //manage pages for virtual scrolling
        this.updatePageInfo(data);
      });
  }

  applyFilter(searchValue) {
    this.initializePageInfo();
    this.searchValue = searchValue;
    this.isLoadingResults = true;
    this.jobService
      .getJobExecutionHistory(this.searchValue, this.currentPage * this.pageSize, this.pageSize, this.getSortValue())
      .subscribe(
        (data) => {
          this.isLoadingResults = false;
          this.executionHistory = data;
          this.dataSource = new MatTableDataSource(this.executionHistory);
          this.updatePageInfo(data);
        },
        (error) => (this.errorMessage = <any>error)
      );
  }

  initializePageInfo() {
    this.pageSize = 50;
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
    if (!this.isLoadingResults && this.hasMoreRecords && this.lastProcessedOffset < this.executionHistory.length) {
      this.isLoadingResults = true;
      this.jobService
        .getJobExecutionHistory(this.searchValue, this.currentPage * this.pageSize, this.pageSize, this.getSortValue())
        .subscribe(
          (data) => {
            this.isLoadingResults = false;
            this.executionHistory = this.executionHistory.concat(data);
            this.dataSource = new MatTableDataSource(this.executionHistory);

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
