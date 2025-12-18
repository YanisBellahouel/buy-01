export interface Product {
	id: string;
	name: string;
	description: string;
	price: number;
	quantity: number;
	userId: string;
	imageIds: string[];
	createdAt: Date;
	updatedAt: Date;
}

export interface CreateProductRequest {
	name: string;
	description: string;
	price: number;
	quantity: number;
	imageIds?: string[];
}

export interface UpdateProductRequest {
	name?: string;
	description?: string;
	price?: number;
	quantity?: number;
	imageIds?: string[];
}