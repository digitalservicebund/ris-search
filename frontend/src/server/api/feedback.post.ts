import {
  defineEventHandler,
  readBody,
  sendRedirect,
  getHeader,
  getRequestURL,
} from "h3";
import useBackendUrl from "~/composables/useBackendUrl";

function buildRedirectPath(
  referer: string | undefined,
  requestUrl: URL,
  feedbackStatus: "sent" | "error",
): string {
  let redirectPath = `/?feedback=${feedbackStatus}`;

  if (referer) {
    try {
      const refererUrl = new URL(referer);

      if (refererUrl.origin === requestUrl.origin) {
        const url = new URL(refererUrl);
        url.searchParams.delete("feedback");
        url.searchParams.set("feedback", feedbackStatus);
        redirectPath = `${url.pathname}${url.search}`;
      }
    } catch (error) {
      // Invalid URL, fall back to default redirect path
      console.error("Failed to parse referer URL:", {
        referer,
        error: error instanceof Error ? error.message : String(error),
      });
    }
  }

  return redirectPath;
}

interface FeedbackBody {
  text?: string;
  url?: string;
  user_id?: string;
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
        user_id: body?.user_id || "anonymous_feedback_user",
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
        user_id: body?.user_id,
        name: body?.name,
      },
    });
    return sendRedirect(
      event,
      buildRedirectPath(referer, getRequestURL(event), "error"),
    );
  }
});
