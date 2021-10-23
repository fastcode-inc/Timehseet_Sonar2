import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { RegisterExtendedService } from '../register/register.service';
import { ConfirmEmailComponent } from 'src/app/account/confirm-email/confirm-email.component';

@Component({
  selector: 'app-confirm-email',
  templateUrl: './confirm-email.component.html',
  styleUrls: ['./confirm-email.component.scss'],
})
export class ConfirmEmailExtendedComponent extends ConfirmEmailComponent implements OnInit {
  loading = true;
  confirmed = false;
  resetToken = '';
  constructor(
    public router: Router,
    public activatedRoute: ActivatedRoute,
    public registerService: RegisterExtendedService
  ) {
    super(router, activatedRoute, registerService);
  }
}
