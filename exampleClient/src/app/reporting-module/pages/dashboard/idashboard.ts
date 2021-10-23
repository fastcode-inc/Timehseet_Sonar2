import { IReport } from 'src/app/reporting-module/pages/myreports/ireport';
export interface IDashboard {
  id?: number;
  title?: string;
  description?: string;
  reportDetails?: Array<IReport>;
  editable?: boolean;
  isAssignedByRole?: boolean;
  isPublished?: boolean;
  isRefreshed?: boolean;
  isResetted?: boolean;
  ownerSharingStatus?: boolean;
  recipientSharingStatus?: boolean;
  sharedWithMe?: boolean;
  sharedWithOthers?: boolean;
  isShareable?: boolean;
  usersId?: number;
  ownerId?: number;
  usersDescriptiveField?: string;
  ownerDescriptiveField?: string;
}
