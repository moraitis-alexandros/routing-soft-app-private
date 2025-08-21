import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatCardModule } from '@angular/material/card'; // <-- import MatCardModule
import { TruckServiceComponent } from '../../shared/services/truck-service-component';
import { TruckReadOnlyDto } from '../../shared/interfaces/truck-readonly-dto';

@Component({
  selector: 'app-truck-table-component',
  standalone: true,
  imports: [CommonModule, MatTableModule, MatButtonModule, MatCardModule], // <-- include here
  templateUrl: './truck-table-component.html',
  styleUrl: './truck-table-component.css'
})

export class TruckTableComponent implements OnInit {

  private truckService = inject(TruckServiceComponent);
  private snackBar = inject(MatSnackBar);

  trucks: TruckReadOnlyDto[] = [];
  displayedColumns: string[] = ['unloadingTime', 'maxSpeed', 'capacity', 'description', 'actions'];

  ngOnInit() {
    this.loadTrucks();
  }

  loadTrucks() {
    this.truckService.getAllTrucks().subscribe({
      next: (data) => this.trucks = data,
      error: (err) => {
        console.error(err);
        this.snackBar.open('Failed to load trucks', 'Close', { duration: 3000, panelClass: ['snackbar-error'] });
      }
    });
  }

  deleteTruck(truckId: number) {
    this.truckService.deleteTruck(truckId).subscribe({
      next: () => {
        this.snackBar.open('Truck deleted successfully', 'Close', { duration: 3000, panelClass: ['snackbar-success'] });
        this.trucks = this.trucks.filter(t => t.id !== truckId); // remove from local array
      },
      error: (err) => {
        console.error(err);
        this.snackBar.open('Failed to delete truck', 'Close', { duration: 3000, panelClass: ['snackbar-error'] });
      }
    });
  }
}
