import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { TextFieldComponent } from '../../components/text-field/text-field.component';
import { ButtonComponent } from '../../components/button/button.component';
import { FormFieldComponent } from "../../components/form-field/form-field.component";
import { LabelComponent } from "../../components/label/label.component";
import { FormErrorComponent } from "../../components/form-error/form-error.component";
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

function passwordsMatchValidator(control: AbstractControl): ValidationErrors | null {
  const pw = control.get('password')?.value;
  const pw2 = control.get('repeatPassword')?.value;
  if (pw && pw2 && pw !== pw2) {
    return { passwordMismatch: true };
  }
  return null;
}

@Component({
  selector: 'app-signup-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, TextFieldComponent, ButtonComponent, FormFieldComponent, LabelComponent, FormErrorComponent],
  templateUrl: './signup-page.component.html',
  styleUrls: ['./signup-page.component.css']
})
export class SignupPageComponent {
  form = this.fb.group({
    name: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    repeatPassword: ['', [Validators.required]]
  }, { validators: passwordsMatchValidator });

  loading = false;
  error: string | null = null;

  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router) {}

  onSubmit(): void {
    this.error = null;
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const { name, email, password } = this.form.value;
    this.loading = true;
    this.auth.signup(email as string, password as string, name as string).subscribe({
      next: () => {
        this.loading = false;
        // After signup, navigate to login page
        this.router.navigate(['/login']);
      },
      error: (err) => {
        this.loading = false;
        this.error = err?.error?.message || 'Signup failed';
      }
    });
  }
}
