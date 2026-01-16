import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { AuthService } from '../../services/auth.service';
import { Product } from '../../models/product.model';

@Component({
	selector: 'app-seller-dashboard',
	standalone: true,
	imports: [CommonModule, RouterModule],
	templateUrl: './seller-dashboard.component.html',
	styleUrls: ['./seller-dashboard.component.css']
})
export class SellerDashboardComponent implements OnInit {
	products: Product[] = [];
	isLoading: boolean = true;
	errorMessage: string = '';

	constructor(
		private productService: ProductService,
		public authService: AuthService,
		private router: Router
	) { }

	ngOnInit(): void {
		this.loadMyProducts();
	}

	loadMyProducts(): void {
		this.isLoading = true;
		this.productService.getMyProducts().subscribe({
			next: (products) => {
				this.products = products;
				this.isLoading = false;
			},
			error: (error) => {
				console.error('Error loading products', error);
				this.errorMessage = 'Failed to load your products';
				this.isLoading = false;
			}
		});
	}

	createProduct(): void {
		this.router.navigate(['/product-form']);
	}

	editProduct(productId: string): void {
		this.router.navigate(['/product-form', productId]);
	}

	deleteProduct(productId: string): void {
		if (confirm('Are you sure you want to delete this product?')) {
			this.productService.deleteProduct(productId).subscribe({
				next: () => {
					this.loadMyProducts();
				},
				error: (error) => {
					console.error('Error deleting product', error);
					alert('Failed to delete product');
				}
			});
		}
	}

	logout(): void {
		this.authService.logout();
		this.router.navigate(['/login']);
	}

	goToProducts(): void {
		this.router.navigate(['/products']);
	}

	getImageUrl(imageId: string): string {
		return `http://localhost:8083/api/media/${imageId}/file`;
	}
}