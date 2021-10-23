import { Component, OnInit, Input, OnChanges, SimpleChanges, ViewChild } from '@angular/core';
import { CubejsClient } from '@cubejs-client/ngx';
import moment from 'moment';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-chart',
  templateUrl: './chart.component.html',
  styleUrls: ['./chart.component.scss'],
})
export class ChartComponent implements OnInit, OnChanges {
  @Input() chartType;
  @Input() ctype = 'line';
  @Input() query;
  @Input() title;

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;

  constructor(private cubejs: CubejsClient, private translate: TranslateService) {}

  error = '';
  ready = false;
  public table = false;
  displayedColumns;
  dataSource;

  showChart = false;
  public chartData;
  public chartLabels;
  public chartOptions: any = {
    responsive: true,
    maintainAspectRatio: false,
  };
  public chartColors;
  public lineChartColors = [
    {
      borderColor: '#7DB3FF',
      backgroundColor: 'rgba(106, 110, 229, .16)',
      fill: false,
    },
  ];

  public areaChartColors = [
    {
      borderColor: '#7DB3FF',
      backgroundColor: 'rgba(106, 110, 229, .16)',
    },
  ];

  public pieChartColors = [
    {
      backgroundColor: [
        '#7DB3FF',
        '#49457B',
        '#FF7C78',
        '#FED3D0',
        '#6F76D9',
        '#9ADFB4',
        '#2E7987',
        '#7DB2FF',
        '#49447B',
        '#FF7678',
        '#FED7D0',
        '#6F72D9',
        '#9AD1B4',
        '#2E7287',
        '#9A55B4',
        '#2E1187',
        '#7D99FF',
        '#49027B',
      ],
    },
  ];
  public barChartColors = [
    {
      borderColor: '#7DB3FF',
      backgroundColor: '#7DB3FF',
    },
    {
      borderColor: '#49457B',
      backgroundColor: '#49457B',
    },
    {
      borderColor: '#FF7C78',
      backgroundColor: '#FF7C78',
    },
  ];

  private dateFormatter = ({ x }) => moment(x).format('MMM DD');
  private numberFormatter = (x) => x.toLocaleString();
  private capitalize = ([first, ...rest]) => first.toUpperCase() + rest.join('').toLowerCase();

  setLineChartData() {
    this.chartOptions = {
      ...this.chartOptions,
      scales: {
        xAxes: [
          {
            ticks: {
              maxTicksLimit: 4,
              maxRotation: 0,
            },
            gridLines: {
              color: 'rgba(0, 0, 0, 0)',
            },
          },
        ],
        yAxes: [
          {
            stacked: true,
            gridLines: {
              color: 'rgba(212, 212, 212, 0.2)',
            },
          },
        ],
      },
      legend: {
        display: false,
        labels: {
          fontColor: '#673ab7',
        },
      },
      fill: false,
    };
    if (this.ctype === 'line') {
      this.chartColors = this.lineChartColors;
    } else {
      this.chartColors = this.areaChartColors;
    }
  }

  setPieChartData() {
    this.chartOptions = {
      responsive: true,
      maintainAspectRatio: false,
      legend: {
        display: false,
      },
    };
    this.chartColors = this.pieChartColors;
  }

  setStackedBarChartData(stacked) {
    this.chartType = 'bar';
    this.chartColors = this.barChartColors;
    this.chartOptions = {
      ...this.chartOptions,
      scales: {
        xAxes: [
          {
            stacked: stacked,
            ticks: {
              maxTicksLimit: 4,
              maxRotation: 0,
            },
          },
        ],
        yAxes: [{ stacked: stacked }],
      },
      legend: {
        position: 'bottom',
      },
    };
  }

  commonSetup(resultSet) {
    this.chartLabels = resultSet.categories().map((c) => c.category);
    this.chartData = resultSet.series().map((s, index) => ({
      label: s.title,
      data: s.series.map((r) => r.value),
    }));
  }

  convertCols(r) {
    return r.map((v) => v.key);
  }

  tableSetup(resultSet) {
    this.chartLabels = resultSet.tableColumns();
    this.chartData = resultSet.tablePivot();
    this.displayedColumns = this.chartLabels;
    this.dataSource = new MatTableDataSource<any>(this.chartData);
    this.dataSource.paginator = this.paginator;
    this.table = true;
  }

  resultChanged(resultSet) {
    if (this.chartType === 'table') {
      this.tableSetup(resultSet);
      this.table = true;
      this.ready = true;
    } else {
      this.commonSetup(resultSet);
      if (this.chartType === 'line') {
        this.setLineChartData();
      } else if (this.chartType === 'pie' || this.chartType === 'doughnut' || this.chartType === 'polarArea') {
        this.setPieChartData();
      } else if (this.chartType === 'stackedBar' || this.chartType === 'bar') {
        let stacked = true;
        if (this.chartType === 'bar') {
          stacked = false;
        }
        this.setStackedBarChartData(stacked);
      } else if (this.chartType === 'singleValue') {
        this.chartData = this.numberFormatter(resultSet.chartPivot()[0][resultSet.seriesNames()[0].key]);
      }
      this.ready = true;
      this.table = false;
    }
  }

  loadChart() {
    this.error = '';
    this.cubejs.load(this.query).subscribe(this.resultChanged, this.errorHandler);
  }

  decideChart(result) {
    this.resultChanged(result);
  }

  errorHandler(error) {
    this.error = error.toString();
  }

  ngOnInit() {
    this.decideChart = this.decideChart.bind(this);
    this.errorHandler = this.errorHandler.bind(this);
    this.resultChanged = this.resultChanged.bind(this);
    this.loadChart();
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.showChart = this.chartType !== 'singleValue' && this.chartType !== 'table';
    if (changes.hasOwnProperty('query')) {
      if (Object.keys(changes.query.currentValue).length > 0) {
        this.query = changes.query.currentValue;
        if (changes.query.previousValue !== undefined) {
          this.chartData = [{ data: [] }];
          this.loadChart();
        }
      } else {
        this.title = '';
        this.showChart = false;
        this.chartData = this.translate.instant('REPORTING.MESSAGES.SELECT-MEASURE-MESSAGE');
      }
    }
  }
}
