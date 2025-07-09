import { defineNuxtRouteMiddleware } from "nuxt/app";
import { redirectToLogin } from "~/utils/redirectToLogin";

export default defineNuxtRouteMiddleware((to) => {
  const config = useRuntimeConfig();
  if (process.env.TEST || !config.public.authEnabled) return; // disable login checks while unit testing, or if disabled
  const { loggedIn } = useUserSession();
  if (to.fullPath.startsWith("/auth")) {
    return;
  }
  if (!loggedIn.value) {
    return redirectToLogin(to.fullPath);
  }
});
