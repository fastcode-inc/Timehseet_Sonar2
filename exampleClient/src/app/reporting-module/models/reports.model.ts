export interface Measures {
  name: any;
  title: any;
  shortTitle: any;
  cumulativeTotal: boolean;
  cumulative: boolean;
  type: number;
  drillMembers?: Array<string>;
}
export interface Dimensions {
  name: any;
  title: any;
  type: any;
  alias: any;
  shortTitle: any;
  suggestFilterValues: boolean;
}
export interface MetaContent {
  name: any;
  title: any;
  connectedComponent: any;
  measures: Measures;
  dimensions: Dimensions;
  segments: Array<any>;
}

export interface MetaData {
  cubes: Array<MetaContent>;
}

export interface TimeDimension {
  dimension?: string;
  granularity?: string;
  dateRange?: string;
  For?: string;
  By?: string;
}
export interface Filters {
  dimension?: string;
  operator?: string;
  values?: any;
}
export interface QueryParam {
  measures?: Array<string>;
  dimensions?: Array<string>;
  timeDimensions?: Array<TimeDimension>;
  filters?: Array<Filters>;
  order?: any;
}
