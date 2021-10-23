export interface IApiHistory {
  id?: number;
  correlation?: string;
  path?: string;
  processTime?: number;
  requestTime?: string;
  responseTime?: string;
  responseStatus?: string;
  userName?: string;
  method?: string;
  contentType?: string;
  query?: string;
  scheme?: string;
  header?: string;
  body?: string;
  browser?: string;
  clientAddress?: string;
  response_status?: number;
  response?: string;
}
