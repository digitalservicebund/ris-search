export const useBackendURL = () => {
  const config = useRuntimeConfig();
  return config.public.backendURL;
};
