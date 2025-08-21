import { Component } from '@angular/core';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-wait-dialog',
  template: `
    <div style="text-align:center; padding: 20px;">
      <h3>Please wait...</h3>
      <mat-progress-spinner mode="indeterminate"></mat-progress-spinner>
    </div>
  `,
  standalone: true,
  imports: [MatProgressSpinnerModule],  // <-- make sure this is here
})
export class WaitDialogComponent {}
