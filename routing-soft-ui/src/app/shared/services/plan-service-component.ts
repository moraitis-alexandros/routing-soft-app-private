import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PlanInsertDto } from '../interfaces/plan-insert-dto';
import { PlanReadonlyDto } from '../interfaces/plan-readonly-dto';
import { environment } from '../../../environments/environment.development';

const API_URL_PLAN = `${environment.apiURL}/plan`;

@Injectable({
  providedIn: 'root'
})
export class PlanServiceComponent {
  constructor(private http: HttpClient) {}

   getAllPlans(): Observable<PlanReadonlyDto[]> {
      return this.http.get<PlanReadonlyDto[]>(`${API_URL_PLAN}/getAll`);
    }

     insertPlan(plan: PlanInsertDto): Observable<PlanReadonlyDto> {
        console.log("ENTERED SERVICE")
        return this.http.post<PlanReadonlyDto>(`${API_URL_PLAN}/insert`, plan);
      }

      deletePlan(planId: number): Observable<PlanReadonlyDto> {
          return this.http.post<PlanReadonlyDto>(`${API_URL_PLAN}/delete/${planId}`, {});
        }

    executePlan(planId: number): Observable<PlanReadonlyDto> {
      return this.http.get<PlanReadonlyDto>(`${API_URL_PLAN}/solve/${planId}`, {}); 
    }     

      checkPlanStatus(planId: number): Observable<{ status: string }> {
          return this.http.get<{ status: string }>(`${API_URL_PLAN}/status/${planId}`);
        }

}
