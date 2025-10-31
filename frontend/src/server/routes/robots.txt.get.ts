import { getRequestURL, getHeader, setHeader, defineEventHandler } from "h3";
import { requireAccessTokenWithRefresh } from "../auth";
import { usePrivateFeaturesFlag } from "~/composables/usePrivateFeaturesFlag";

export default defineEventHandler(async (event) => {
  const userAgent = (getHeader(event, "User-Agent") ?? "").toUpperCase();
  const privateFeaturesEnabled = usePrivateFeaturesFlag();
  let file = privateFeaturesEnabled
    ? "robots.staging.txt"
    : "robots.public.txt";
  const config = useRuntimeConfig();
  if (userAgent === "DG_JUSTICE_CRAWLER") file = "robots.dg.txt";

  setHeader(event, "Content-Type", "text/plain; charset=utf-8");
  setHeader(event, "Vary", "User-Agent");
  const { origin } = getRequestURL(event);

  if (userAgent === "DG_JUSTICE_CRAWLER") {
    const headers: Record<string, string> = {};
    if (config.public.authEnabled) {
      const token = await requireAccessTokenWithRefresh(event);
      headers["Authorization"] = `Bearer ${token}`;
    }
    const backendUrl = config.risBackendUrl;
    return await $fetch<string>(`${backendUrl}/v1/eclicrawler/robots.txt`, {
      headers,
      method: "GET",
    });
  }

  const url = `${origin}/${file}`;
  return await $fetch<string>(url, { method: "GET" });
});
