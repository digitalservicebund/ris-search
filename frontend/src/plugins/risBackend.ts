export default defineNuxtPlugin(() => {
  const config = useRuntimeConfig();
  const backendUrl = config.public.risBackendUrl;

  const risBackend = $fetch.create({
    baseURL: backendUrl,
    onRequest({ options }) {
      if (import.meta.server) {
        const basicAuth = config.basicAuth;
        if (basicAuth) {
          options.headers.set("Authorization", `Basic ${basicAuth}`);
        }
      }
    },
  });

  return {
    provide: {
      risBackend,
    },
  };
});
