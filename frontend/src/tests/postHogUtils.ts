export function getPostHogConfig(
  key: string | undefined,
  host: string | undefined,
) {
  return {
    public: {
      analytics: {
        posthogKey: key,
        posthogHost: host,
      },
    },
  };
}
