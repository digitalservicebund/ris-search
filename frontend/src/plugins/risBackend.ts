import { isStringEmpty } from "~/utils/textFormatting";

export default defineNuxtPlugin(() => {
  const config = useRuntimeConfig();
  const basicAuth = config.basicAuth;
  let backendUrl = config.public.risBackendUrl;
  if (import.meta.server && !isStringEmpty(config.public.risBackendUrlSsr)) {
    backendUrl = config.public.risBackendUrlSsr;
  }

  const risBackend = $fetch.create({
    baseURL: backendUrl,
    onRequest({ options }) {
      if (import.meta.server && basicAuth) {
        options.headers.set("Authorization", `Basic ${basicAuth}`);
      }
    },
  });

  return {
    provide: {
      risBackend,
    },
  };
});
