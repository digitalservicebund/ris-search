import type { FetchHook } from "ofetch";

function addServerAuthentication(basicAuth: string): FetchHook {
  return ({ options }) => {
    if (import.meta.server && basicAuth) {
      options.headers.set("Authorization", `Basic ${basicAuth}`);
    }
  };
}

/**
 * Allows you to register onRequest handlers for fetch calls while preserving
 * the default application-wide handlers. If you use the onRequest option
 * without this, any application-wide handlers will be replaced.
 *
 * Usage with useRisBackend:
 *
 * ```ts
 * useRisBackend<Example>(baseUrl, {
 *   onRequest: extendOnRequest(() => {
 *     // Custom handler implementation
 *   })
 * });
 * ```
 *
 * This is currently only required for onRequest since our custom $fetch doesn't
 * use any other interceptors.
 *
 * @param cb - Interceptor(s) to add to the default ones
 * @returns Interceptor list to provide to
 */
export function extendOnRequest(...cb: FetchHook[]) {
  const config = useRuntimeConfig();
  return [addServerAuthentication(config.basicAuth), ...cb];
}

export default defineNuxtPlugin(() => {
  const config = useRuntimeConfig();
  const backendUrl = config.public.risBackendUrl;

  const risBackend = $fetch.create({
    baseURL: backendUrl,
    onRequest: extendOnRequest(),
  });

  return { provide: { risBackend } };
});
