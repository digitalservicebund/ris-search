export const useBackendURL = () => {
  const config = useRuntimeConfig();
  if (import.meta.server) {
    return config.ssrBackendUrl;
  }
  return config.public.backendURL;
};
