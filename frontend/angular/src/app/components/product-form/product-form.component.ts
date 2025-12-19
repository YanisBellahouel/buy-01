import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ProductService } from '../../services/product.service';

@Component({
	selector: 'app-product-form',
	standalone: true,
	imports: [CommonModule, ReactiveFormsModule, RouterModule],
	templateUrl: './product-form.component.html',
	styleUrls: ['./product-form.component.css']
})
export class ProductFormComponent implements OnInit {
	productForm: FormGroup;
	isEditMode: boolean = false;
	productId: string | null = null;
	isLoading: boolean = false;
	errorMessage: string = '';

	constructor(
		private fb: FormBuilder,
		private productService: ProductService,
		private router: Router,
		private route: ActivatedRoute
	) {
		this.productForm = this.fb.group({
			name: ['', [Validators.required, Validators.minLength(3)]],
			description: ['', [Validators.required, Validators.minLength(10)]],
			price: [0, [Validators.required, Validators.min(0.01)]],
			quantity: [0, [Validators.required, Validators.min(0)]]
		});
	}

	ngOnInit(): void {
		this.productId = this.route.snapshot.paramMap.get('id');
		if (this.productId) {
			this.isEditMode = true;
			this.loadProduct(this.productId);
		}
	}

	loadProduct(id: string): void {
		this.productService.getProductById(id).subscribe({
			next: (product) => {
				this.productForm.patchValue({
					name: product.name,
					description: product.description,
					price: product.price,
					quantity: product.quantity
				});
			},
			error: (error) => {
				console.error('Error loading product', error);
				this.errorMessage = 'Failed to load product';
			}
		});
	}

	onSubmit(): void {
		if (this.productForm.invalid) {
			return;
		}

		this.isLoading = true;
		this.errorMessage = '';

		const productData = this.productForm.value;

		if (this.isEditMode && this.productId) {
			this.productService.updateProduct(this.productId, productData).subscribe({
				next: () => {
					this.router.navigate(['/seller-dashboard']);
				},
				error: (error) => {
					console.error('Error updating product', error);
					this.errorMessage = error.error?.error || 'Failed to update product';
					this.isLoading = false;
				}
			});
		} else {
			this.productService.createProduct(productData).subscribe({
				next: () => {
					this.router.navigate(['/seller-dashboard']);
				},
				error: (error) => {
					console.error('Error creating product', error);
					this.errorMessage = error.error?.error || 'Failed to create product';
					this.isLoading = false;
				}
			});
		}
	}

	cancel(): void {
		this.router.navigate(['/seller-dashboard']);
	}
}