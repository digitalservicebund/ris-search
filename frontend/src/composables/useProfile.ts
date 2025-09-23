import { useRuntimeConfig } from "#imports";

export function useProfile() {
  const config = useRuntimeConfig();

  function isPublicProfile(): boolean {
    return config.public.profile === "public";
  }

  function isInternalProfile(): boolean {
    return config.public.profile === "internal";
  }

  function isPrototypeProfile(): boolean {
    return config.public.profile === "prototype";
  }

  return { isPublicProfile, isInternalProfile, isPrototypeProfile };
}
