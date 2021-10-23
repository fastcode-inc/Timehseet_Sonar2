import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { IApiHistory } from './apiHistory';
import { ApiHistoryService } from './api-history.service';
import { MatTableDataSource } from '@angular/material/table';
import { Observable } from 'rxjs';

import { ISearchField, operatorType } from 'src/app/common/shared';
import { FormBuilder, FormGroup } from '@angular/forms';
import { UsersService, IUsers } from 'src/app/admin/user-management/users/index';
import { TranslateService } from '@ngx-translate/core';
import { Globals } from 'src/app/core/services/globals';
import { ErrorService } from 'src/app/core/services/error.service';

enum listProcessingType {
  Replace = 'Replace',
  Append = 'Append',
}

@Component({
  selector: 'app-api-history',
  templateUrl: './api-history.component.html',
  styleUrls: ['./api-history.component.scss'],
})
export class ApiHistoryComponent implements OnInit {
  title: string = 'User Audit Trail';
  apiHistory: IApiHistory[] = [];
  itemsObservable: Observable<IApiHistory[]>;
  errorMessage: '';
  displayedColumns: string[] = ['API', 'Operation', 'ProcessTime', 'User', 'Time', 'Caller Adddress'];

  public dataSource;
  userList: IUsers[] = [];
  IsCreatePermission: boolean = true;

  states: string[] = ['Alabama'];
  state: string = 'All';

  filterFields = [];
  basicFilterForm: FormGroup;

  constructor(
    private global: Globals,
    private apiHistoryService: ApiHistoryService,
    public errorService: ErrorService,
    public usersService: UsersService,
    private formBuilder: FormBuilder,
    private translate: TranslateService
  ) {}

  ngOnInit() {
    this.manageScreenResizing();
    this.getApiHistory();

    this.basicFilterForm = this.formBuilder.group({
      author: [''],
      from: [''],
      to: [''],
      api: [''],
      operation: [''],
    });

    this.basicFilterForm.get('author').valueChanges.subscribe((value) => this.onPickerSearch(value));
  }

  getApiHistory() {
    this.isLoadingResults = true;
    this.initializePageInfo();
    this.itemsObservable = this.apiHistoryService.getAll(
      this.searchValue,
      this.currentPage * this.pageSize,
      this.pageSize
    );
    this.processListObservable(this.itemsObservable, listProcessingType.Replace);
  }

  manageScreenResizing() {
    this.global.isMediumDeviceOrLess$.subscribe((value) => {
      // this.isMediumDeviceOrLess = value;
      // if (value)
      //   this.displayedColumns = ['API', 'Operation', 'ProcessTime', 'User'];
      // else
      //   this.displayedColumns = ['API', 'Operation', 'ProcessTime', 'User', 'Time', 'Caller Adddress']
    });
  }

  createSearchString() {
    let searchString: string = '';
    let searchFormValue = this.basicFilterForm.getRawValue();
    if (searchFormValue.author) {
      searchString += 'userName=' + searchFormValue.author;
    }

    if (searchFormValue.api) {
      if (searchString.length > 0) {
        searchString += ';';
      }
      searchString += 'path=' + searchFormValue.api;
    }

    if (searchFormValue.operation) {
      if (searchString.length > 0) {
        searchString += ';';
      }
      searchString += 'method=' + searchFormValue.operation;
    }

    if (searchFormValue.from) {
      if (searchString.length > 0) {
        searchString += ';';
      }
      let from = new Date(searchFormValue.from);
      searchString += 'requestTime=' + from.getFullYear() + '-' + (from.getMonth() + 1) + '-' + from.getDate(); // + " " + from.getHours() + ":" + from.getMinutes() + ":" + from.getSeconds() + "." + from.getMilliseconds();
    }

    if (searchFormValue.to) {
      if (searchString.length > 0) {
        searchString += ';';
      }
      let to = new Date(searchFormValue.to);
      searchString += 'responseTime=' + to.getFullYear() + '-' + (to.getMonth() + 1) + '-' + to.getDate(); // + " " + to.getHours() + ":" + to.getMinutes() + ":" + to.getSeconds() + "." + to.getMilliseconds();
    }

    return searchString;
  }

  applyFilter() {
    this.searchValue = this.createSearchString();
    this.isLoadingResults = true;
    this.initializePageInfo();
    this.itemsObservable = this.apiHistoryService.getAll(
      this.searchValue,
      this.currentPage * this.pageSize,
      this.pageSize
    );
    this.processListObservable(this.itemsObservable, listProcessingType.Replace);
  }

  isLoadingResults = true;

  currentPage: number;
  pageSize: number;
  lastProcessedOffset: number;
  hasMoreRecords: boolean;
  searchValue: string = '';

  initializePageInfo() {
    this.hasMoreRecords = true;
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
    if (!this.isLoadingResults && this.hasMoreRecords && this.lastProcessedOffset < this.apiHistory.length) {
      this.isLoadingResults = true;
      this.itemsObservable = this.apiHistoryService.getAll(
        this.searchValue,
        this.currentPage * this.pageSize,
        this.pageSize
      );
      this.processListObservable(this.itemsObservable, listProcessingType.Append);
    }
  }

  processListObservable(listObservable: Observable<IApiHistory[]>, type: listProcessingType) {
    listObservable.subscribe(
      (apiHistory) => {
        this.isLoadingResults = false;
        if (type == listProcessingType.Replace) {
          this.apiHistory = apiHistory;
          this.dataSource = new MatTableDataSource(this.apiHistory);
        } else {
          this.apiHistory = this.apiHistory.concat(apiHistory);
          this.dataSource = new MatTableDataSource(this.apiHistory);
        }
        this.updatePageInfo(apiHistory);
      },
      (error) => {
        this.errorMessage = <any>error;
        this.errorService.showError(this.translate.instant('GENERAL.ERRORS.FETCHING-RESULT'));
      }
    );
  }

  /**
   * Author list processing
   */

  getUsers() {
    this.usersService
      .getAll(this.searchValuePicker, this.currentPickerPage * this.pickerPageSize, this.pickerPageSize)
      .subscribe((items) => {
        console.log('MyItems :', items);
        this.userList = items;
      });
  }

  isLoadingPickerResults = true;

  currentPickerPage: number;
  pickerPageSize: number;
  lastProcessedOffsetPicker: number;
  hasMoreRecordsPicker: boolean;

  searchValuePicker: ISearchField[] = [];
  pickerItemsObservable: Observable<any>;

  /**
   * Initializes/Resets paging information of user data list
   * showing in autocomplete options.
   */
  initializePickerPageInfo() {
    this.hasMoreRecordsPicker = true;
    this.pickerPageSize = 30;
    this.lastProcessedOffsetPicker = -1;
    this.currentPickerPage = 0;
  }

  /**
   * Manages paging for virtual scrolling for user data list
   * showing in autocomplete options.
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
   * Loads more user data when
   * list is scrolled to the bottom (virtual scrolling).
   */
  onPickerScroll() {
    if (
      !this.isLoadingPickerResults &&
      this.hasMoreRecordsPicker &&
      this.lastProcessedOffsetPicker < this.userList.length
    ) {
      this.isLoadingPickerResults = true;
      this.usersService
        .getAll(
          this.basicFilterForm.get('author').value,
          this.currentPickerPage * this.pickerPageSize,
          this.pickerPageSize
        )
        .subscribe(
          (items) => {
            this.isLoadingPickerResults = false;
            this.userList = this.userList.concat(items);
            this.updatePickerPageInfo(items);
          },
          (error) => {
            this.errorMessage = <any>error;
            this.errorService.showError(this.translate.instant('GENERAL.ERRORS.FETCHING-RESULT'));
          }
        );
    }
  }

  /**
   * Loads the user data meeting given criteria.
   * @param searchValue Filters to be applied.
   */
  onPickerSearch(searchValue: string) {
    let searchField: ISearchField = {
      fieldName: 'userName',
      operator: operatorType.Contains,
      searchValue: searchValue ? searchValue : '',
    };
    this.searchValuePicker = [searchField];
    this.getUsers();
  }

  showDetails() {
    alert('ok');
  }
}
