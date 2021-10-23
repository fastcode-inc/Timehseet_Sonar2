import { Component, OnInit, OnDestroy, ViewChild, ElementRef } from '@angular/core';
import { QueryParam, Measures, Dimensions, MetaContent } from 'src/app/reporting-module/models/reports.model';
import * as _ from 'lodash';
import { Dashboard } from 'src/app/reporting-module/models/dashboard.model';
import { ActivatedRoute } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AddReportsToDashboardComponent } from 'src/app/reporting-module/modalDialogs/addReportsToDashboard/addReportsToDashboard.component';
import { AddExReportsToDashboardComponent } from 'src/app/reporting-module/modalDialogs/addExReportsToDashboard/addExReportsToDashboard.component';
import { MainService } from 'src/app/reporting-module/services/main.service';
import { SaveReportsComponent } from 'src/app/reporting-module/modalDialogs/saveReports/saveReports.component';
import { ReportService } from '../report.service';
import { DashboardService } from 'src/app/reporting-module/pages/dashboard/dashboard.service';
import { IReport } from '../ireport';
import { IDashboard } from 'src/app/reporting-module/pages/dashboard/idashboard';
import { CubejsClient } from '@cubejs-client/ngx';
import * as CodeMirror from 'codemirror';
import { WindowRef } from './WindowRef';
import sqlFormatter from 'sql-formatter';
import { Globals } from 'src/app/core/services/globals';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-generate-reports',
  templateUrl: './generate-reports.component.html',
  styleUrls: ['./generate-reports.component.scss'],
})
export class GenerateReportComponent implements OnInit, OnDestroy {
  dashboard: Dashboard = {
    title: '',
    description: '',
    reportDetails: [
      {
        title: '',
        reportType: '',
        ctype: '',
        query: {},
        reportWidth: '',
      },
    ],
  };
  allDashboardsList = [];
  allDashboardsData: IDashboard[] = [];

  title = this.translate.instant('REPORTING.LABELS.REPORT.UNTITLED');
  metadata: Array<MetaContent>;
  reOrderMeta = {};
  measures: Array<Measures> = [];
  dimensions: Array<Dimensions> = [];
  segments: Array<any>;
  tableColumns = [];
  measuresChipArray = [];
  selectedTableColumn: any;
  order: any;
  dataTime: Array<Dimensions> = [];
  filters = [];
  selectedFilters = [];
  chartType = 'line';
  query: QueryParam;
  ctype = 'line';
  timeFilter = '';
  timeFilterFor = '';
  timeFilterBy = '';
  filItemVal = '';
  selected = '';
  selectedChart = this.translate.instant('REPORTING.LABELS.REPORT.SELECT-CHART');
  addToDashStatus = false;
  report_id = -1;
  report: IReport;
  jsonQuery = '';
  sql = '';
  devEnvironment = true;
  loading = false;

  aggregations = [
    {
      value: 'count',
      label: this.translate.instant('REPORTING.LABELS.REPORT.AGGREGATIONS-OPTIONS.COUNT'),
    },
    {
      value: 'countDistinct',
      label: this.translate.instant('REPORTING.LABELS.REPORT.AGGREGATIONS-OPTIONS.COUNT-DISTINCT'),
    },
    {
      value: 'countDistinctApprox',
      label: this.translate.instant('REPORTING.LABELS.REPORT.AGGREGATIONS-OPTIONS.COUNT-DISTINCT-APPROX'),
    },
    {
      value: 'min',
      label: this.translate.instant('REPORTING.LABELS.REPORT.AGGREGATIONS-OPTIONS.MIN'),
    },
    {
      value: 'max',
      label: this.translate.instant('REPORTING.LABELS.REPORT.AGGREGATIONS-OPTIONS.MAX'),
    },
    {
      value: 'sum',
      label: this.translate.instant('REPORTING.LABELS.REPORT.AGGREGATIONS-OPTIONS.SUM'),
    },
    {
      value: 'avg',
      label: this.translate.instant('REPORTING.LABELS.REPORT.AGGREGATIONS-OPTIONS.AVG'),
    },
  ];
  generalAggregations = _.clone(this.aggregations);
  timeAggregations = this.aggregations.slice(0, 5);
  stringAggregations = this.aggregations.slice(0, 3);

  timeFilterForList = [
    {
      value: 'All time',
      label: this.translate.instant('REPORTING.LABELS.REPORT.TIME-FILTER-FOR-LIST-OPTIONS.ALL-TIME'),
    },
    {
      value: 'Today',
      label: this.translate.instant('REPORTING.LABELS.REPORT.TIME-FILTER-FOR-LIST-OPTIONS.TODAY'),
    },
    {
      value: 'Yesterday',
      label: this.translate.instant('REPORTING.LABELS.REPORT.TIME-FILTER-FOR-LIST-OPTIONS.YESTERDAY'),
    },
    {
      value: 'This week',
      label: this.translate.instant('REPORTING.LABELS.REPORT.TIME-FILTER-FOR-LIST-OPTIONS.THIS-WEEK'),
    },
    {
      value: 'This month',
      label: this.translate.instant('REPORTING.LABELS.REPORT.TIME-FILTER-FOR-LIST-OPTIONS.THIS-MONTH'),
    },
    {
      value: 'This quarter',
      label: this.translate.instant('REPORTING.LABELS.REPORT.TIME-FILTER-FOR-LIST-OPTIONS.THIS-QUARTER'),
    },
    {
      value: 'This year',
      label: this.translate.instant('REPORTING.LABELS.REPORT.TIME-FILTER-FOR-LIST-OPTIONS.THIS-YEAR'),
    },
    {
      value: 'Last 7 days',
      label: this.translate.instant('REPORTING.LABELS.REPORT.TIME-FILTER-FOR-LIST-OPTIONS.LAST-7-DAYS'),
    },
    {
      value: 'Last 30 days',
      label: this.translate.instant('REPORTING.LABELS.REPORT.TIME-FILTER-FOR-LIST-OPTIONS.LAST-30-DAYS'),
    },
    {
      value: 'Last week',
      label: this.translate.instant('REPORTING.LABELS.REPORT.TIME-FILTER-FOR-LIST-OPTIONS.LAST-WEEK'),
    },
    {
      value: 'Last month',
      label: this.translate.instant('REPORTING.LABELS.REPORT.TIME-FILTER-FOR-LIST-OPTIONS.LAST-MONTH'),
    },
    {
      value: 'Last quarter',
      label: this.translate.instant('REPORTING.LABELS.REPORT.TIME-FILTER-FOR-LIST-OPTIONS.LAST-QUARTER'),
    },
    {
      value: 'Last year',
      label: this.translate.instant('REPORTING.LABELS.REPORT.TIME-FILTER-FOR-LIST-OPTIONS.LAST-YEAR'),
    },
  ];
  timeFilterByList = [
    {
      value: 'w/o grouping',
      label: this.translate.instant('REPORTING.LABELS.REPORT.TIME-FILTER-BY-LIST.W-O-GROUPING'),
    },
    {
      value: 'Hour',
      label: this.translate.instant('REPORTING.LABELS.REPORT.TIME-FILTER-BY-LIST.HOUR'),
    },
    {
      value: 'Day',
      label: this.translate.instant('REPORTING.LABELS.REPORT.TIME-FILTER-BY-LIST.DAY'),
    },
    {
      value: 'Week',
      label: this.translate.instant('REPORTING.LABELS.REPORT.TIME-FILTER-BY-LIST.WEEK'),
    },
    {
      value: 'Month',
      label: this.translate.instant('REPORTING.LABELS.REPORT.TIME-FILTER-BY-LIST.MONTH'),
    },
    {
      value: 'Year',
      label: this.translate.instant('REPORTING.LABELS.REPORT.TIME-FILTER-BY-LIST.YEAR'),
    },
  ];
  generalFilters = {
    equals: this.translate.instant('REPORTING.LABELS.REPORT.FILTERS-GENERAL.EQUALS'),
    notEquals: this.translate.instant('REPORTING.LABELS.REPORT.FILTERS-GENERAL.NOT-EQUALS'),
    set: this.translate.instant('REPORTING.LABELS.REPORT.FILTERS-GENERAL.SET'),
    notSet: this.translate.instant('REPORTING.LABELS.REPORT.FILTERS-GENERAL.NOT-SET'),
  };
  filtersNonStrings = {
    ...this.generalFilters,
    gt: this.translate.instant('REPORTING.LABELS.REPORT.FILTERS-NON-STRINGS.GT'),
    gte: this.translate.instant('REPORTING.LABELS.REPORT.FILTERS-NON-STRINGS.GTE'),
    lt: this.translate.instant('REPORTING.LABELS.REPORT.FILTERS-NON-STRINGS.LT'),
    lte: this.translate.instant('REPORTING.LABELS.REPORT.FILTERS-NON-STRINGS.LTE'),
  };
  filtersStrings = {
    ...this.generalFilters,
    contains: this.translate.instant('REPORTING.LABELS.REPORT.FILTERS-STRINGS.CONTAINS'),
    notContains: this.translate.instant('REPORTING.LABELS.REPORT.FILTERS-STRINGS.NOT-CONTAINS'),
  };

  queryParam = {
    measures: [],
    dimensions: [],
    timeDimensions: [],
    filters: [],
    order: {},
  };
  sqlDoc;
  sqlViewer;
  @ViewChild('sqlViewer', { static: false }) set content(content: ElementRef) {
    if (content) {
      // initially setter gets called with undefined
      if (!this.sqlViewer) {
        this.sqlViewer = content;
        this.setCodeMirror();
      }
    }
  }
  constructor(
    private service: MainService,
    private reportService: ReportService,
    private dashboardService: DashboardService,
    public dialog: MatDialog,
    private _snackBar: MatSnackBar,
    private route: ActivatedRoute,
    private cubejs: CubejsClient,
    private winRef: WindowRef,
    private global: Globals,
    private translate: TranslateService
  ) {}

  ngOnInit() {
    this.manageScreenResizing();
    this.report_id = +this.route.snapshot.paramMap.get('id');
    this.route.params.subscribe((params) => {
      this.report_id = params['id'];
    });
    this.getMetaData();
    this.dashboardService.getAll([], 0, 1000).subscribe((res) => {
      this.allDashboardsData = res;
    });
  }

  measureCriteriaFieldWidth = 30;
  criteriaFieldWidth = 20;
  aggregationFieldWidth = 10;
  timeValueFieldWidth = 15;
  filterFieldWidth = 33;
  filterRowWidth = 60;
  manageScreenResizing() {
    this.global.isSmallDevice$.subscribe((value) => {
      if (value) {
        this.filterFieldWidth = 100;
        this.aggregationFieldWidth = 100;
        this.timeValueFieldWidth = 100;
        this.measureCriteriaFieldWidth = 100;
        this.criteriaFieldWidth = 100;
        this.filterRowWidth = 100;
      }
    });
    this.global.isMediumDevice$.subscribe((value) => {
      if (value) {
        this.filterFieldWidth = 33;
        this.aggregationFieldWidth = 50;
        this.timeValueFieldWidth = 50;
        this.measureCriteriaFieldWidth = 50;
        this.criteriaFieldWidth = 50;
        this.filterRowWidth = 100;
      }
    });
    this.global.isLargeDevice$.subscribe((value) => {
      if (value) {
        this.filterFieldWidth = 33;
        this.aggregationFieldWidth = 10;
        this.timeValueFieldWidth = 15;
        this.measureCriteriaFieldWidth = 30;
        this.criteriaFieldWidth = 20;
        this.filterRowWidth = 60;
      }
    });
  }

  getMetaData() {
    this.service.getMetaData().subscribe(
      (res) => {
        this.metadata = res.cubes;
        for (const m of res.cubes) {
          this.reOrderMeta[m.name] = m;
          if (m.hasOwnProperty('measures')) {
            this.measures.push(...JSON.parse(JSON.stringify(m.measures)));
          }
          if (m.hasOwnProperty('dimensions')) {
            this.dimensions.push(...JSON.parse(JSON.stringify(m.dimensions)));
          }
          // if(m.hasOwnProperty('segments')) {
          //   this.segments.push(...JSON.parse(JSON.stringify(m.segments)));
          // }
        }
        for (let d of this.dimensions) {
          // this.tableColumns.push({ name: d.title, type: d.type });
          let n = d.title.lastIndexOf(d.shortTitle);
          let alias = d.title.substring(0, n) + '- ' + d.title.substring(n);
          this.tableColumns.push({ name: d.title, type: d.type, alias: alias });
          d.alias = alias;
        }
        this.dataTime = this.dimensions.filter((x) => x.type === 'time');
        this.filters.push(...JSON.parse(JSON.stringify(this.measures)));
        this.filters.push(...JSON.parse(JSON.stringify(this.dimensions)));
        if (this.report_id >= 0) {
          this.getReport();
        } else {
          this.initializeReport();
        }
      },
      (err) => {
        console.error('Observer got an error: ' + err);
      }
    );
  }

  getReport() {
    this.reportService.getById(this.report_id).subscribe((report) => {
      this.report = report;
      this.updateReportInfo();
    });
  }

  updateReportInfo() {
    this.title = this.report.title;
    this.query = this.report.query;
    this.chartType = this.report.reportType;
    this.selectedChart = this.report.reportType.charAt(0).toUpperCase() + this.report.reportType.slice(1);
    this.queryParam.measures = this.report.query.measures;
    this.queryParam.dimensions = this.report.query.dimensions;
    this.queryParam.timeDimensions = this.report.query.timeDimensions;
    this.dashboard.description = this.report.description;
    this.measuresChipArray = [];
    if (this.report.query.timeDimensions.length > 0) {
      this.timeFilter = this.report.query.timeDimensions[0].dimension;
      this.timeFilterFor = 'All time';
      // this.timeFilterBy = this.timeFilterByList[this.timeFilterByList.findIndex(x => x.toLowerCase() == this.report.query.timeDimensions[0].granularity)];
      this.timeFilterBy = this.getTimeFilterByObj(this.report.query.timeDimensions[0].granularity);
    }
    for (let i = 0; i < this.queryParam.measures.length; i++) {
      let meta = this.queryParam.measures[i].split('.')[0];
      let aggregatedString = this.queryParam.measures[i].substr(this.queryParam.measures[i].indexOf('.') + 1);
      let aggregate = aggregatedString.split('_')[0];
      let title = aggregatedString.substr(aggregatedString.indexOf('_') + 1);
      title = title.replace(/_/g, ' ');
      title = title
        .toLowerCase()
        .split(' ')
        .map((s) => s.charAt(0).toUpperCase() + s.substr(1))
        .join(' ');
      let dimension = this.tableColumns[this.tableColumns.findIndex((s) => s.name === `${meta} ${title}`)];
      if (dimension.type == 'string') {
        this.aggregations = this.stringAggregations;
      } else if (dimension.type == 'time') {
        this.aggregations = this.timeAggregations;
      } else {
        this.aggregations = this.generalAggregations;
      }
      let chipTitle = `${meta}.${title.replace(/ /g, '_')}`;
      this.measuresChipArray.push({ name: chipTitle, aggregation: this.aggregations, selectedAggregation: aggregate });
    }
  }

  getTimeFilterByObj(value: string) {
    let tfb = this.timeFilterByList.filter((x) => x.value.toLowerCase() == value);
    return tfb.length > 0 ? tfb[0].value : '';
  }

  initializeReport() {
    this.dashboard = {
      title: '',
      description: '',
      reportDetails: [
        {
          title: '',
          reportType: '',
          ctype: '',
          query: {},
          reportWidth: '',
        },
      ],
    };
    this.chartType = 'line';
    this.measuresChipArray = [];
    this.selectedTableColumn = undefined;
    this.query = undefined;
    this.title = this.translate.instant('REPORTING.LABELS.REPORT.UNTITLED');
    this.timeFilter = '';
    this.timeFilterFor = '';
    this.timeFilterBy = '';
    this.selectedChart = this.translate.instant('REPORTING.LABELS.REPORT.SELECT-CHART');
    this.queryParam = {
      measures: [],
      dimensions: [],
      timeDimensions: [],
      filters: [],
      order: {},
    };
  }

  ngOnDestroy() {}

  showLineChart() {
    this.chartType = 'line';
    this.ctype = 'line';
    this.resetDimentions('Line');
  }
  showAreaChart() {
    this.chartType = 'line';
    this.ctype = 'area';
    this.resetDimentions('Area');
  }
  showPieChart() {
    this.chartType = 'pie';
    this.resetDimentions('Pie');
  }
  showDoughnutChart() {
    this.chartType = 'doughnut';
    this.resetDimentions('Doughnut');
  }
  showPolarAreaChart() {
    this.chartType = 'polarArea';
    this.resetDimentions('Polar Area');
  }
  showBarChart() {
    this.chartType = 'bar';
    this.resetDimentions('Bar');
  }
  showStackedBarChart() {
    this.chartType = 'stackedBar';
    this.resetDimentions('Stacked Bar');
  }
  showSingleValue() {
    this.chartType = 'singleValue';
    this.resetDimentions('Number');
  }

  showTable() {
    this.chartType = 'table';
    this.resetDimentions('Table');
  }

  resetDimentions(chart) {
    this.selectedChart = chart;
    if (this.queryParam.timeDimensions.length > 0) {
      if (this.chartType === 'singleValue') {
        this.queryParam.timeDimensions = [{ dimension: this.queryParam.timeDimensions[0].dimension }];
        this.timeFilterBy = 'Week';
      } else {
        let gran = 'week';
        if (this.queryParam.timeDimensions[0].granularity) {
          gran = this.queryParam.timeDimensions[0].granularity;
        }
        this.queryParam.timeDimensions = [
          { dimension: this.queryParam.timeDimensions[0].dimension, granularity: gran },
        ];
      }
    }
    this.buildQuery();
  }

  setMeasure(m) {
    this.selectedTableColumn = m;
    if (this.selectedTableColumn.type == 'string') {
      this.aggregations = this.stringAggregations;
    } else if (this.selectedTableColumn.type == 'time') {
      this.aggregations = this.timeAggregations;
    } else {
      this.aggregations = this.generalAggregations;
    }
    const metakey = this.selectedTableColumn.alias.split('-')[0].replace(/ /g, '');
    let chipMeasure = `${metakey}.${this.selectedTableColumn.name
      .substr(this.selectedTableColumn.alias.indexOf('-'))
      .replace(/ /g, '_')}`;
    if (this.measuresChipArray.includes(chipMeasure) === false) {
      this.measuresChipArray.push({ name: chipMeasure, aggregation: this.aggregations, selectedAggregation: 'count' });
      this.getMeasure('count', this.measuresChipArray.length - 1);
    }
  }

  getMeasure(m, index) {
    let measure;
    let queryMeasure;
    const metakey = this.measuresChipArray[index].name.split('.')[0];
    let column = this.measuresChipArray[index].name
      .substr(this.measuresChipArray[index].name.indexOf('.') + 1)
      .toLowerCase();
    measure = `${m}_${column}`;
    queryMeasure = `${this.measuresChipArray[index].name.split('.')[0]}.${measure}`;

    if (this.queryParam.measures[index]) {
      this.queryParam.measures[index] = queryMeasure;
      this.measuresChipArray[index].selectedAggregation = m;
    } else if (this.queryParam.measures.includes(this.selectedTableColumn.name) === false) {
      this.queryParam.measures.push(queryMeasure);
      this.measuresChipArray[index].selectedAggregation = m;
      const defaultTimeDimension = this.reOrderMeta[metakey].dimensions
        .filter((x) => x.type === 'time')
        .map((y) => y.name);
      if (defaultTimeDimension.length > 0) {
        this.queryParam.timeDimensions = [
          {
            dimension: defaultTimeDimension[0],
            granularity: 'week',
          },
        ];
        this.timeFilter = defaultTimeDimension[0];
        this.timeFilterFor = 'All time';
        this.timeFilterBy = 'Week';
      } else {
        this.showSingleValue();
      }
      this.queryParam.order = {};
    }

    this.buildQuery();
  }

  removeMeasure(i) {
    this.measuresChipArray.splice(i, 1);
    this.queryParam.measures.splice(i, 1);
    if (this.queryParam.measures.length > 0) {
      this.buildQuery();
    } else {
      this.queryParam.timeDimensions = [];
      this.timeFilterFor = '';
      this.timeFilterBy = '';
      this.query = {};
    }
  }

  getDimension(d) {
    if (this.queryParam.dimensions.includes(d.name) === false) {
      this.queryParam.dimensions.push(d.name);
      this.buildQuery();
    }
  }

  removeDimension(i) {
    this.queryParam.dimensions.splice(i, 1);
    this.buildQuery();
  }

  getTimeFilter(e) {
    if (!e) {
      this.removeTimeFilter(0);
      return;
    }
    this.queryParam.timeDimensions = [
      {
        dimension: e,
        granularity: 'week',
      },
    ];
    this.timeFilterFor = 'All time';
    this.timeFilterBy = 'Week';
    this.buildQuery();
  }

  removeTimeFilter(i) {
    this.queryParam.timeDimensions.splice(i, 1);
    this.buildQuery();
  }

  getTimeFilterFor(ff) {
    this.timeFilterFor = ff.value;
    if (ff.value !== 'All time') {
      this.queryParam.timeDimensions[0].dateRange = ff.value;
    } else {
      delete this.queryParam.timeDimensions[0].dateRange;
    }
    this.buildQuery();
  }

  getTimeFilterBy(fb) {
    this.timeFilterBy = fb.value;
    if (fb.value !== 'w/o grouping') {
      this.queryParam.timeDimensions[0].granularity = fb.value.toLowerCase();
    } else {
      delete this.queryParam.timeDimensions[0].granularity;
    }
    this.buildQuery();
  }

  selectFilter(f) {
    this.selectedFilters.push({
      dimension: f.name,
      type: f.type,
    });
    this.queryParam.filters.push({});
  }

  selectOperator(i, e, dim) {
    this.queryParam.filters[i].dimension = dim;
    this.queryParam.filters[i].operator = e.source.value;
    this.queryParam.filters[i].values = [];
    // this.buildQuery();
  }

  removeFilter(i) {
    this.selectedFilters.splice(i, 1);
    this.queryParam.filters.splice(i, 1);
    this.buildQuery();
  }

  addFilterItem(i, v, type) {
    if (type === 'm') {
      this.queryParam.filters[i].values.push(v.target.value);
      this.filItemVal = null;
    } else {
      this.queryParam.filters[i].values = [v.target.value];
    }
    this.buildQuery();
  }

  removeFilterVal(index1, index2) {
    this.queryParam.filters[index1].values.splice(index2, 1);
    this.buildQuery();
  }

  buildQuery() {
    this.query = _.clone(this.queryParam);
  }

  selectedView: string = 'report';
  sqlQuery: string;

  showReport() {
    this.selectedView = 'report';
  }

  showJsonQuery() {
    this.selectedView = 'json';
    this.jsonQuery = JSON.stringify(this.query, undefined, 4).trim();
  }

  showSqlQuery() {
    this.selectedView = 'sql';
    this.cubejs.sql(this.query).subscribe((res) => {
      // this.sqlQuery = res.sql();
      this.sqlQuery = sqlFormatter.format(res.sql());
      // this.setCodeMirror();
      // this.sqlDoc.setValue(this.sqlQuery);
    });
  }

  setCodeMirror() {
    const mime = 'text/x-mariadb';
    const currentWindow = this.winRef.nativeWindow;
    // get mime type
    // if (currentWindow.location.href.indexOf('mime=') > -1) {
    //   mime = currentWindow.location.href.substr(currentWindow.location.href.indexOf('mime=') + 5);
    // }
    this.sqlDoc = CodeMirror.fromTextArea(this.sqlViewer.nativeElement, {
      mode: mime,
      indentWithTabs: true,
      smartIndent: true,
      lineNumbers: false,
      // readOnly: true,
      // cursorBlinkRate: -1,
      // matchBrackets: true,
      autofocus: true,
      extraKeys: { 'Ctrl-Space': 'autocomplete' },
    });
  }

  asIsOrder(a, b) {
    return 1;
  }

  refreshChart() {
    this.buildQuery();
  }

  saveReportDialog(): void {
    const dialogRef = this.dialog.open(SaveReportsComponent, {
      panelClass: 'fc-modal-dialog',
      data: {
        title: this.title,
        description: this.dashboard.description,
      },
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.loading = true;
        if (!this.report) {
          this.report = {
            title: result.reportTitle,
            description: result.reportdescription,
            reportType: this.chartType,
            ctype: this.ctype,
            query: this.query,
          };
        } else {
          this.report.title = result.reportTitle;
          this.report.description = result.reportdescription;
          this.report.reportType = this.chartType;
          this.report.ctype = this.ctype;
          this.report.query = this.query;
          this.report.id = this.report_id;
        }
        if (this.report_id > -1) {
          this.reportService.update(this.report, this.report_id).subscribe((res) => {
            this.report = res;
            this.updateReportInfo();
            this.loading = false;
            this.showMessage(this.translate.instant('REPORTING.MESSAGES.REPORT.UPDATED'));
          });
        } else {
          this.reportService.create(this.report).subscribe((res) => {
            if (res) {
              this.showMessage(this.translate.instant('REPORTING.MESSAGES.REPORT.CREATED'));
              this.report = res;
              this.report_id = res.id;
              this.updateReportInfo();
              this.loading = false;
            }
          });
        }
      }
    });
  }

  addReporttoDashboardDialog(): void {
    this.allDashboardsList = this.allDashboardsData.map((v) => {
      return {
        id: v.id,
        title: v.title,
      };
    });
    if (this.report_id > 0) {
      const dialogRef = this.dialog.open(AddExReportsToDashboardComponent, {
        panelClass: 'fc-modal-dialog',
        data: this.allDashboardsList,
      });

      dialogRef.afterClosed().subscribe((result) => {
        if (result) {
          this.loading = true;
          if (result.type === 'new') {
            const dashboardDetails = {
              usersId: this.report.usersId,
              title: result.title,
              description: result.description,
              reportDetails: [
                {
                  id: this.report.id,
                  reportWidth: result.chartSize,
                },
              ],
            };
            this.dashboardService.addExistingReportToNewDashboard(dashboardDetails).subscribe((res) => {
              this.showMessage(`${this.translate.instant('REPORTING.MESSAGES.REPORT.ADDED-TO')} ${res.title}`);
              this.loading = false;
            });
          } else {
            const dashboardDetails = {
              id: result.id,
              usersId: this.report.usersId,
              reportDetails: [
                {
                  id: this.report.id,
                  reportWidth: result.chartSize,
                },
              ],
            };
            this.dashboardService.addExistingReportToExistingDashboard(dashboardDetails).subscribe((res) => {
              this.showMessage(`${this.translate.instant('REPORTING.MESSAGES.REPORT.ADDED-TO')} ${res.title}`);
              this.loading = false;
            });
          }
        }
      });
    } else {
      const dialogRef = this.dialog.open(AddReportsToDashboardComponent, {
        panelClass: 'fc-modal-dialog',
        data: this.allDashboardsList,
      });

      dialogRef.afterClosed().subscribe((result) => {
        if (result) {
          this.loading = true;
          if (result.type === 'new') {
            this.dashboard = {
              title: result.title,
              description: result.description,
              reportDetails: [
                {
                  title: result.chartTitle,
                  description: result.reportdescription,
                  reportType: this.chartType,
                  ctype: this.ctype,
                  query: this.query,
                  reportWidth: result.chartSize,
                },
              ],
            };
            this.dashboardService.addNewReporttoNewDashboard(this.dashboard).subscribe((res) => {
              this.allDashboardsData.push(res);
              this.report = res.reportDetails[0];
              this.report_id = res.reportDetails[0].id;
              this.updateReportInfo();
              this.loading = false;
              this.showMessage(`${this.translate.instant('REPORTING.MESSAGES.REPORT.ADDED-TO')} ${res.title}`);
            });
          } else {
            const chartDetails: Dashboard = {
              id: result.title,
              reportDetails: [
                {
                  title: result.chartTitle,
                  description: result.reportdescription,
                  reportType: this.chartType,
                  ctype: this.ctype,
                  query: this.query,
                  reportWidth: result.chartSize,
                },
              ],
            };
            this.dashboardService.addNewReporttoExistingDashboard(chartDetails).subscribe((res) => {
              this.report = res.reportDetails[0];
              this.report_id = res.reportDetails[0].id;
              this.updateReportInfo();
              this.loading = false;
              this.showMessage(`${this.translate.instant('REPORTING.MESSAGES.REPORT.ADDED-TO')} ${res.title}`);
            });
          }
        }
      });
    }
  }

  showMessage(msg): void {
    this._snackBar.open(msg, 'close', {
      duration: 2000,
    });
  }
}
