import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { RegisterCompleteComponent } from 'src/app/account/register/register-complete/register-complete.component';

@Component({
  selector: 'app-register-complete',
  templateUrl: './register-complete.component.html',
  styleUrls: ['./register-complete.component.scss'],
})
export class RegisterCompleteExtendedComponent extends RegisterCompleteComponent implements OnInit {
  constructor(public activatedRoute: ActivatedRoute) {
    super(activatedRoute);
  }
}
