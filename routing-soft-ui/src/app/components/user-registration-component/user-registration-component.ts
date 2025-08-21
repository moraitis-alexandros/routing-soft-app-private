import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormGroup, FormControl, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { UserServiceComponent } from '../../shared/services/user-service-component';
import { UserRegistrationDto } from '../../shared/interfaces/user-registration-dto';
import { Router, RouterModule } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { WaitDialogComponent } from '../app-wait-dialog/app-wait-dialog';

@Component({
  selector: 'app-user-registration-component',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    RouterModule
  ],
  templateUrl: './user-registration-component.html',
  styleUrls: ['./user-registration-component.css']
})
export class UserRegistrationComponent {
  
  private userService = inject(UserServiceComponent);
  private router = inject(Router);
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);

  registrationStatus: { success: boolean, message: string } = {
    success: false,
    message: 'Not attempted yet'
  };

  // Validator to check if password and confirmPassword match
  passwordConfirmValidator = (form: AbstractControl): ValidationErrors | null => {
    const password = form.get('password')?.value;
    const confirmPassword = form.get('confirmPassword')?.value;
    return password && confirmPassword && password !== confirmPassword
      ? { passwordMismatch: true }
      : null;
  };

  // Form group with validators
  registrationForm = new FormGroup(
    {
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', [Validators.required, Validators.minLength(4)]),
      confirmPassword: new FormControl('', [Validators.required, Validators.minLength(4)])
    },
    { validators: this.passwordConfirmValidator }
  );

  onSubmit() {
    if (this.registrationForm.invalid) {
      this.registrationStatus = { success: false, message: 'Form is invalid' };
      return;
    }

    // Open wait dialog
    const waitDialogRef = this.dialog.open(WaitDialogComponent, {
      width: '300px',
      disableClose: true
    });

    // Map Angular form values to backend DTO
    const data: UserRegistrationDto = {
      username: this.registrationForm.get('email')?.value || '',
      password: this.registrationForm.get('password')?.value || '',
      confirmPassword: this.registrationForm.get('confirmPassword')?.value || '',
      role: 'SIMPLE_USER'
    };

    this.userService.registerUser(data).subscribe({
      next: (response) => {
        waitDialogRef.close(); // ✅ close wait dialog

        console.log('User registered successfully', response);
        this.registrationStatus = { success: true, message: 'User registered successfully' };

        this.snackBar.open('User registered successfully', 'Close', { duration: 3000, panelClass: ['snackbar-success'] });

        this.router.navigate(['app-user-login-component']);
      },
      error: (err) => {
        waitDialogRef.close(); // ✅ close wait dialog even on error

        console.error('Registration failed', err);
        this.registrationStatus = {
          success: false,
          message: err.error?.message || 'Registration failed'
        };

        this.snackBar.open('Registration failed', 'Close', { duration: 3000, panelClass: ['snackbar-error'] });
      }
    });
  }

  registerAnother() {
    this.registrationForm.reset();
    this.registrationStatus = { success: false, message: 'Not attempted yet' };
  }
}
