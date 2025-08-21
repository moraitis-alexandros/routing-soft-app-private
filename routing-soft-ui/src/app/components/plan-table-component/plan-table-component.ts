import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatCardModule } from '@angular/material/card'; // <-- import MatCardModule
import { PlanServiceComponent } from '../../shared/services/plan-service-component';
import { PlanReadonlyDto } from '../../shared/interfaces/plan-readonly-dto';
import { PlanReadonlyViewComponent } from '../plan-readonly-view/plan-readonly-view';
import { MatDialog } from '@angular/material/dialog';
import { PlanReadOnlyNodes } from '../plan-read-only-nodes/plan-read-only-nodes';
import { WaitDialogComponent } from '../app-wait-dialog/app-wait-dialog';

@Component({
  selector: 'app-plan-table-component',
  standalone: true,
  imports: [CommonModule, MatTableModule, MatButtonModule, MatCardModule],
  templateUrl: './plan-table-component.html',
  styleUrl: './plan-table-component.css'
})
export class PlanTableComponent implements OnInit{
private planService = inject(PlanServiceComponent);
  private snackBar = inject(MatSnackBar);

  private dialog = inject(MatDialog);

  plans: PlanReadonlyDto[] = [];
  displayedColumns: string[] = ['algorithmSpec', 'timeslotLength', 'planStatus', 'createdAt', 'actions'];

  ngOnInit() {
    this.loadPlans();
  }

  loadPlans() {
    this.planService.getAllPlans().subscribe({
      next: (data) => this.plans = data,
      error: (err) => {
        console.error(err);
        this.snackBar.open('Failed to load plans', 'Close', { duration: 3000, panelClass: ['snackbar-error'] });
      }
    });
  }

  deletePlan(planId: number) {
    this.planService.deletePlan(planId).subscribe({
      next: () => {
        this.snackBar.open('Plan deleted successfully', 'Close', { duration: 3000, panelClass: ['snackbar-success'] });
        this.plans = this.plans.filter(t => t.id !== planId); // remove from local array
      },
      error: (err) => {
        console.error(err);
        this.snackBar.open('Failed to delete Plan', 'Close', { duration: 3000, panelClass: ['snackbar-error'] });
      }
    });
  }

viewPlan(plan: PlanReadonlyDto) {
  this.dialog.open(PlanReadOnlyNodes, {
    width: '90%',          // wider dialog
    maxWidth: '1200px',    // maximum width
    height: '90%',         // taller dialog
    maxHeight: '90vh',     // maximum height in viewport
    data: plan
  });
}

executePlan(planId: number) {
  // Open the wait dialog
  const waitDialogRef = this.dialog.open(WaitDialogComponent, {
    width: '300px',
    disableClose: true // prevent closing while processing
  });

  this.planService.executePlan(planId).subscribe({
    next: (solvedPlan) => {
      waitDialogRef.close(); // close the wait dialog

      this.snackBar.open('Plan executed successfully', 'Close', { duration: 3000, panelClass: ['snackbar-success'] });

      // Open readonly view dialog with the solved plan
      this.dialog.open(PlanReadonlyViewComponent, {
        width: '90%',
        maxWidth: '1200px',
        height: '90%',
        maxHeight: '90vh',
        data: solvedPlan
      });
    },
    error: (err) => {
      waitDialogRef.close(); // close the wait dialog even if there's an error
      console.error(err);
      this.snackBar.open('Failed to execute plan', 'Close', { duration: 3000, panelClass: ['snackbar-error'] });
    }
  });
}

}