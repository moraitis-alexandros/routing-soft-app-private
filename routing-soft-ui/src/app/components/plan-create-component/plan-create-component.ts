import { Component, ViewChild, ElementRef, OnInit } from '@angular/core';
import * as L from 'leaflet';
import { LocationNodeReadonlyDto } from '../../shared/interfaces/location-node-readonly-dto';
import { PlanFormComponent } from '../plan-form-component/plan-form-component';

@Component({
  selector: 'app-plan-create',
  templateUrl: './plan-create-component.html',
  imports: [PlanFormComponent],
  styleUrls: ['./plan-create-component.css']
})
export class PlanCreateComponent implements OnInit {
  private map!: L.Map;
  private markers: L.Marker[] = [];

  @ViewChild('map', { static: true }) mapContainer!: ElementRef;

  ngOnInit() {
    this.map = L.map(this.mapContainer.nativeElement).setView([0, 0], 2);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap contributors'
    }).addTo(this.map);
  }

  onLocationsSelected(locations: LocationNodeReadonlyDto[]) {
    // Remove old markers
    this.markers.forEach(m => this.map.removeLayer(m));
    this.markers = [];

    // Add new markers
    locations.forEach(loc => {
      const marker = L.marker([loc.coordinatesY, loc.coordinatesX]).addTo(this.map);
      marker.bindPopup(loc.description);
      this.markers.push(marker);
    });

    // Fit map to markers
    if (this.markers.length) {
      const group = L.featureGroup(this.markers);
      this.map.fitBounds(group.getBounds());
    }
  }

  onPlanAdded() {
  // Optional: you can show a snackbar, clear selections, or do nothing
  console.log('Plan successfully added!');
}

}
