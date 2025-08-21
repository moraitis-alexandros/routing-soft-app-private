import { Component, ElementRef, ViewChild, OnInit, AfterViewInit } from '@angular/core';
import * as L from 'leaflet';

@Component({
  selector: 'app-location-map',
  templateUrl: './location-map.html',
  styleUrls: ['./location-map.css']
})
export class LocationMapComponent implements OnInit, AfterViewInit {
  @ViewChild('mapContainer') mapContainer!: ElementRef; // <- Add !
  map!: L.Map; // <- Add !
  marker!: L.Marker; // <- Add !

  constructor() { }

  ngOnInit(): void { }

  ngAfterViewInit(): void {
    this.initMap();
  }

  initMap(): void {
    this.map = L.map(this.mapContainer.nativeElement).setView([51.505, -0.09], 13);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(this.map);

    this.map.on('click', (e: L.LeafletMouseEvent) => {
      this.addMarker(e.latlng);
      this.updateCoordinates(e.latlng);
    });
  }

  addMarker(latlng: L.LatLng): void {
    if (this.marker) {
      this.marker.setLatLng(latlng);
    } else {
      this.marker = L.marker(latlng).addTo(this.map);
    }
  }

  updateCoordinates(latlng: L.LatLng): void {
    console.log('Selected coordinates:', latlng.lat, latlng.lng);
    // Here you can update your form controls
  }
}
