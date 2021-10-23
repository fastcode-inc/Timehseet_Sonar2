import { NgModule } from '@angular/core';

import { ForgotPasswordExtendedComponent } from './forgot-password/forgot-password.component';
import { ResetPasswordExtendedComponent } from './reset-password/reset-password.component';
import { UpdatePasswordExtendedComponent } from './update-password/update-password.component';
import { UpdateProfileExtendedComponent } from './update-profile/update-profile.component';
import { RegisterExtendedComponent } from './register/register.component';
import { ConfirmEmailExtendedComponent } from './confirm-email/confirm-email.component';
import { RegisterCompleteExtendedComponent } from './register/register-complete/register-complete.component';
import { SharedModule } from 'src/app/common/shared';
import { routingModule } from './account.routing';
import { AccountModule } from 'src/app/account/account.module';

@NgModule({
  declarations: [
    ForgotPasswordExtendedComponent,
    UpdatePasswordExtendedComponent,
    ResetPasswordExtendedComponent,
    UpdateProfileExtendedComponent,
    RegisterExtendedComponent,
    ConfirmEmailExtendedComponent,
    RegisterCompleteExtendedComponent,
  ],
  exports: [
    ForgotPasswordExtendedComponent,
    UpdatePasswordExtendedComponent,
    ResetPasswordExtendedComponent,
    UpdateProfileExtendedComponent,
    RegisterExtendedComponent,
    ConfirmEmailExtendedComponent,
    RegisterCompleteExtendedComponent,
  ],
  imports: [routingModule, SharedModule, AccountModule],
})
export class AccountExtendedModule {}
