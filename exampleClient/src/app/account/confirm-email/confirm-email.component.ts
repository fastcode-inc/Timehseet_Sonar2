import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { RegisterService } from '../register/register.service';

@Component({
  selector: 'app-confirm-email',
  templateUrl: './confirm-email.component.html',
  styleUrls: ['./confirm-email.component.scss'],
})
export class ConfirmEmailComponent implements OnInit {
  loading = true;
  confirmed = false;
  resetToken = '';
  constructor(public router: Router, public activatedRoute: ActivatedRoute, public registerService: RegisterService) {}

  ngOnInit() {
    this.resetToken = this.activatedRoute.snapshot.queryParams['token'];
    this.verifyEmail();
  }

  verifyEmail() {
    if (this.resetToken) {
      this.registerService.verifyEmail(this.resetToken).subscribe(
        (resp) => {
          this.loading = false;
          if (resp) {
            this.confirmed = true;
          }
        },
        (err) => {
          this.loading = false;
        }
      );
    }
  }
}
