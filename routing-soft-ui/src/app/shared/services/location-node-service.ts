import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LocationNodeInsertDto } from '../interfaces/location-node-insert-dto';
import { LocationNodeReadonlyDto } from '../interfaces/location-node-readonly-dto';
import { environment } from '../../../environments/environment.development';

const API_URL_NODE = `${environment.apiURL}/node`;

@Injectable({
  providedIn: 'root'
})
export class LocationNodeService {

  constructor(private http: HttpClient) {}

  getAllNodes(): Observable<LocationNodeReadonlyDto[]> {
    return this.http.get<LocationNodeReadonlyDto[]>(`${API_URL_NODE}/getAll`);
  }

  getNodesByPlan(planId: number): Observable<LocationNodeReadonlyDto[]> {
    return this.http.get<LocationNodeReadonlyDto[]>(`${API_URL_NODE}/getAll/${planId}`);
  }

  insertNode(node: LocationNodeInsertDto): Observable<LocationNodeReadonlyDto> {
    console.log("ENTERED LOCATION NODE SERVICE");
    return this.http.post<LocationNodeReadonlyDto>(`${API_URL_NODE}/insert`, node);
  }

  deleteNode(nodeId: number): Observable<LocationNodeReadonlyDto> {
    return this.http.post<LocationNodeReadonlyDto>(`${API_URL_NODE}/delete/${nodeId}`, {});
  }
}
