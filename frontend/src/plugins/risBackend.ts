export default defineNuxtPlugin(() => {
  const config = useRuntimeConfig();
  const basicAuth = config.basicAuth;

  const risBackend = $fetch.create({
    onRequest({ options }) {
      if (import.meta.server && basicAuth) {
        options.headers.set("Authorization", basicAuth);
      }
    },
  });

  return {
    provide: {
      risBackend,
    },
  };
});
