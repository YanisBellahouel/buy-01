export interface Media {
	id: string;
	fileName: string;
	contentType: string;
	fileSize: number;
	imagePath: string;
	productId: string;
	userId: string;
	createdAt: Date;
}

export interface MediaUploadResponse {
	id: string;
	fileName: string;
	contentType: string;
	fileSize: number;
	imagePath: string;
	productId?: string;
	createdAt: Date;
}