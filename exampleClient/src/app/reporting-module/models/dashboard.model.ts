export interface Report {
  id?: string;
  userId?: number;
  title: string;
  description?: string;
  reportType: string;
  ctype: string;
  query: object;
  reportWidth: string;
}

export interface Dashboard {
  id?: string;
  userId?: number;
  title?: string;
  description?: string;
  reportDetails?: Array<Report>;
}
