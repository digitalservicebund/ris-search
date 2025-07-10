import { HttpStatusCode } from "axios";
import type { NitroRuntimeConfig } from "nitropack";

async function doFinalSignout(
  config: Pick<
    NitroRuntimeConfig,
    "serverUrl" | "realm" | "clientId" | "clientSecret"
  >,
  refreshToken: string,
) {
  try {
    const logoutEndpoint = `${config.serverUrl}/realms/${config.realm}/protocol/openid-connect/logout`;

    await $fetch(logoutEndpoint, {
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: new URLSearchParams({
        client_id: config.clientId,
        client_secret: config.clientSecret,
        refresh_token: refreshToken,
      }),
      method: "POST",
    });
  } catch (e) {
    console.error("Unable to perform post-logout handshake", e);
  }
}

export default defineEventHandler(async (event) => {
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

  await doFinalSignout(config, refreshToken);
  await clearUserSession(event);

  await event.respondWith(
    new Response(null, { status: HttpStatusCode.NoContent }),
  );
});
