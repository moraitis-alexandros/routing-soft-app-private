export interface LoginResponse {
  status: boolean;  // Optional if backend always sends it
  data?: string;    // Optional if backend might send it
  token?: string;   // JWT token returned from backend
}
