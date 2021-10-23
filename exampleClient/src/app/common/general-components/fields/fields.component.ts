import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { IAssociationEntry } from 'src/app/common/shared';

@Component({
  selector: 'fc-fields',
  templateUrl: './fields.component.html',
  styleUrls: ['./fields.component.scss'],
})
export class FieldsComponent implements OnInit {
  @Input() itemForm: FormGroup;
  @Input() fields: any[];
  @Input() parentAssociations: IAssociationEntry[];
  @Output() onPickerScroll: EventEmitter<any> = new EventEmitter();
  @Output() onSelectAssociation: EventEmitter<any> = new EventEmitter();

  @Input() childAssociations;
  @Input() IsUpdatePermission;
  @Input() loading;
  @Output() onBack: EventEmitter<any> = new EventEmitter();
  @Output() onSubmit: EventEmitter<any> = new EventEmitter();
  @Output() openChildDetails: EventEmitter<any> = new EventEmitter();

  constructor(public formBuilder: FormBuilder) {}

  ngOnInit() {}

  pickerScroll(association) {
    this.onPickerScroll.emit(association);
  }

  selectAssociation(association) {
    this.onSelectAssociation.emit(association);
  }

  back() {
    this.onBack.emit();
  }

  submit() {
    this.onSubmit.emit();
  }

  openDetails(association) {
    this.openChildDetails.emit(association);
  }
}
