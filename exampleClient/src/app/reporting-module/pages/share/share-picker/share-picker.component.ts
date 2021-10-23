import { Component, OnInit, Input, OnDestroy, Output, EventEmitter } from '@angular/core';
import { Observable, ReplaySubject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { sharingType } from '../ishare-config';
import { operatorType, ISearchField } from 'src/app/common/shared';

@Component({
  selector: 'app-share-picker',
  templateUrl: './share-picker.component.html',
  styleUrls: ['./share-picker.component.scss'],
})
export class SharePickerComponent implements OnInit, OnDestroy {
  @Input() serviceMethod;
  @Input() displayField;
  @Input() shareType: sharingType;
  @Input() primaryKeys: string[];

  @Output() selectionUpdated = new EventEmitter<any>();

  private destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);
  selectedItems: any = {};
  selectedItemsArray: any[] = [];
  items: any = [];
  removable = true;
  constructor() {}

  ngOnInit() {
    this.onPickerSearch('');
  }

  setSelectedItems(ilist) {
    ilist.forEach((item) => {
      if (!Object.keys(this.selectedItems).includes(item.__uniqueIdentifier__)) {
        this.selectedItems[item.__uniqueIdentifier__] =
          item.editable === true ? 'Edit' : item.editable === false ? 'Read' : '';
      }
    });
  }

  onSelectionChange(event, item) {
    if (this.shareType == sharingType.Share) {
      if (event.value) {
        this.selectedItemsArray = this.selectedItemsArray.filter((selectedItem) => {
          return selectedItem.__uniqueIdentifier__ != item.__uniqueIdentifier__;
        });
        this.selectedItemsArray.push({
          ...item,
          editable: event.value == 'Edit',
        });
        this.selectionUpdated.emit(this.selectedItemsArray);
      } else {
        this.remove(item);
      }
    } else {
      this.selectedItemsArray = this.selectedItemsArray.filter((selectedItem) => {
        return selectedItem.__uniqueIdentifier__ != item.__uniqueIdentifier__;
      });
      this.selectedItemsArray.push({
        ...item,
        editable: event.value ? event.value == 'Edit' : null,
      });
      this.selectionUpdated.emit(this.selectedItemsArray);
    }
  }

  remove(item) {
    delete this.selectedItems[item.__uniqueIdentifier__];
    this.selectedItemsArray = this.selectedItemsArray.filter((selectedItem) => {
      return selectedItem.__uniqueIdentifier__ != item.__uniqueIdentifier__;
    });
    this.selectionUpdated.emit(this.selectedItemsArray);
  }

  // to handle composite/different primary keys
  setUniqueIdentifier(items: any[]) {
    items.forEach((item) => {
      let identifier = '';
      this.primaryKeys.forEach((key) => {
        identifier += item[key] + '__';
      });
      item['__uniqueIdentifier__'] = identifier;
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
   * Initializes/Resets paging information of data list
   * of association showing in autocomplete options.
   */
  initializePickerPageInfo() {
    this.hasMoreRecordsPicker = true;
    this.pickerPageSize = 5;
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
      this.lastProcessedOffsetPicker < this.items.length
    ) {
      this.isLoadingPickerResults = true;
      let userObs: Observable<any[]>;
      userObs = this.serviceMethod(
        this.searchValuePicker,
        this.currentPickerPage * this.pickerPageSize,
        this.pickerPageSize
      );
      userObs.pipe(takeUntil(this.destroyed$)).subscribe(
        (items) => {
          this.setUniqueIdentifier(items);
          this.isLoadingPickerResults = false;
          this.items = this.items.concat(items);
          this.updatePickerPageInfo(items);
          if (this.shareType == sharingType.Unshare) {
            this.setSelectedItems(items);
          }
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
        fieldName: this.displayField,
        operator: operatorType.Contains,
        searchValue: searchValue ? searchValue : '',
      };
      this.searchValuePicker = [searchField];
    }

    this.initializePickerPageInfo();
    this.serviceMethod(this.searchValuePicker, this.currentPickerPage * this.pickerPageSize, this.pickerPageSize)
      .pipe(takeUntil(this.destroyed$))
      .subscribe((items) => {
        this.setUniqueIdentifier(items);
        this.items = items;
        this.updatePickerPageInfo(items);
        this.isLoadingPickerResults = false;
        if (this.shareType == sharingType.Unshare) {
          this.setSelectedItems(items);
        }
      });
  }

  ngOnDestroy() {
    this.destroyed$.next(true);
    this.destroyed$.unsubscribe();
  }
}
