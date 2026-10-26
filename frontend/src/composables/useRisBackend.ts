export const useRisBackend = createUseFetch((callerOptions) => ({
  $fetch: useNuxtApp().$risBackend as typeof $fetch,
  ...callerOptions,
}));
