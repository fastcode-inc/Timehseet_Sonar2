import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ChangeDetectorRef } from '@angular/core';
import { HttpTestingController } from '@angular/common/http/testing';

import { ApiHistoryComponent } from './api-history.component';
import { ApiHistoryService } from './api-history.service';
import { IApiHistory } from './apiHistory';
import { TestingModule } from '../../testing/utils';
import { environment } from '../../environments/environment';
import { ListFiltersComponent } from 'fastCodeCore';
import { AddFilterFieldComponent } from 'fastCodeCore';

describe('ApiHistoryComponent', () => {
  let component: ApiHistoryComponent;
  let fixture: ComponentFixture<ApiHistoryComponent>;
  let httpTestingController: HttpTestingController;
  let ApiHistoryService: ApiHistoryService;
  let baseUrl: string = environment.apiUrl;
  let data: IApiHistory[] = [
    {
      changeType: 'SetChange',
      globalId: {
        entity: 'com.nfinity.fastcode.domain.Authorization.users.UsersEntity',
        cdoId: 3,
      },
      commitMetadata: {
        author: 'unauthenticated',
        properties: [],
        commitDate: '2018-12-05T11:49:31.869',
        id: 142.0,
      },
      property: 'permissions',
    },
    {
      changeType: 'SetChange',
      globalId: {
        entity: 'com.nfinity.fastcode.domain.Authorization.users.UsersEntity',
        cdoId: 2,
      },
      commitMetadata: {
        author: 'unauthenticated',
        properties: [],
        commitDate: '2018-12-05T11:49:31.804',
        id: 141.0,
      },
      property: 'permissions',
    },
  ];

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ApiHistoryComponent, ListFiltersComponent, AddFilterFieldComponent],
      imports: [TestingModule],
      providers: [ApiHistoryService, ChangeDetectorRef],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ApiHistoryComponent);
    httpTestingController = TestBed.inject(HttpTestingController);
    ApiHistoryService = TestBed.inject(ApiHistoryService);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should run #ngOnInit()', async () => {
    httpTestingController = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
    const req = httpTestingController
      .expectOne((req) => req.method === 'GET' && req.url === baseUrl + '/audit/changes')
      .flush(data);
    expect(component.entityHistory.length).toEqual(data.length);
    httpTestingController.verify();
  });

  it('should list entity history', async () => {
    httpTestingController = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
    const req = httpTestingController
      .expectOne((req) => req.method === 'GET' && req.url === baseUrl + '/audit/changes')
      .flush(data);
    expect(component.entityHistory.length).toEqual(data.length);
    fixture.detectChanges();
    httpTestingController.verify();
  });
});
