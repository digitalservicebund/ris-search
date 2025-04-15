// noinspection JSUnusedGlobalSymbols

declare module "#auth-utils" {
  interface User {
    name: string;
    email?: string;
  }

  interface UserSession {
    secure?: SecureSessionData;
    expiresAt: number;
  }

  interface SecureSessionData {
    tokens: {
      accessToken: string;
      refreshToken: string;
    };
  }
}

export { SecureSessionData, UserSession };
