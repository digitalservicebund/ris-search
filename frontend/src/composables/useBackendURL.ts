export const useBackendURL = () => {
  return process.env.NUXT_RIS_BACKEND_URL ?? "http://localhost:8090";
};
