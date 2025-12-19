import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environement';
import { Media, MediaUploadResponse } from '../models/media.model';
import { AuthService } from './auth.service';

@Injectable({
	providedIn: 'root'
})
export class MediaService {
	private apiUrl = `${environment.apiUrl}/media`;

	constructor(
		private http: HttpClient,
		private authService: AuthService
	) { }

	private getHeaders(): HttpHeaders {
		const token = this.authService.token;
		return new HttpHeaders({
			'Authorization': `Bearer ${token}`
		});
	}

	uploadMedia(file: File, productId?: string): Observable<MediaUploadResponse> {
		const formData = new FormData();
		formData.append('file', file);
		if (productId) {
			formData.append('productId', productId);
		}

		return this.http.post<MediaUploadResponse>(this.apiUrl, formData, {
			headers: this.getHeaders()
		});
	}

	getMediaById(id: string): Observable<Media> {
		return this.http.get<Media>(`${this.apiUrl}/${id}`);
	}

	getMediaByProductId(productId: string): Observable<Media[]> {
		return this.http.get<Media[]>(`${this.apiUrl}/product/${productId}`);
	}

	getMyMedia(): Observable<Media[]> {
		return this.http.get<Media[]>(`${this.apiUrl}/my-media`, {
			headers: this.getHeaders()
		});
	}

	deleteMedia(id: string): Observable<any> {
		return this.http.delete(`${this.apiUrl}/${id}`, {
			headers: this.getHeaders()
		});
	}

	getImageUrl(imagePath: string): string {
		return `http://localhost:8083${imagePath}`;
	}
}