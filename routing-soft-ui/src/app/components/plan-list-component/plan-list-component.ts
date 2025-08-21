import { Component, inject, ViewChild } from '@angular/core';
import { PlanTableComponent } from '../plan-table-component/plan-table-component';
import { PlanFormComponent } from '../plan-form-component/plan-form-component';
import { MatButtonModule } from '@angular/material/button';
import { NgIf } from '@angular/common';


@Component({
  selector: 'app-plan-list-component',
  standalone: true,
  imports: [PlanTableComponent,
    PlanFormComponent, MatButtonModule,NgIf
  ],
  templateUrl: './plan-list-component.html',
  styleUrl: './plan-list-component.css'
})
export class PlanListComponent {
  showForm = false;


@ViewChild(PlanTableComponent)
planTable!: PlanTableComponent;

  toggleForm() {
    this.showForm = !this.showForm;
  }

  onPlanAdded() {
    this.showForm = false;
    this.planTable.loadPlans();
  }
}