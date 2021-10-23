import { Component, OnInit, ViewChild } from '@angular/core';
import { FormArray, FormBuilder, FormGroup } from '@angular/forms';
import { SchemaFileData } from '../../models/schema.model';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MainService } from '../../services/main.service';
import { MatAccordion } from '@angular/material/expansion';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-schema',
  templateUrl: './schema.component.html',
  styleUrls: ['./schema.component.scss'],
})
export class SchemaComponent implements OnInit {
  @ViewChild(MatAccordion, { static: false }) accordion: MatAccordion;
  allSchemas = [];
  listFlexWidth = 30;
  allData = {};
  allValues = [];
  allFiles: Array<SchemaFileData> = [];
  fileContent: SchemaFileData = {
    content: this.translate.instant('REPORTING.LABELS.REPORT.FILE-CONTENT-TITLE'),
    absPath: '',
    fileName: '',
  };
  postContent: string = '';
  schemaFormGroup: FormGroup;
  selectedSchemas = {};
  selectedTables = [];

  schemaTables = [
    'task',
    'timeofftype',
    'customer',
    'timesheet',
    'rolepermission',
    'users',
    'role',
    'project',
    'usertask',
    'permission',
    'usersrole',
    'timesheetstatus',
    'timesheetdetails',
    'userspermission',
  ];

  constructor(
    private service: MainService,
    private fb: FormBuilder,
    private _snackBar: MatSnackBar,
    private translate: TranslateService
  ) {
    this.schemaFormGroup = this.fb.group({
      schemaList: this.fb.array([]),
    });
  }

  get schemas() {
    return this.schemaFormGroup.get('schemaList') as FormArray;
  }

  addSchemas() {
    this.schemas.push(this.fb.control(''));
  }

  ngOnInit() {
    this.getAllDbTables();
    this.getAllSchemaFiles();
  }

  getAllDbTables() {
    this.service.getDbTablesList().subscribe((res) => {
      this.allSchemas = Object.keys(res.tablesSchema).filter((schema) => schema == 'timesheet');
      for (const k of this.allSchemas) {
        let tableKeys = Object.keys(res.tablesSchema[k]).filter((table) => this.schemaTables.includes(table));
        this.allData[k] = tableKeys;
        this.allValues.push(...tableKeys);
      }
      for (let val of Object.keys(this.allData)) {
        for (let v of this.allData[val]) {
          this.addSchemas();
        }
      }
    });
  }

  getAllSchemaFiles() {
    this.service.getSchemaFilesList().subscribe((res) => {
      for (let v of Object.values(res.files)) {
        this.allFiles[v.fileName] = v;
      }
    });
  }

  switchAll(event) {
    for (let table of this.allData[this.allSchemas[0]]) {
      this.checkSelectedList(this.allSchemas[0], table, event);
    }
  }

  checkSelectedList(s, t, e) {
    this.selectedTables[s + '.' + t] = !!e.checked;
  }

  onClickGenerateSchema() {
    const tablesList = {
      tables: Object.keys(this.selectedTables).filter((key) => this.selectedTables[key]),
    };
    this.service.generateSchema(tablesList).subscribe((res) => {
      this.service.generateAggregatedMeasures().subscribe((res2) => {
        for (let v of Object.values(res2.files)) {
          this.allFiles[v.fileName] = v;
        }
        this.showMessage(this.translate.instant('REPORTING.MESSAGES.SCHEMA-GENERATED'));
      });
    });
  }

  showFile(f: SchemaFileData) {
    this.fileContent = f;
    this.postContent = f.content;
  }

  onClickUpdateSchema() {
    this.fileContent.content = this.postContent;
    this.service.updateSchemaFile(this.fileContent).subscribe((res) => {
      this.getAllSchemaFiles();
      this.showMessage(this.translate.instant('REPORTING.MESSAGES.SCHEMA-FILE-UPDATED'));
    });
  }

  showMessage(msg): void {
    this._snackBar.open(msg, 'close', {
      duration: 2000,
    });
  }
}
