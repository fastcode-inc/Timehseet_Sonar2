import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-register-complete',
  templateUrl: './register-complete.component.html',
  styleUrls: ['./register-complete.component.scss'],
})
export class RegisterCompleteComponent implements OnInit {
  email = '';
  constructor(public activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.email = this.activatedRoute.snapshot.queryParams['email'];
  }
}
