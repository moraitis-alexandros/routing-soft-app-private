import { Injectable, inject, signal, effect } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment.development';
import { UserLoginDto } from '../interfaces/user-login-dto';
import { UserLoginReadOnlyDto } from '../interfaces/user-login-read-only-dto';
import { UserRegistrationDto } from '../interfaces/user-registration-dto';
import { UserRegistrationReadOnlyDto } from '../interfaces/user-registration-read-only-dto';
import { Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode'; 

const API_URL_REGISTER = `${environment.apiURL}/auth/register`;
const API_URL_AUTH = `${environment.apiURL}/auth`;

interface JwtPayload {
  sub: string;
  role: string;
  exp?: number; 
}

@Injectable({
  providedIn: 'root'
})
export class UserServiceComponent {

  http: HttpClient = inject(HttpClient);
  router = inject(Router);

  user$ = signal<UserLoginReadOnlyDto | null>(null);

  constructor() {
    this.initializeUserFromToken();

    // Log user state reactively
    effect(() => {
      if (this.user$()) {
        console.log('User Logged In:', this.user$()?.username);
      } else {
        console.log('No user logged in');
      }
    });
  }

  public initializeUserFromToken() {
    const access_token = localStorage.getItem("access_token");
    if (!access_token) return; // No token → exit early

    try {
      const decoded = jwtDecode<{ sub: string; role: string; exp: number }>(access_token);

      if (!decoded) throw new Error('Invalid token');

      if (!this.isTokenExpired(decoded)) {
        this.user$.set({
          username: decoded.sub,
          role: decoded.role
        });
      } else {
        // Token expired → clean up
        localStorage.removeItem("access_token");
      }
    } catch (err) {
      console.warn("Failed to decode token:", err);
      localStorage.removeItem("access_token"); // optional cleanup
    }
  }
  

  registerUser(user: UserRegistrationDto) {
    return this.http.post<{ status: boolean, data: UserRegistrationReadOnlyDto }>(
      `${API_URL_REGISTER}`, user
    );
  }

 loginUser(credentials: UserLoginDto) {
  return this.http.post<{ token: string }>(
    `${API_URL_AUTH}/login`,
    credentials
  );
}



  logout() {
    this.user$.set(null);
    localStorage.removeItem('access_token');
    this.router.navigate(['app-user-login-component']);
  }

  isTokenExpired(decoded?: JwtPayload): boolean {
    let payload: JwtPayload | null = decoded || null;

    if (!payload) {
      const token = localStorage.getItem('access_token');
      if (!token) return true;
      try {
        payload = jwtDecode<JwtPayload>(token);
      } catch {
        return true;
      }
    }

    if (payload.exp) {
      const now = Math.floor(Date.now() / 1000);
      return payload.exp < now;
    }

    return true; // Treat token without exp as expired
  }
}
