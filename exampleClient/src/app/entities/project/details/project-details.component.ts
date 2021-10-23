import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';

import { ProjectService } from '../project.service';
import { IProject } from '../iproject';

import { BaseDetailsComponent, PickerDialogService } from 'src/app/common/shared';
import { ErrorService } from 'src/app/core/services/error.service';
import { GlobalPermissionService } from 'src/app/core/services/global-permission.service';

import { CustomerService } from 'src/app/entities/customer/customer.service';

@Component({
  selector: 'app-project-details',
  templateUrl: './project-details.component.html',
  styleUrls: ['./project-details.component.scss'],
})
export class ProjectDetailsComponent extends BaseDetailsComponent<IProject> implements OnInit {
  title = 'Project';
  parentUrl = 'project';
  constructor(
    public formBuilder: FormBuilder,
    public router: Router,
    public route: ActivatedRoute,
    public dialog: MatDialog,
    public projectService: ProjectService,
    public pickerDialogService: PickerDialogService,
    public errorService: ErrorService,
    public customerService: CustomerService,
    public globalPermissionService: GlobalPermissionService
  ) {
    super(formBuilder, router, route, dialog, pickerDialogService, projectService, errorService);
  }

  ngOnInit() {
    this.entityName = 'Project';
    this.setAssociations();
    super.ngOnInit();
    this.setForm();
    this.getItem();
  }

  setForm() {
    this.itemForm = this.formBuilder.group({
      description: [''],
      enddate: ['', Validators.required],
      id: [{ value: '', disabled: true }, Validators.required],
      name: ['', Validators.required],
      startdate: ['', Validators.required],
      customerid: ['', Validators.required],
      customerDescriptiveField: ['', Validators.required],
    });

    this.fields = [
      {
        name: 'startdate',
        label: 'startdate',
        isRequired: true,
        isAutoGenerated: false,
        type: 'date',
      },
      {
        name: 'name',
        label: 'name',
        isRequired: true,
        isAutoGenerated: false,
        type: 'string',
      },
      {
        name: 'enddate',
        label: 'enddate',
        isRequired: true,
        isAutoGenerated: false,
        type: 'date',
      },
      {
        name: 'description',
        label: 'description',
        isRequired: false,
        isAutoGenerated: false,
        type: 'string',
      },
    ];
  }

  onItemFetched(item: IProject) {
    this.item = item;
    this.itemForm.patchValue(item);
    this.itemForm.get('enddate').setValue(item.enddate ? new Date(item.enddate) : null);
    this.itemForm.get('startdate').setValue(item.startdate ? new Date(item.startdate) : null);
  }

  setAssociations() {
    this.associations = [
      {
        column: [
          {
            key: 'customerid',
            value: undefined,
            referencedkey: 'customerid',
          },
        ],
        isParent: false,
        table: 'customer',
        type: 'ManyToOne',
        label: 'customer',
        service: this.customerService,
        descriptiveField: 'customerDescriptiveField',
        referencedDescriptiveField: 'customerid',
      },
      {
        column: [
          {
            key: 'projectid',
            value: undefined,
            referencedkey: 'id',
          },
        ],
        isParent: true,
        table: 'task',
        type: 'OneToMany',
        label: 'tasks',
      },
    ];

    this.childAssociations = this.associations.filter((association) => {
      return association.isParent;
    });

    this.parentAssociations = this.associations.filter((association) => {
      return !association.isParent;
    });
  }

  onSubmit() {
    let project = this.itemForm.getRawValue();
    super.onSubmit(project);
  }
}
