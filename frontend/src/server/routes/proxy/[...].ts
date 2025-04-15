import { HttpStatusCode } from "axios";
import { requireAccessToken } from "@/server/auth";
import {
  createError,
  defineEventHandler,
  type EventHandlerRequest,
  getRequestURL,
  type H3Event,
} from "h3";

import { useRuntimeConfig } from "#imports";

const runtimeConfig = useRuntimeConfig();

export default defineEventHandler(
  async (event: H3Event<EventHandlerRequest>): Promise<unknown> => {
    if (!runtimeConfig.public.authEnabled) {
      throw createError({
        statusCode: HttpStatusCode.NotFound,
      });
    }
    const accessToken = await requireAccessToken(event);
    const requestedUrl = getRequestURL(event);

    if (!requestedUrl.pathname.startsWith("/proxy/v1/")) {
      throw createError({
        statusCode: HttpStatusCode.Forbidden,
      });
    }
    const newUrl =
      runtimeConfig.risBackendUrl +
      requestedUrl.pathname.replace("/proxy", "") +
      requestedUrl.search;

    try {
      return await $fetch<unknown>(newUrl, {
        headers: {
          Accept: event.headers.get("Accept") || "application/json",
          Authorization: `Bearer ${accessToken}`,
        },
        responseType: "stream",
      });
    } catch (error) {
      if (
        (error as { cause?: { cause: { code: string } } })?.cause?.cause
          ?.code === "ECONNREFUSED"
      )
        throw {
          message: "Bad Gateway",
          statusCode: HttpStatusCode.BadGateway,
        };
      throw error;
    }
  },
);
