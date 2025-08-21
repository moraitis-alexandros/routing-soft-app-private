import { Component, Inject, AfterViewInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { PlanReadonlyDto } from '../../shared/interfaces/plan-readonly-dto';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import * as L from 'leaflet';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-plan-readonly-view',
  standalone: true,
  imports: [CommonModule, MatTableModule, MatButtonModule, MatIconModule],
  templateUrl: './plan-readonly-view.html',
  styleUrls: ['./plan-readonly-view.css']
})
export class PlanReadonlyViewComponent 
// implements AfterViewInit 
{
  plan: PlanReadonlyDto;
  truckColumns: string[] = ['truckId', 'description', 'capacity'];
  showTrucks = false; // <-- toggle trucks table visibility
  showPlanInfo = false;
  showMap = false;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: PlanReadonlyDto,
    private dialogRef: MatDialogRef<PlanReadonlyViewComponent>
  ) {
    this.plan = data;
  }

  close() {
    this.dialogRef.close();
  }

  toggleTrucks() {
    this.showTrucks = !this.showTrucks;
  }

  togglePlanInfo() {
    this.showPlanInfo = !this.showPlanInfo;
  }

    toggleMap() {
    this.showMap = !this.showMap;
  if (this.showMap) {
    setTimeout(() => {
      this.initMap();
    }, 0);
  }
}

private initMap() {
  if (!this.plan.routeDtoList || this.plan.routeDtoList.length === 0) return;

  // Center map on first stop of first route
  const firstStop = this.plan.routeDtoList[0].stops[0];
  const map = L.map('map').setView([firstStop.coordinatesX, firstStop.coordinatesY], 13);

  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; OpenStreetMap contributors'
  }).addTo(map);

  const allCoords: [number, number][] = [];

  this.plan.routeDtoList.forEach(route => {
    // Draw each leg if legCoordinates exist
    if (route.legCoordinates && route.legCoordinates.length > 0) {
      route.legCoordinates.forEach(leg => {
        const latlngs: [number, number][] = leg.map(c => [c[0], c[1]]);
        L.polyline(latlngs, { color: route.color || 'blue', weight: 4, opacity: 0.8 }).addTo(map);
        allCoords.push(...latlngs);
      });
    } else {
      // fallback to straight lines
      const latlngs: [number, number][] = route.stops.map(s => [s.coordinatesX, s.coordinatesY]);
      L.polyline(latlngs, { color: route.color || 'blue', weight: 4, opacity: 0.8 }).addTo(map);
      allCoords.push(...latlngs);
    }

    // Add markers for stops
    route.stops.forEach((stop, idx) => {
      const markerIcon = stop.isSource
        ? L.icon({
            iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-red.png',
            iconSize: [25, 41],
            iconAnchor: [12, 41],
            popupAnchor: [1, -34],
            shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
            shadowSize: [41, 41],
            shadowAnchor: [12, 41]
          })
        : undefined;

      const marker = L.marker([stop.coordinatesX, stop.coordinatesY], markerIcon ? { icon: markerIcon } : undefined)
        .addTo(map)
        .bindPopup(`<b>${route.truckDescription} - Stop ${idx + 1}</b><br>${stop.description}`);
    });
  });

  // Fit map to all coordinates
  if (allCoords.length > 0) {
    const bounds = L.latLngBounds(allCoords);
    map.fitBounds(bounds);
  }
}


}

