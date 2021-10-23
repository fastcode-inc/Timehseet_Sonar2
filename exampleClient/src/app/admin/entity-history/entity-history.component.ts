import { Component, OnInit } from '@angular/core';
import { IEntityHistory } from './entityHistory';
import { EntityHistoryService } from './entity-history.service';
import { MatTableDataSource } from '@angular/material/table';
import { Observable } from 'rxjs';

import { ISearchField, operatorType } from 'src/app/common/shared';
import { ErrorService } from 'src/app/core/services/error.service';
import { Globals } from 'src/app/core/services/globals';
import { FormBuilder, FormGroup } from '@angular/forms';

import { IUsers } from 'src/app/admin/user-management/users/index';
import { UsersExtendedService } from 'src/app/extended/admin/user-management/users/index';
import { TranslateService } from '@ngx-translate/core';
import { Entities, AuthEntities } from 'src/app/core/main-nav/entities';

enum listProcessingType {
  Replace = 'Replace',
  Append = 'Append',
}

@Component({
  selector: 'app-entity-history',
  templateUrl: './entity-history.component.html',
  styleUrls: ['./entity-history.component.scss'],
})
export class EntityHistoryComponent implements OnInit {
  title: string = this.translate.instant('MainNav.EntityHistory');
  entityHistory: IEntityHistory[] = [];
  itemsObservable: Observable<IEntityHistory[]>;
  errorMessage: '';
  displayedColumns: string[] = [
    'entity',
    'cdoId',
    'changeType',
    'author',
    'commitDate',
    'propertyName',
    'previousValue',
    'currentValue',
  ];

  public dataSource;
  userList: IUsers[] = [];
  entityList = [...Entities, ...AuthEntities];

  filterFields = [];
  basicFilterForm: FormGroup;

  constructor(
    public global: Globals,
    public entityHistoryService: EntityHistoryService,
    public errorService: ErrorService,
    public usersService: UsersExtendedService,
    public formBuilder: FormBuilder,
    public translate: TranslateService
  ) {}

  ngOnInit() {
    this.manageScreenResizing();
    this.getEntityHistory();

    this.basicFilterForm = this.formBuilder.group({
      author: [''],
      entity: [''],
      from: [''],
      to: [''],
    });

    this.basicFilterForm.get('author').valueChanges.subscribe((value) => this.onPickerSearch(value));
  }

  getEntityHistory() {
    this.isLoadingResults = true;
    this.initializePageInfo();
    this.itemsObservable = this.entityHistoryService.getAll(
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
      //   this.displayedColumns = ['entity', 'cdoId', 'commitDate', 'author'];
      // else
      //   this.displayedColumns = ['entity', 'cdoId', 'changeType', 'author', 'commitDate', 'propertyName', 'previousValue', 'currentValue']
    });
  }

  createSearchString() {
    let searchString: string = '';
    let searchFormValue = this.basicFilterForm.getRawValue();
    if (searchFormValue.author) {
      searchString += 'author=' + searchFormValue.author;
    }

    if (searchFormValue.from) {
      if (searchString.length > 0) {
        searchString += ';';
      }
      let from = new Date(searchFormValue.from);
      searchString += `from=${from.getFullYear()}-${
        from.getMonth() + 1
      }-${from.getDate()} ${from.getHours()}:${from.getMinutes()}:${from.getSeconds()}.${from.getMilliseconds()}`;
    }

    if (searchFormValue.to) {
      if (searchString.length > 0) {
        searchString += ';';
      }
      let to = new Date(searchFormValue.to);
      searchString += `to=${to.getFullYear()}-${
        to.getMonth() + 1
      }-${to.getDate()} ${to.getHours()}:${to.getMinutes()}:${to.getSeconds()}.${to.getMilliseconds()}`;
    }

    return searchString;
  }

  applyFilter() {
    this.searchValue = this.createSearchString();
    this.isLoadingResults = true;
    this.initializePageInfo();
    if (!this.basicFilterForm.value.entity) {
      this.itemsObservable = this.entityHistoryService.getAll(
        this.searchValue,
        this.currentPage * this.pageSize,
        this.pageSize
      );
    } else {
      this.itemsObservable = this.entityHistoryService.getByEntity(
        this.basicFilterForm.value.entity,
        this.searchValue,
        this.currentPage * this.pageSize,
        this.pageSize
      );
    }
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
    if (!this.isLoadingResults && this.hasMoreRecords && this.lastProcessedOffset < this.entityHistory.length) {
      this.isLoadingResults = true;
      if (!this.basicFilterForm.value.entity) {
        this.itemsObservable = this.entityHistoryService.getAll(
          this.searchValue,
          this.currentPage * this.pageSize,
          this.pageSize
        );
      } else {
        this.itemsObservable = this.entityHistoryService.getByEntity(
          this.basicFilterForm.value.entity,
          this.searchValue,
          this.currentPage * this.pageSize,
          this.pageSize
        );
      }
      this.processListObservable(this.itemsObservable, listProcessingType.Append);
    }
  }

  processListObservable(listObservable: Observable<IEntityHistory[]>, type: listProcessingType) {
    listObservable.subscribe(
      (entityHistory) => {
        this.isLoadingResults = false;
        if (type == listProcessingType.Replace) {
          this.entityHistory = entityHistory;
          this.dataSource = new MatTableDataSource(this.entityHistory);
        } else {
          this.entityHistory = this.entityHistory.concat(entityHistory);
          this.dataSource = new MatTableDataSource(this.entityHistory);
        }
        this.updatePageInfo(entityHistory);
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
      .subscribe(
        (items) => {
          this.userList = items;
          this.updatePickerPageInfo(items);
        },
        (error) => {
          this.errorMessage = <any>error;
          this.errorService.showError(this.translate.instant('GENERAL.ERRORS.FETCHING-RESULT'));
        }
      );
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
      fieldName: 'username',
      operator: operatorType.Contains,
      searchValue: searchValue ? searchValue : '',
    };
    this.searchValuePicker = [searchField];
    this.getUsers();
  }
}
