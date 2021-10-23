import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ITask } from './itask';
import { GenericApiService } from 'src/app/common/shared';

@Injectable({
  providedIn: 'root',
})
export class TaskService extends GenericApiService<ITask> {
  constructor(protected httpclient: HttpClient) {
    super(httpclient, 'task');
  }
}
