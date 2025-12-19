import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../environments/environement';
import { AuthResponse, LoginRequest, RegisterRequest, User } from '../models/user.model';

@Injectable({
	providedIn: 'root'
})
export class AuthService {
	private apiUrl = environment.apiUrl;
	private currentUserSubject: BehaviorSubject<User | null>;
	public currentUser: Observable<User | null>;

	constructor(private http: HttpClient) {
		const storedUser = localStorage.getItem('currentUser');
		this.currentUserSubject = new BehaviorSubject<User | null>(
			storedUser ? JSON.parse(storedUser) : null
		);
		this.currentUser = this.currentUserSubject.asObservable();
	}

	public get currentUserValue(): User | null {
		return this.currentUserSubject.value;
	}

	public get token(): string | null {
		return localStorage.getItem('token');
	}

	public get isAuthenticated(): boolean {
		return !!this.token;
	}

	public get isSeller(): boolean {
		return this.currentUserValue?.role === 'SELLER';
	}

	register(request: RegisterRequest): Observable<AuthResponse> {
		return this.http.post<AuthResponse>(`${this.apiUrl}/auth/register`, request)
			.pipe(
				tap(response => {
					this.setSession(response);
				})
			);
	}

	login(request: LoginRequest): Observable<AuthResponse> {
		return this.http.post<AuthResponse>(`${this.apiUrl}/auth/login`, request)
			.pipe(
				tap(response => {
					this.setSession(response);
				})
			);
	}

	logout(): void {
		localStorage.removeItem('token');
		localStorage.removeItem('currentUser');
		this.currentUserSubject.next(null);
	}

	private setSession(response: AuthResponse): void {
		localStorage.setItem('token', response.token);
		localStorage.setItem('currentUser', JSON.stringify(response.user));
		this.currentUserSubject.next(response.user);
	}
}