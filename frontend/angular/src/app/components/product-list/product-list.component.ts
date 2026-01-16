import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { AuthService } from '../../services/auth.service';
import { Product } from '../../models/product.model';

@Component({
	selector: 'app-product-list',
	standalone: true,
	imports: [CommonModule, RouterModule],
	templateUrl: './product-list.component.html',
	styleUrls: ['./product-list.component.css']
})
export class ProductListComponent implements OnInit {
	products: Product[] = [];
	isLoading: boolean = true;
	errorMessage: string = '';

	constructor(
		public productService: ProductService,
		public authService: AuthService,
		private router: Router
	) { }

	ngOnInit(): void {
		this.loadProducts();
	}

	loadProducts(): void {
		this.isLoading = true;
		this.productService.getAllProducts().subscribe({
			next: (products) => {
				this.products = products;
				this.isLoading = false;
			},
			error: (error) => {
				console.error('Error loading products', error);
				this.errorMessage = 'Failed to load products';
				this.isLoading = false;
			}
		});
	}

	logout(): void {
		this.authService.logout();
		this.router.navigate(['/login']);
	}

	goToSellerDashboard(): void {
		this.router.navigate(['/seller-dashboard']);
	}

	getImageUrl(imageId: string): string {
		return `http://localhost:8083/api/media/${imageId}/file`;
	}
}