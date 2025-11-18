import type { UseFetchOptions } from "nuxt/app";

export function useRisBackend<T>(
  url: ComputedRef<string> | string | (() => string),
  options?: UseFetchOptions<T>,
) {
  return useFetch(url, {
    ...options,
    $fetch: useNuxtApp().$risBackend as typeof $fetch,
  });
}
