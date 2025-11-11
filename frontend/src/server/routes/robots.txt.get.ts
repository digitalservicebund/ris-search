import { getRequestURL, getHeader, setHeader, defineEventHandler } from "h3";
import { useBackendURL } from "~/composables/useBackendURL";
import { usePrivateFeaturesFlag } from "~/composables/usePrivateFeaturesFlag";

export default defineEventHandler(async (event) => {
  const userAgent = (getHeader(event, "User-Agent") ?? "").toUpperCase();
  const privateFeaturesEnabled = usePrivateFeaturesFlag();
  let file = privateFeaturesEnabled
    ? "robots.staging.txt"
    : "robots.public.txt";
  if (userAgent === "DG_JUSTICE_CRAWLER") file = "robots.dg.txt";

  setHeader(event, "Content-Type", "text/plain; charset=utf-8");
  setHeader(event, "Vary", "User-Agent");
  const { origin } = getRequestURL(event);

  if (userAgent === "DG_JUSTICE_CRAWLER") {
    const backendUrl = useBackendURL();
    return await $fetch<string>(`${backendUrl}/v1/eclicrawler/robots.txt`, {
      method: "GET",
    });
  }

  const url = `${origin}/${file}`;
  return await $fetch<string>(url, { method: "GET" });
});
