import { Component, Inject, ChangeDetectorRef } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { PlanReadonlyDto } from '../../shared/interfaces/plan-readonly-dto';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import * as L from 'leaflet';
import { CommonModule, NgIf } from '@angular/common';

// ✅ Fix for missing default Leaflet icons in Angular
delete (L.Icon.Default.prototype as any)._getIconUrl;

L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
  iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
  shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
});

@Component({
  selector: 'app-plan-read-only-nodes',
  standalone: true,
  imports: [CommonModule, MatTableModule, MatButtonModule, MatIconModule, NgIf],
  templateUrl: './plan-read-only-nodes.html',
  styleUrls: ['./plan-read-only-nodes.css']
})
export class PlanReadOnlyNodes {

  plan: PlanReadonlyDto;
  truckColumns: string[] = ['truckId', 'description', 'capacity'];
  showTrucks = false;
  showPlanInfo = false;
  showMap = false;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: PlanReadonlyDto,
    private dialogRef: MatDialogRef<PlanReadOnlyNodes>,
    private cdr: ChangeDetectorRef
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
      // ensure DOM updates first
      this.cdr.detectChanges();
      setTimeout(() => this.initMap(), 100);
    }
  }

  private initMap() {
    if (!this.plan.locationNodeReadOnlyDtoList || this.plan.locationNodeReadOnlyDtoList.length === 0) return;

    const firstNode = this.plan.locationNodeReadOnlyDtoList[0];
    const map = L.map('map').setView([firstNode.coordinatesX, firstNode.coordinatesY], 13);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap contributors'
    }).addTo(map);

    const allCoords: [number, number][] = [];

    this.plan.locationNodeReadOnlyDtoList.forEach((node) => {
      allCoords.push([node.coordinatesX, node.coordinatesY]);

      const markerIcon = node.isSource
        ? L.icon({
            iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-red.png',
            iconSize: [25, 41],
            iconAnchor: [12, 41],
            popupAnchor: [1, -34],
            shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
            shadowSize: [41, 41],
            shadowAnchor: [12, 41],
          })
        : undefined; // ✅ default blue marker will be used

      L.marker([node.coordinatesX, node.coordinatesY], markerIcon ? { icon: markerIcon } : {})
        .addTo(map)
        .bindPopup(`<b>${node.description}</b><br>Capacity: ${node.capacity}`);
    });

    if (allCoords.length > 0) {
      map.fitBounds(L.latLngBounds(allCoords));
    }
  }
}
