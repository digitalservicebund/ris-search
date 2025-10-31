export function usePrivateFeaturesFlag(): boolean {
  const config = useRuntimeConfig();
  return config.public?.privateFeaturesEnabled;
}
