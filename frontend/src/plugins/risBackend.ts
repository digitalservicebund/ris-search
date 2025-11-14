export default defineNuxtPlugin(() => {
  const config = useRuntimeConfig();
  const basicAuth = config.basicAuth;
  const backendUrl = config.public.risBackendUrl;

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
