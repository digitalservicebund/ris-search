/**
 * A custom composable that receives a url and return it prepended with
 * the backend base URL
 *
 * @param url An optional path that needs to be prepended (e.g., '/v1/legislation' => 'http://localhost:8090/v1/legislation').
 * @returns A string with the full path.
 */
function useBackendUrl(url?: string) {
  const config = useRuntimeConfig();
  const backendUrl = config.public.risBackendUrl;
  return url ? `${backendUrl}${url}` : backendUrl;
}

export default useBackendUrl;
