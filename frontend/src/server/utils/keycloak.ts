import type { UserSession } from "~/auth";

export interface KeycloakTokenResponse {
  access_token: string;
  refresh_token: string;
  expires_in: number;
  refresh_expires_in: number;
}

export const buildSessionAttributes = (
  response: KeycloakTokenResponse,
): UserSession => {
  return {
    expiresAt: Date.now() + response.expires_in * 1000,
    // Private data accessible only on server / routes
    secure: {
      tokens: {
        accessToken: response.access_token,
        refreshToken: response.refresh_token,
      },
    },
  };
};
