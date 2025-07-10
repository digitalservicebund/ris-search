import { HttpStatusCode } from "axios";
import { createError, type EventHandlerRequest, type H3Event } from "h3";
import type { NitroRuntimeConfig } from "nitropack/types";
import { buildSessionAttributes } from "./routes/auth/keycloak/keycloakUtils";
import type { KeycloakTokenResponse } from "./routes/auth/keycloak/keycloakUtils";
import { useRuntimeConfig } from "#imports";

async function getRefreshedTokens(
  config: Pick<
    NitroRuntimeConfig,
    "serverUrl" | "realm" | "clientId" | "clientSecret"
  >,
  refreshToken: string,
): Promise<KeycloakTokenResponse> {
  const tokenEndpoint = `${config.serverUrl}/realms/${config.realm}/protocol/openid-connect/token`;
  return await $fetch<KeycloakTokenResponse>(tokenEndpoint, {
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
    },
    body: new URLSearchParams({
      client_id: config.clientId,
      client_secret: config.clientSecret,
      grant_type: "refresh_token",
      refresh_token: refreshToken,
    }),
    method: "POST",
  });
}

export async function performRefresh(event: H3Event<EventHandlerRequest>) {
  const config = useRuntimeConfig(event).oauth?.keycloak;

  if (!config) throw new Error("No config provided");

  const session = await getUserSession(event);
  const refreshToken = session.secure?.tokens.refreshToken;

  if (!refreshToken) {
    throw createError({
      statusCode: HttpStatusCode.Unauthorized,
      statusMessage: "Unauthorized",
      cause: "no refresh token",
    });
  }
  const response = await getRefreshedTokens(config, refreshToken);

  const newSession = {
    ...session,
    ...buildSessionAttributes(response),
  };

  await setUserSession(event, newSession);
  return newSession;
}

export const requireAccessTokenWithRefresh = async (
  event: H3Event<EventHandlerRequest>,
) => {
  const session = await getUserSession(event);
  if (!session.secure?.tokens.accessToken) {
    throw createError({
      statusCode: HttpStatusCode.Unauthorized,
      statusMessage: "Unauthorized",
      cause: "no access token",
    });
  }

  const expiredOrExpiresSoon = session.expiresAt < Date.now() + 5000;
  if (expiredOrExpiresSoon) {
    const refreshed = await performRefresh(event);
    return refreshed.secure?.tokens.accessToken;
  }

  return session.secure?.tokens.accessToken;
};

export const requireAccessToken = async (
  event: H3Event<EventHandlerRequest>,
) => {
  const session = await getUserSession(event);
  if (!session.secure?.tokens.accessToken) {
    throw createError({
      statusCode: HttpStatusCode.Unauthorized,
      statusMessage: "Unauthorized",
      cause: "no access token",
    });
  }

  return session.secure.tokens.accessToken;
};

export const authRedirectCookieName = "auth_redirect";
export const refreshUrl = "/auth/keycloak/refresh";
