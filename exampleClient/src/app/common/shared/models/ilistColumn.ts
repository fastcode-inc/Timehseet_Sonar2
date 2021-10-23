export enum ListColumnType {
  String = 'String',
  Number = 'Number',
  Date = 'Date',
  DateTime = 'DateTime',
  Boolean = 'Boolean',
}
export interface IListColumn {
  column: string;
  searchColumn?: string;
  label: string;
  sort: boolean;
  filter: boolean;
  type: ListColumnType;
  options?: Array<any>;
}
