import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { ProductListComponent } from './components/product-list/product-list.component';
import { SellerDashboardComponent } from './components/seller-dashboard/seller-dashboard.component';
import { ProductFormComponent } from './components/product-form/product-form.component';
import { MediaUploadComponent } from './components/media-upload/media-upload.component';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
	{ path: '', redirectTo: '/products', pathMatch: 'full' },
	{ path: 'login', component: LoginComponent },
	{ path: 'register', component: RegisterComponent },
	{ path: 'products', component: ProductListComponent },
	{
		path: 'seller-dashboard',
		component: SellerDashboardComponent,
		canActivate: [authGuard],
		data: { role: 'SELLER' }
	},
	{
		path: 'product-form',
		component: ProductFormComponent,
		canActivate: [authGuard],
		data: { role: 'SELLER' }
	},
	{
		path: 'product-form/:id',
		component: ProductFormComponent,
		canActivate: [authGuard],
		data: { role: 'SELLER' }
	},
	{
		path: 'media-upload',
		component: MediaUploadComponent,
		canActivate: [authGuard],
		data: { role: 'SELLER' }
	},
	{ path: '**', redirectTo: '/products' }
];