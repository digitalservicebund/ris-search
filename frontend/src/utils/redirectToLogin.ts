import { navigateTo } from "nuxt/app";

export function redirectToLogin(fullPath: string) {
  return navigateTo("/auth?redirectTo=" + fullPath, {
    external: true,
    replace: true,
  });
}
