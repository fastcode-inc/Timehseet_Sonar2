import { NgModule } from '@angular/core';

// import { EntityHistoryComponent } from 'src/app/admin/entity-history/entity-history.component';
import { ApiHistoryComponent } from 'src/app/admin/api-history/api-history.component';
import { ApiHistoryDetailsComponent } from 'src/app/admin/api-history/api-history-details/api-history-details.component';

import { routingModule } from './admin.routing';
import { SharedModule } from 'src/app/common/shared';

const entities = [
  // EntityHistoryComponent,
  // ApiHistoryComponent,
  // ApiHistoryDetailsComponent,
];

@NgModule({
  declarations: entities,
  exports: entities,
  imports: [routingModule, SharedModule],
})
export class AdminModule {}
