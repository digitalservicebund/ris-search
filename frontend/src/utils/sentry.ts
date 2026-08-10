/**
 * Whether an error captured by Sentry is a 3xx/4xx error page error, e.g. a 404
 * created via `createError` or `showError`, which we don't want to report.
 *
 * Checking `isNuxtError` is not sufficient here, because that is only true for
 * errors thrown on the client. An error thrown on the server is serialized to
 * JSON, losing its Error type and internal markers that `isNuxtError` uses for
 * detecting its own errors. Instead, we'll check the shape, but cancel if:
 *
 * - The error is not an object at all, and therefore guaranteed not to have a
 *   status property
 * - The object is an Error but not a Nuxt error - that way we know it has been
 *   thrown on the client, but has originated somewhere else. Deliberately left
 *   alone, even if they carry a status, so that any unhandled errors are still
 *   reported.
 */
export function shouldSuppressSentryEvent(error: unknown): boolean {
  if (!error || typeof error !== "object") return false;
  if (error instanceof Error && !isNuxtError(error)) return false;

  const { status, statusCode } = error as {
    status?: unknown;
    statusCode?: unknown;
  };

  const parsedStatus = Number(status ?? statusCode);

  return (
    Number.isFinite(parsedStatus) && parsedStatus >= 300 && parsedStatus < 500
  );
}
