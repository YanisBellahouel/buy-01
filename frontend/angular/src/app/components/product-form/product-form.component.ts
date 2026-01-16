import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { MediaService } from '../../services/media.service';

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

	selectedFiles: File[] = [];
	previewUrls: string[] = [];
	uploadedImageIds: string[] = [];

	constructor(
		private fb: FormBuilder,
		private productService: ProductService,
		private mediaService: MediaService,
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
				this.uploadedImageIds = product.imageIds || [];
			},
			error: (error) => {
				console.error('Error loading product', error);
				this.errorMessage = 'Failed to load product';
			}
		});
	}

	onFilesSelected(event: any): void {
		const files = event.target.files;

		if (!files || files.length === 0) return;

		// Validation
		for (let file of files) {
			const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp'];
			if (!allowedTypes.includes(file.type)) {
				this.errorMessage = 'Invalid file type. Only images are allowed (JPEG, PNG, GIF, WebP)';
				return;
			}

			const maxSize = 2 * 1024 * 1024; // 2MB
			if (file.size > maxSize) {
				this.errorMessage = 'File size exceeds 2MB limit';
				return;
			}
		}

		// Ajouter les fichiers sélectionnés
		this.selectedFiles = Array.from(files);
		this.errorMessage = '';

		// Générer les previews
		this.previewUrls = [];
		this.selectedFiles.forEach(file => {
			const reader = new FileReader();
			reader.onload = (e: any) => {
				this.previewUrls.push(e.target.result);
			};
			reader.readAsDataURL(file);
		});
	}

	removePreview(index: number): void {
		this.selectedFiles.splice(index, 1);
		this.previewUrls.splice(index, 1);
	}

	async onSubmit(): Promise<void> {
		if (this.productForm.invalid) {
			return;
		}

		this.isLoading = true;
		this.errorMessage = '';

		try {
			// 1. Upload images d'abord (si sélectionnées)
			const imageIds: string[] = [...this.uploadedImageIds];

			if (this.selectedFiles.length > 0) {
				for (const file of this.selectedFiles) {
					try {
						const uploadResult = await new Promise<any>((resolve, reject) => {
							this.mediaService.uploadMedia(file).subscribe({
								next: (result) => resolve(result),
								error: (err) => reject(err)
							});
						});

						if (uploadResult && uploadResult.id) {
							imageIds.push(uploadResult.id);
						}
					} catch (uploadError) {
						console.error('Error uploading image:', uploadError);
						this.errorMessage = 'Failed to upload one or more images';
						this.isLoading = false;
						return;
					}
				}
			}

			// 2. Créer/Modifier le produit avec les IDs d'images
			const productData = {
				...this.productForm.value,
				imageIds: imageIds
			};

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
		} catch (error) {
			console.error('Error uploading images', error);
			this.errorMessage = 'Failed to upload images';
			this.isLoading = false;
		}
	}

	cancel(): void {
		this.router.navigate(['/seller-dashboard']);
	}
}