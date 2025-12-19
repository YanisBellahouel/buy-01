import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
	selector: 'app-login',
	standalone: true,
	imports: [CommonModule, ReactiveFormsModule, RouterModule],
	templateUrl: './login.component.html',
	styleUrls: ['./login.component.css']
})
export class LoginComponent {
	loginForm: FormGroup;
	errorMessage: string = '';
	isLoading: boolean = false;

	constructor(
		private fb: FormBuilder,
		private authService: AuthService,
		private router: Router
	) {
		this.loginForm = this.fb.group({
			email: ['', [Validators.required, Validators.email]],
			password: ['', [Validators.required, Validators.minLength(6)]]
		});
	}

	onSubmit(): void {
		if (this.loginForm.invalid) {
			return;
		}

		this.isLoading = true;
		this.errorMessage = '';

		this.authService.login(this.loginForm.value).subscribe({
			next: (response) => {
				console.log('Login successful', response);
				if (this.authService.isSeller) {
					this.router.navigate(['/seller-dashboard']);
				} else {
					this.router.navigate(['/products']);
				}
			},
			error: (error) => {
				console.error('Login failed', error);
				this.errorMessage = error.error?.error || 'Login failed. Please try again.';
				this.isLoading = false;
			}
		});
	}
}