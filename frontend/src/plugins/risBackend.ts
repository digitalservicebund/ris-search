export default defineNuxtPlugin(() => {
  const basicAuth = useRuntimeConfig().basicAuth;

  const risBackend = $fetch.create({
    onRequest({ options }) {
      options.headers.set("Authorization", `Basic ${basicAuth}`);
    },
  });

  return {
    provide: {
      risBackend,
    },
  };
});
