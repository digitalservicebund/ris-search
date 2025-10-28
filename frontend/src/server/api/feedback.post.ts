import {
  defineEventHandler,
  readBody,
  sendRedirect,
  getHeader,
  getRequestURL,
} from "h3";

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
        redirectPath = url.pathname + url.search;
      }
    } catch {
      // Invalid URL, fall back
    }
  }

  return redirectPath;
}

export default defineEventHandler(async (event) => {
  const body = await readBody(event);
  const config = useRuntimeConfig();

  const referer = getHeader(event, "referer");
  let currentUrl = "/";
  if (referer) {
    try {
      const refererUrl = new URL(referer);
      currentUrl = refererUrl.pathname + refererUrl.search;
    } catch {
      // Invalid URL, fall back
    }
  }

  try {
    const params = new URLSearchParams({
      text: body.text,
      url: body.url || currentUrl,
      user_id: body.user_id || "anonymous_feedback_user",
    });

    await $fetch(`${config.risBackendUrl}/v1/feedback?${params.toString()}`);

    return sendRedirect(
      event,
      buildRedirectPath(referer, getRequestURL(event), "sent"),
    );
  } catch {
    return sendRedirect(
      event,
      buildRedirectPath(referer, getRequestURL(event), "error"),
    );
  }
});
