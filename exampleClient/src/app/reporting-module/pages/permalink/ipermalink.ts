export interface IPermalink {
  id?: number;
  authentication: string;
  description: boolean;
  refreshRate: number;
  rendering: string;
  resource: string;
  password?: string;
  resourceId: number;
  toolbar: boolean;
  height?: number;
  width?: number;
}
