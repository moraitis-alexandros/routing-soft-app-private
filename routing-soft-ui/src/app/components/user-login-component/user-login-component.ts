import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserServiceComponent } from '../../shared/services/user-service-component';
import { UserLoginDto } from '../../shared/interfaces/user-login-dto';
import { Router, RouterModule } from '@angular/router';
import { jwtDecode } from 'jwt-decode';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { WaitDialogComponent } from '../app-wait-dialog/app-wait-dialog';

@Component({
  selector: 'app-user-login-component',
  standalone: true,
  imports: [ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatButtonModule, RouterModule],
  templateUrl: './user-login-component.html',
  styleUrls: ['./user-login-component.css']
})
export class UserLoginComponent {
  private userService = inject(UserServiceComponent);
  private router = inject(Router);
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);

  loginForm = new FormGroup({
    username: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required, Validators.minLength(4)])
  });

  onSubmit() {
    if (this.loginForm.invalid) return;

    // Open wait dialog
    const waitDialogRef = this.dialog.open(WaitDialogComponent, {
      width: '300px',
      disableClose: true
    });

    const credentials = this.loginForm.value as UserLoginDto;

    this.userService.loginUser(credentials).subscribe({
      next: (response) => {
        waitDialogRef.close();

        const access_token = response.token;
        localStorage.setItem('access_token', access_token);

        const decodedToken = jwtDecode<{ role: string; sub: string }>(access_token);

        this.userService.user$.set({
          username: decodedToken.sub,
          role: decodedToken.role
        });

        this.snackBar.open('Login successful', 'Close', { duration: 3000, panelClass: ['snackbar-success'] });
        this.router.navigate(['plan-list-component']);
      },
      error: (error) => {
        waitDialogRef.close();
        console.error("Login failed", error);
        this.snackBar.open(error.error?.message || 'Login failed', 'Close', { duration: 3000, panelClass: ['snackbar-error'] });
      }
    });
  }
}
