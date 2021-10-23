import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { SchemaData, SchemaFiles } from '../models/schema.model';
import { MetaData } from '../models/reports.model';
import { environment } from 'src/environments/environment';
@Injectable({
  providedIn: 'root',
})
export class MainService {
  constructor(private http: HttpClient) {}

  getMetaData() {
    return this.http.get<MetaData>(environment.reportingUrl + '/cubejs-api/v1/meta');
  }

  getDbTablesList() {
    return this.http.get<SchemaData>(environment.reportingUrl + '/playground/db-schema');
  }

  getSchemaFilesList() {
    return this.http.get<SchemaFiles>(environment.reportingUrl + '/playground/files');
  }

  generateSchema(tablesList) {
    return this.http.post<SchemaFiles>(environment.reportingUrl + '/playground/generate-schema', tablesList);
  }

  updateSchemaFile(updatedContent) {
    return this.http.post(environment.reportingUrl + '/saveschema', updatedContent);
  }

  generateAggregatedMeasures() {
    return this.http.get<SchemaFiles>(environment.reportingUrl + '/generateAggregatedMeasures');
  }
}
