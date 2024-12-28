// adminService.ts
export interface Admin {
  id: number;
  name: string;
  email: string;
  joinedDate: string;
  image_base64: string | null;
}

export interface LoginCredentials {
  email: string;
  password: string;
}

export class AdminService {
  private static BASE_URL = 'http://localhost:8090/api/admins';

  static async login(credentials: LoginCredentials): Promise<Admin> {
    try {
      const response = await fetch(`${this.BASE_URL}/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(credentials),
      });

      if (!response.ok) {
        if (response.status === 401) {
          throw new Error('Invalid credentials');
        }
        throw new Error('Login failed');
      }

      return await response.json();
    } catch (error) {
      throw error;
    }
  }
}
