import { Component, inject, Output, EventEmitter, ViewChild, ElementRef, AfterViewInit, NgZone } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { LocationNodeService } from '../../shared/services/location-node-service';
import { LocationNodeInsertDto } from '../../shared/interfaces/location-node-insert-dto';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import * as L from 'leaflet';

@Component({
  selector: 'app-location-node-form-component',
  standalone: true,
  imports: [ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatButtonModule],
  templateUrl: './locationnode-form-component.html',
  styleUrls: ['./locationnode-form-component.css']
})
export class LocationNodeFormComponent implements AfterViewInit {

  @ViewChild('mapContainer') mapContainer!: ElementRef;
  map!: L.Map;
  marker!: L.Marker;

  @Output() nodeAdded = new EventEmitter<void>();

  private fb = inject(FormBuilder);
  private nodeService = inject(LocationNodeService);
  private snackBar = inject(MatSnackBar);
  private zone = inject(NgZone);

  nodeForm: FormGroup = this.fb.group({
    isSource: [false],
    coordinatesX: [0, [Validators.required]],
    coordinatesY: [0, [Validators.required]],
    capacity: [0, [Validators.required, Validators.min(0)]],
    description: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(255)]]
  });

  submitNode() {
    if (this.nodeForm.valid) {
      const node: LocationNodeInsertDto = this.nodeForm.value;
      this.nodeService.insertNode(node).subscribe({
        next: () => {
          this.snackBar.open('Location node added successfully!', 'Close', { duration: 3000 });
          this.nodeForm.reset({ isSource: false });
          this.nodeAdded.emit();
        },
        error: (err) => {
          this.snackBar.open('Failed to add node. Please try again.', 'Close', { duration: 3000 });
          console.error(err);
        }
      });
    }
  }

  ngAfterViewInit(): void {
    const initialLat = 37.9838; // Athens latitude
    const initialLng = 23.7275; // Athens longitude

    this.map = L.map(this.mapContainer.nativeElement, { keyboard: false })
      .setView([initialLat, initialLng], 13);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap contributors'
    }).addTo(this.map);

    this.marker = L.marker([initialLat, initialLng], { draggable: true }).addTo(this.map);

    // Update form if marker is dragged
    this.marker.on('dragend', (e: L.DragEndEvent) => {
      const latlng = e.target.getLatLng();
      this.updateFormCoordinates(latlng);
    });

    // Move marker on map click and update form
    this.map.on('click', (e: L.LeafletMouseEvent) => {
      this.moveMarker(e.latlng);
      this.updateFormCoordinates(e.latlng);
    });

    // Ensure map renders correctly
    setTimeout(() => {
      this.map.invalidateSize();
    }, 0);
  }

  private moveMarker(latlng: L.LatLng) {
    if (this.marker) {
      this.marker.setLatLng(latlng);
    } else {
      this.marker = L.marker(latlng, { draggable: true }).addTo(this.map);
    }
  }

  private updateFormCoordinates(latlng: L.LatLng) {
    this.nodeForm.patchValue({
      coordinatesX: latlng.lat,
      coordinatesY: latlng.lng
    });
  }
}
