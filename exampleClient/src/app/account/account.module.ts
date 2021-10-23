import { NgModule } from '@angular/core';

import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './reset-password/reset-password.component';
import { UpdatePasswordComponent } from './update-password/update-password.component';
import { UpdateProfileComponent } from './update-profile/update-profile.component';
import { RegisterComponent } from 'src/app/account/register/register.component';
import { ConfirmEmailComponent } from 'src/app/account/confirm-email/confirm-email.component';
import { RegisterCompleteComponent } from 'src/app/account/register/register-complete/register-complete.component';
import { SharedModule } from 'src/app/common/shared';
import { routingModule } from './account.routing';

@NgModule({
  declarations: [
    ForgotPasswordComponent,
    UpdatePasswordComponent,
    ResetPasswordComponent,
    UpdateProfileComponent,
    RegisterComponent,
    ConfirmEmailComponent,
    RegisterCompleteComponent,
  ],
  exports: [
    ForgotPasswordComponent,
    UpdatePasswordComponent,
    ResetPasswordComponent,
    UpdateProfileComponent,
    RegisterComponent,
    ConfirmEmailComponent,
    RegisterCompleteComponent,
  ],
  imports: [routingModule, SharedModule],
})
export class AccountModule {}
