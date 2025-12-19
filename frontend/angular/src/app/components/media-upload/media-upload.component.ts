import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MediaService } from '../../services/media.service';
import { Media } from '../../models/media.model';

@Component({
	selector: 'app-media-upload',
	standalone: true,
	imports: [CommonModule, RouterModule],
	templateUrl: './media-upload.component.html',
	styleUrls: ['./media-upload.component.css']
})
export class MediaUploadComponent {
	selectedFile: File | null = null;
	previewUrl: string | null = null;
	isUploading: boolean = false;
	errorMessage: string = '';
	successMessage: string = '';
	myMedia: Media[] = [];
	isLoadingMedia: boolean = true;

	constructor(
		private mediaService: MediaService,
		private router: Router
	) {
		this.loadMyMedia();
	}

	loadMyMedia(): void {
		this.mediaService.getMyMedia().subscribe({
			next: (media) => {
				this.myMedia = media;
				this.isLoadingMedia = false;
			},
			error: (error) => {
				console.error('Error loading media', error);
				this.isLoadingMedia = false;
			}
		});
	}

	onFileSelected(event: any): void {
		const file = event.target.files[0];

		if (!file) return;

		// Validation
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

		this.selectedFile = file;
		this.errorMessage = '';
		this.successMessage = '';

		// Preview
		const reader = new FileReader();
		reader.onload = (e: any) => {
			this.previewUrl = e.target.result;
		};
		reader.readAsDataURL(file);
	}

	uploadFile(): void {
		if (!this.selectedFile) {
			this.errorMessage = 'Please select a file first';
			return;
		}

		this.isUploading = true;
		this.errorMessage = '';
		this.successMessage = '';

		this.mediaService.uploadMedia(this.selectedFile).subscribe({
			next: (response) => {
				this.successMessage = 'File uploaded successfully!';
				this.isUploading = false;
				this.selectedFile = null;
				this.previewUrl = null;
				this.loadMyMedia();
			},
			error: (error) => {
				console.error('Upload failed', error);
				this.errorMessage = error.error?.error || 'Upload failed';
				this.isUploading = false;
			}
		});
	}

	deleteMedia(mediaId: string): void {
		if (confirm('Are you sure you want to delete this media?')) {
			this.mediaService.deleteMedia(mediaId).subscribe({
				next: () => {
					this.loadMyMedia();
				},
				error: (error) => {
					console.error('Error deleting media', error);
					alert('Failed to delete media');
				}
			});
		}
	}

	getImageUrl(imagePath: string): string {
		return this.mediaService.getImageUrl(imagePath);
	}

	goBack(): void {
		this.router.navigate(['/seller-dashboard']);
	}
}