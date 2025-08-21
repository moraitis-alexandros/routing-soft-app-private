import { Component, inject, Output, EventEmitter } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { TruckServiceComponent } from '../../shared/services/truck-service-component';
import { TruckInsertDto } from '../../shared/interfaces/truck-insert-dto';

@Component({
  selector: 'app-truck-form-component',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './truck-form-component.html',
  styleUrls: ['./truck-form-component.css']
})
export class TruckFormComponent {

  @Output() truckAdded = new EventEmitter<void>();

  private fb = inject(FormBuilder);
  private truckService = inject(TruckServiceComponent);
  private snackBar = inject(MatSnackBar);

  truckForm: FormGroup = this.fb.group({
    unloadingTime: [0, [Validators.required, Validators.min(0), Validators.max(60)]],
    maxSpeed: [0, [Validators.required, Validators.min(0), Validators.max(100)]],
    capacity: [0, [Validators.required, Validators.min(0), Validators.max(40000)]],
    description: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(255)]]
  });

  submitTruck() {
    if (this.truckForm.valid) {
      const truck: TruckInsertDto = this.truckForm.value;
      this.truckService.insertTruck(truck).subscribe({
        next: () => {
          this.snackBar.open('Truck added successfully!', 'Close', {
            duration: 3000,
            panelClass: ['snackbar-success']
          });
          this.truckForm.reset();

          // Emit event to parent
          this.truckAdded.emit();
        },
        error: (err) => {
          this.snackBar.open('Failed to add truck. Please try again.', 'Close', {
            duration: 3000,
            panelClass: ['snackbar-error']
          });
          console.error(err);
        }
      });
    }
  }
}
