import { RouterModule, Routes } from '@angular/router';
import { ModuleWithProviders } from '@angular/core';
import { CanDeactivateGuard } from 'src/app/core/guards/can-deactivate.guard';
import { AuthGuard } from 'src/app/core/guards/auth.guard';

import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './reset-password/reset-password.component';
import { UpdatePasswordComponent } from './update-password/update-password.component';
import { UpdateProfileComponent } from './update-profile/update-profile.component';
import { RegisterComponent } from 'src/app/account/register/register.component';
import { ConfirmEmailComponent } from 'src/app/account/confirm-email/confirm-email.component';
import { RegisterCompleteComponent } from 'src/app/account/register/register-complete/register-complete.component';
const routes: Routes = [
  /*
	{ path: "update-profile", component: UpdateProfileComponent, canActivate: [AuthGuard], canDeactivate: [CanDeactivateGuard] },
	{ path: 'forgot-password', component: ForgotPasswordComponent },
	{ path: 'reset-password', component: ResetPasswordComponent },
	{ path: 'update-password', component: UpdatePasswordComponent, canActivate: [AuthGuard] },
	{ path: 'register', component: RegisterComponent },
	{ path: 'register-complete', component: RegisterCompleteComponent },
	{ path: 'verify-email', component: ConfirmEmailComponent },
	*/
];

export const routingModule: ModuleWithProviders<any> = RouterModule.forChild(routes);
