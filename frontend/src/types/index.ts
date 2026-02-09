export interface ScanRequest {
  url: string;
}

export interface ScanIssue {
  code: string;
  title: string;
  description: string;
  severity: number;
  passed: boolean;
}

export interface ScanResponse {
  id: number;
  url: string;
  score: number;
  issues: ScanIssue[];
  recommendations: string[];
  jurisdiction: string;
  riskLevel: 'LOW' | 'MEDIUM' | 'HIGH';
}

export interface DocumentType {
  value: string;
  label: string;
}

export interface GenerateDocRequest {
  documentType: string;
  businessName: string;
  businessType?: string;
  websiteUrl?: string;
  jurisdiction?: string;
  language?: string;
  scanId?: number;
  additionalInfo?: string;
}

export interface GeneratedDocument {
  id: number;
  documentType: string;
  title: string;
  content: string;
  businessName: string;
  jurisdiction: string;
  createdAt: string;
}

export interface UserProfile {
  id: number;
  email: string;
  name: string;
  plan: string;
  credits: number;
  createdAt: string;
}

export interface TokenResponse {
  token: string;
  type: string;
  expiresIn: number;
}
