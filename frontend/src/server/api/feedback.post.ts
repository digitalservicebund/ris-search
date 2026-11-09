import {
  defineEventHandler,
  readBody,
  sendRedirect,
  getHeader,
  getRequestURL,
} from "h3";
import useBackendUrl from "~/composables/useBackendUrl";
import { getSameOriginUrl } from "~/server/utils/sameOriginUrl";

function buildRedirectPath(
  referer: string | undefined,
  requestUrl: URL,
  feedbackStatus: "sent" | "error",
): string {
  const url = getSameOriginUrl(referer, requestUrl.origin);
  if (!url) return `/?feedback=${feedbackStatus}`;

  url.searchParams.delete("feedback");
  url.searchParams.set("feedback", feedbackStatus);
  return `${url.pathname}${url.search}`;
}

interface FeedbackBody {
  text?: string;
  url?: string;
  name?: string;
}

export default defineEventHandler(async (event) => {
  const body = await readBody<FeedbackBody>(event);

  const referer = getHeader(event, "referer");
  let currentUrl = "/";
  if (referer) {
    try {
      const refererUrl = new URL(referer);
      currentUrl = `${refererUrl.pathname}${refererUrl.search}`;
    } catch (error) {
      // Invalid URL, fall back to default
      console.error("Failed to parse referer URL for currentUrl:", {
        referer,
        error: error instanceof Error ? error.message : String(error),
      });
    }
  }

  try {
    await $fetch(useBackendUrl(`/v1/feedback`), {
      method: "POST",
      body: {
        text: body?.text,
        url: body?.url || currentUrl,
        name: body?.name || "",
      },
    });

    return sendRedirect(
      event,
      buildRedirectPath(referer, getRequestURL(event), "sent"),
    );
  } catch (error) {
    // Log feedback submission errors for debugging
    // This catches network errors, HTTP errors etc
    // We redirect the user with error status so they see feedback was not sent
    console.error("Failed to submit feedback:", error, {
      body: {
        text: body?.text,
        url: body?.url,
        name: body?.name,
      },
    });
    return sendRedirect(
      event,
      buildRedirectPath(referer, getRequestURL(event), "error"),
    );
  }
});
