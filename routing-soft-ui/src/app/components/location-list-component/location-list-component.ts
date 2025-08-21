import { Component, inject, ViewChild } from '@angular/core';
import { LocationNodeFormComponent } from '../locationnode-form-component/locationnode-form-component';
import { LocationNodeTableComponent } from '../locationnode-table-component/locationnode-table-component';
import { MatButtonModule } from '@angular/material/button';
import { NgIf } from '@angular/common';

@Component({
  selector: 'location-list-component',
  standalone: true,
  imports: [MatButtonModule, LocationNodeFormComponent, LocationNodeTableComponent,NgIf],
  templateUrl: './location-list-component.html',
  styleUrl: './location-list-component.css'
})
export class LocationListComponent {

  showForm = false;

  @ViewChild(LocationNodeTableComponent) nodeTable!: LocationNodeTableComponent;
  @ViewChild(LocationNodeFormComponent) nodeForm!: LocationNodeFormComponent;

  toggleForm() {
    this.showForm = !this.showForm;
  }

  nodeAdded() {
    this.showForm = false;
    this.nodeTable.loadNodes();
  }
}
