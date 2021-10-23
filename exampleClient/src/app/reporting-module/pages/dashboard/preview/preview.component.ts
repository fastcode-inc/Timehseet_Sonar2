import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DashboardService } from '../dashboard.service';
import { IDashboard } from '../idashboard';

@Component({
  selector: 'app-preview',
  templateUrl: './preview.component.html',
  styleUrls: ['./preview.component.scss'],
})
export class PreviewComponent implements OnInit, OnDestroy {
  dashboard: IDashboard;
  constructor(private route: ActivatedRoute, private router: Router, private dashboardService: DashboardService) {}

  ngOnInit() {
    this.route.paramMap.subscribe((params) => {
      const id = params.get('id');
      this.dashboardService.getById(id).subscribe((res) => {
        this.dashboard = res;
      });
    });
  }

  editDashboard(id) {
    this.router.navigate([`reporting/dashboard/edit/${id}`]);
  }
  ngOnDestroy() {}
}
