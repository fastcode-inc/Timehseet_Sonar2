import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, Validators, FormGroup } from '@angular/forms';
import { first } from 'rxjs/operators';
import { RegisterService } from '../register/register.service';

import { Globals } from 'src/app/core/services/globals';
import { ErrorService } from 'src/app/core/services/error.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
})
export class RegisterComponent implements OnInit {
  itemForm: FormGroup;
  loading = false;
  submitted = false;

  constructor(
    public formBuilder: FormBuilder,
    public router: Router,
    public global: Globals,
    public errorService: ErrorService,
    public registerService: RegisterService
  ) {}

  ngOnInit() {
    this.setForm();
  }

  setForm() {
    this.itemForm = this.formBuilder.group({
      emailaddress: ['', Validators.required],

      firstname: ['', Validators.required],

      joinDate: [''],

      lastname: ['', Validators.required],

      confirmPassword: ['', Validators.required],
      password: ['', Validators.required],

      username: ['', Validators.required],
    });
  }

  onSubmit() {
    // stop here if form is invalid
    if (this.itemForm.invalid) {
      return;
    }

    this.submitted = true;
    this.loading = true;
    const user = this.itemForm.getRawValue();
    this.registerService
      .register(user, location.origin)
      .pipe(first())
      .subscribe(
        (data) => {
          this.loading = false;
          this.router.navigate(['register-complete'], { queryParams: { email: user.emailaddress } });
        },
        (error) => {
          this.loading = false;
          this.errorService.showError(error);
        }
      );
  }
}
