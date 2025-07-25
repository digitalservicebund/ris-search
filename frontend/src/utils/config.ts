import { useRuntimeConfig } from "#imports";

export function isPublicProfile(): boolean {
  return checkProfile("public");
}

export function isInternalProfile(): boolean {
  return checkProfile("internal");
}

export function isPrototypeProfile(): boolean {
  return checkProfile("prototype");
}

function checkProfile(profile: string): boolean {
  const config = useRuntimeConfig();
  return config.public.profile === profile;
}
