import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder } from '@angular/forms';
import { LoginComponent } from 'src/app/core/login/login.component';
import { AuthenticationService } from 'src/app/core/services/authentication.service';
import { RegisterExtendedService } from 'src/app/extended/account/register/register.service';
import { ErrorService } from 'src/app/core/services/error.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginExtendedComponent extends LoginComponent implements OnInit {
  constructor(
    public formBuilder: FormBuilder,
    public route: ActivatedRoute,
    public router: Router,
    public authenticationService: AuthenticationService,
    public registerService: RegisterExtendedService,
    public errorService: ErrorService
  ) {
    super(formBuilder, route, router, authenticationService, registerService, errorService);
  }
}
