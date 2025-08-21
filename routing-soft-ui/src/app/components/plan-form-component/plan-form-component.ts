import { Component, EventEmitter, Output, inject, OnInit, ViewChild, ElementRef, AfterViewInit, NgZone } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PlanInsertDto } from '../../shared/interfaces/plan-insert-dto';
import { PlanServiceComponent } from '../../shared/services/plan-service-component';
import { TruckServiceComponent } from '../../shared/services/truck-service-component';
import { LocationNodeService } from '../../shared/services/location-node-service';
import { CommonModule } from '@angular/common';
import { TruckReadOnlyDto } from '../../shared/interfaces/truck-readonly-dto';
import { LocationNodeReadonlyDto } from '../../shared/interfaces/location-node-readonly-dto';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import * as L from 'leaflet';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Inject } from '@angular/core';

@Component({
  selector: 'app-plan-form-component',
  standalone: true,
  imports: [ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatButtonModule, CommonModule],
  templateUrl: './plan-form-component.html',
  styleUrls: ['./plan-form-component.css']
})
export class PlanFormComponent implements OnInit, AfterViewInit {

  @Output() onPlanAdded = new EventEmitter<void>();
  @Output() locationsSelected = new EventEmitter<LocationNodeReadonlyDto[]>();

  @ViewChild('mapContainer') mapContainer!: ElementRef;
  map!: L.Map;

  private fb = inject(FormBuilder);
  private planService = inject(PlanServiceComponent);
  private truckService = inject(TruckServiceComponent);
  private locationService = inject(LocationNodeService);
  private snackBar = inject(MatSnackBar);
  private zone = inject(NgZone);

  algorithmOptions: string[] = ['SimpleTSP', 'MultipleVRP', 'MultiScheduling'];

  availableTrucks: TruckReadOnlyDto[] = [];
  availableLocations: LocationNodeReadonlyDto[] = [];
  locationMarkers: Map<number, L.Marker> = new Map();


  
  selectedTrucks: TruckReadOnlyDto[] = [];
  selectedLocations: LocationNodeReadonlyDto[] = [];

  formSubmitted = false;

  planForm: FormGroup = this.fb.group({
    timeslotLength: [60, [Validators.required, Validators.min(1)]],
    algorithmSpec: [this.algorithmOptions[0], Validators.required]
  });

  nodeForm: FormGroup = this.fb.group({
    isSource: [false],
    coordinatesX: [0, [Validators.required]],
    coordinatesY: [0, [Validators.required]],
    capacity: [0, [Validators.required, Validators.min(0)]],
    description: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(255)]]
  });

  ngAfterViewInit(): void {
    const initialLat = 37.9838; // Athens latitude
    const initialLng = 23.7275; // Athens longitude

    this.map = L.map(this.mapContainer.nativeElement, { keyboard: false })
      .setView([initialLat, initialLng], 13);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap contributors'
    }).addTo(this.map);

    // Ensure map renders correctly
    setTimeout(() => {
      this.map.invalidateSize();
    }, 0);
  }
  
  ngOnInit() {
    this.loadAvailableTrucks();
    this.loadAvailableLocations();
  }

  loadAvailableTrucks() {
    this.truckService.getAllTrucks().subscribe({
      next: (data) => this.availableTrucks = data,
      error: (err) => console.error('Error loading trucks', err)
    });
  }

  loadAvailableLocations() {
    this.locationService.getAllNodes().subscribe({
      next: (data) => this.availableLocations = data,
      error: (err) => console.error('Error loading locations', err)
    });
  }

  toggleTruckSelection(event: any, truck: TruckReadOnlyDto) {
    if (event.target.checked) {
      this.selectedTrucks.push(truck);
    } else {
      this.selectedTrucks = this.selectedTrucks.filter(t => t.id !== truck.id);
    }
  }

  toggleLocationSelection(event: any, location: LocationNodeReadonlyDto) {
    if (event.target.checked) {
      this.selectedLocations.push(location);

      // Add a marker for this location
      const latlng = L.latLng(location.coordinatesX, location.coordinatesY);
      const marker = L.marker(latlng).addTo(this.map);
      this.locationMarkers.set(location.id, marker);
    } else {
      this.selectedLocations = this.selectedLocations.filter(l => l.id !== location.id);

      // Remove marker for this location
      const marker = this.locationMarkers.get(location.id);
      if (marker) {
        this.map.removeLayer(marker);
        this.locationMarkers.delete(location.id);
      }
    }
  }

  submitPlan() {
    this.formSubmitted = true;

    if (this.planForm.valid && this.selectedTrucks.length > 0 && this.selectedLocations.length > 0) {
      const plan: PlanInsertDto = {
        timeslotLength: this.planForm.value.timeslotLength,
        algorithmSpec: this.planForm.value.algorithmSpec,
        trucksList: this.selectedTrucks,
        locationNodeList: this.selectedLocations
      };

      this.planService.insertPlan(plan).subscribe({
        next: () => {
          this.snackBar.open('Plan added successfully!', 'Close', {
            duration: 3000,
            panelClass: ['snackbar-success']
          });
          this.planForm.reset({ timeslotLength: 60, algorithmSpec: this.algorithmOptions[0] });
          this.selectedTrucks = [];
          this.selectedLocations = [];

          // Remove all markers from map
          this.locationMarkers.forEach(marker => this.map.removeLayer(marker));
          this.locationMarkers.clear();

          this.formSubmitted = false;
          this.onPlanAdded.emit();
        },
        error: (err) => {
          this.snackBar.open('Failed to add plan. Please try again.', 'Close', {
            duration: 3000,
            panelClass: ['snackbar-error']
          });
          console.error(err);
        }
      });
    }
  }
}
