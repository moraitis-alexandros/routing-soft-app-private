import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TruckInsertDto } from '../interfaces/truck-insert-dto';
import { TruckReadOnlyDto } from '../interfaces/truck-readonly-dto';
import { environment } from '../../../environments/environment.development';

  const API_URL_TRUCK = `${environment.apiURL}/truck`;

@Injectable({
  providedIn: 'root'
})

export class TruckServiceComponent {
  
  constructor(private http: HttpClient) {}

  getAllTrucks(): Observable<TruckReadOnlyDto[]> {
    return this.http.get<TruckReadOnlyDto[]>(`${API_URL_TRUCK}/getAll`);
  }

  getTrucksByPlan(planId: number): Observable<TruckReadOnlyDto[]> {
    return this.http.get<TruckReadOnlyDto[]>(`${API_URL_TRUCK}/getAll/${planId}`);
  }

  insertTruck(truck: TruckInsertDto): Observable<TruckReadOnlyDto> {
    console.log("ENTERED SERVICE")
    return this.http.post<TruckReadOnlyDto>(`${API_URL_TRUCK}/insert`, truck);
  }

  deleteTruck(truckId: number): Observable<TruckReadOnlyDto> {
    return this.http.post<TruckReadOnlyDto>(`${API_URL_TRUCK}/delete/${truckId}`, {});
  }
}
