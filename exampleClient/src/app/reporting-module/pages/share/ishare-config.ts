export enum sharingType {
  Unshare = 'Unshare',
  Share = 'Share',
}

export interface IShareConfig {
  resource: string;
  id: number;
  service: any;
  type: sharingType;
}
