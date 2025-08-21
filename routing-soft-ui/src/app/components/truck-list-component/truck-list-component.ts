import { Component, inject, ViewChild } from '@angular/core';
import { TruckTableComponent } from '../truck-table-component/truck-table-component';
import { TruckFormComponent } from '../truck-form-component/truck-form-component';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-truck-list-component',
  standalone: true,
  imports: [
    TruckTableComponent,
    TruckFormComponent,
    MatButtonModule
  ],
  templateUrl: './truck-list-component.html',
  styleUrls: ['./truck-list-component.css']
})
export class TruckListComponent {
  showForm = false;

  @ViewChild(TruckTableComponent) truckTable!: TruckTableComponent;
  @ViewChild(TruckFormComponent) truckForm!: TruckFormComponent;

  toggleForm() {
    this.showForm = !this.showForm;
  }

  onTruckAdded() {
    // Hide the form
    this.showForm = false;

    // Refresh the truck table
    this.truckTable.loadTrucks();
  }
}
