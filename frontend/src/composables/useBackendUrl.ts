/**
 * A custom composable that receives a url and return it prepended with
 * the backend base URL. The function returns the backend url when no input
 *
 * @param url An optional path that needs to be prepended (e.g., '/v1/legislation' => 'http://localhost:8090/v1/legislation').
 * @returns A string with the full path.
 */
function useBackendUrl(url?: string) {
  const config = useRuntimeConfig();
  let backendUrl = config.public.risBackendUrl;
  if (import.meta.server && !isStringEmpty(config.public.risBackendUrlSsr)) {
    backendUrl = config.public.risBackendUrlSsr;
  }

  return url ? `${backendUrl}${url}` : backendUrl;
}

export default useBackendUrl;
