import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
	selector: 'app-register',
	standalone: true,
	imports: [CommonModule, ReactiveFormsModule, RouterModule],
	templateUrl: './register.component.html',
	styleUrls: ['./register.component.css']
})
export class RegisterComponent {
	registerForm: FormGroup;
	errorMessage: string = '';
	isLoading: boolean = false;

	constructor(
		private fb: FormBuilder,
		private authService: AuthService,
		private router: Router
	) {
		this.registerForm = this.fb.group({
			name: ['', [Validators.required, Validators.minLength(2)]],
			email: ['', [Validators.required, Validators.email]],
			password: ['', [Validators.required, Validators.minLength(6)]],
			role: ['CLIENT', Validators.required]
		});
	}

	onSubmit(): void {
		if (this.registerForm.invalid) {
			return;
		}

		this.isLoading = true;
		this.errorMessage = '';

		this.authService.register(this.registerForm.value).subscribe({
			next: (response) => {
				console.log('Registration successful', response);
				if (this.authService.isSeller) {
					this.router.navigate(['/seller-dashboard']);
				} else {
					this.router.navigate(['/products']);
				}
			},
			error: (error) => {
				console.error('Registration failed', error);
				this.errorMessage = error.error?.error || 'Registration failed. Please try again.';
				this.isLoading = false;
			}
		});
	}
}