export interface SchemaData {
  tablesSchema: Object;
}

export interface SchemaFileData {
  fileName: string;
  content: string;
  absPath: string;
}
export interface SchemaFiles {
  files: Array<SchemaFileData>;
}
