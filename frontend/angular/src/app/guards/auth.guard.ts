import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
	const authService = inject(AuthService);
	const router = inject(Router);

	if (authService.isAuthenticated) {
		// Vérifier si la route nécessite le rôle SELLER
		if (route.data['role'] === 'SELLER' && !authService.isSeller) {
			router.navigate(['/products']);
			return false;
		}
		return true;
	}

	// Rediriger vers login
	router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
	return false;
};