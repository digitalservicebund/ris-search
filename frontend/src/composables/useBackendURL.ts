import { useRuntimeConfig } from "#imports";
import { isServer } from "~/utils/importMeta";

export const useBackendURL = () => {
  const config = useRuntimeConfig();
  if (isServer) {
    return config.ssrBackendUrl;
  }
  return config.public.backendURL;
};
