import { getRequestURL, getHeader, setHeader, defineEventHandler } from "h3";
import useBackendUrl from "~/composables/useBackendUrl";
import { usePrivateFeaturesFlag } from "~/composables/usePrivateFeaturesFlag";

export default defineEventHandler(async (event) => {
  const config = useRuntimeConfig();
  const userAgent = (getHeader(event, "User-Agent") ?? "").toUpperCase();
  const privateFeaturesEnabled = usePrivateFeaturesFlag();
  const file = privateFeaturesEnabled
    ? "robots.staging.txt"
    : "robots.public.txt";

  setHeader(event, "Content-Type", "text/plain; charset=utf-8");
  setHeader(event, "Vary", "User-Agent");
  const { origin } = getRequestURL(event);

  if (userAgent === "DG_JUSTICE_CRAWLER") {
    return await $fetch<string>(useBackendUrl(`/v1/eclicrawler/robots.txt`), {
      method: "GET",
    });
  }

  const url = `${origin}/${file}`;

  return await $fetch<string>(url, {
    method: "GET",
    headers: { Authorization: config.basicAuth },
  });
});
