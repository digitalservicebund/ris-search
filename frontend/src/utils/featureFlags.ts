import { useRuntimeConfig } from "#imports";

export function privateFeaturesEnabled(): boolean {
  const config = useRuntimeConfig();
  return config.public.privateFeaturesEnabled;
}
