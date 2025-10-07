import { HttpStatusCode } from "axios";
import { defineEventHandler, getRequestURL, createError } from "h3";
import { FetchError } from "ofetch";
import { requireAccessTokenWithRefresh } from "../auth";
import { useRuntimeConfig } from "#imports";

export default defineEventHandler(async (event): Promise<unknown> => {
  const runtimeConfig = useRuntimeConfig(event);

  if (!runtimeConfig.public.authEnabled) {
    throw createError({
      statusCode: HttpStatusCode.NotFound,
    });
  }

  let token: string;
  try {
    token = await requireAccessTokenWithRefresh(event);
  } catch (error) {
    if (error instanceof FetchError && error.data?.error === "invalid_grant") {
      throw createError({
        statusCode: HttpStatusCode.Unauthorized,
        statusMessage: "invalid_grant",
      });
    }
    throw error;
  }
  try {
    const requestedUrl = getRequestURL(event);
    const newUrl =
      runtimeConfig.risBackendUrl + requestedUrl.pathname + requestedUrl.search;

    const headers: Record<string, string> = {
      Accept: event.headers.get("Accept") ?? "application/json",
      Authorization: `Bearer ${token}`,
      "get-resources-via": "PROXY",
    };
    return await $fetch.raw(newUrl, {
      headers,
      responseType: "stream",
    });
  } catch (error) {
    /* if the upstream server is not available, translate the error into
       BadGateway */
    if (
      (error as { cause?: { cause: { code: string } } })?.cause?.cause?.code ===
      "ECONNREFUSED"
    )
      throw createError({
        statusCode: HttpStatusCode.InternalServerError,
        statusMessage: "Internal Server Error",
      });

    throw error;
  }
});
