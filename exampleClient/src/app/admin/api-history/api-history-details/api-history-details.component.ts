import { Location } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { IApiHistory } from '../apiHistory';

@Component({
  selector: 'app-api-history-details',
  templateUrl: './api-history-details.component.html',
  styleUrls: ['./api-history-details.component.css'],
})
export class ApiHistoryDetailsComponent implements OnInit {
  apiHistoryDetails: IApiHistory;
  constructor(private location: Location) {}

  ngOnInit() {
    this.apiHistoryDetails = this.location.getState();
    console.log(this.apiHistoryDetails);
  }
}
