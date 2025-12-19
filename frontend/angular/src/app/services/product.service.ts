import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environement';
import { Product, CreateProductRequest, UpdateProductRequest } from '../models/product.model';
import { AuthService } from './auth.service';

@Injectable({
	providedIn: 'root'
})
export class ProductService {
	private apiUrl = `${environment.apiUrl}/products`;

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

	getAllProducts(): Observable<Product[]> {
		return this.http.get<Product[]>(this.apiUrl);
	}

	getProductById(id: string): Observable<Product> {
		return this.http.get<Product>(`${this.apiUrl}/${id}`);
	}

	getMyProducts(): Observable<Product[]> {
		return this.http.get<Product[]>(`${this.apiUrl}/my-products`, {
			headers: this.getHeaders()
		});
	}

	createProduct(request: CreateProductRequest): Observable<Product> {
		return this.http.post<Product>(this.apiUrl, request, {
			headers: this.getHeaders()
		});
	}

	updateProduct(id: string, request: UpdateProductRequest): Observable<Product> {
		return this.http.put<Product>(`${this.apiUrl}/${id}`, request, {
			headers: this.getHeaders()
		});
	}

	deleteProduct(id: string): Observable<any> {
		return this.http.delete(`${this.apiUrl}/${id}`, {
			headers: this.getHeaders()
		});
	}
}