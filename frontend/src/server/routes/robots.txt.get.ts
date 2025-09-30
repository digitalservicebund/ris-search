import { getRequestURL, getHeader, setHeader, defineEventHandler } from "h3";
import { requireAccessTokenWithRefresh } from "../auth";
import { useRuntimeConfig } from "#imports";
import { isInternalProfile } from "~/utils/config";

export default defineEventHandler(async (event) => {
  const userAgent = (getHeader(event, "User-Agent") ?? "").toUpperCase();
  let file = isInternalProfile() ? "robots.staging.txt" : "robots.public.txt";
  if (userAgent === "DG_JUSTICE_CRAWLER") file = "robots.dg.txt";

  setHeader(event, "Content-Type", "text/plain; charset=utf-8");
  setHeader(event, "Vary", "User-Agent");
  const { origin } = getRequestURL(event);

  if (userAgent === "DG_JUSTICE_CRAWLER") {
    const token = await requireAccessTokenWithRefresh(event);
    const runtimeConfig = useRuntimeConfig(event);

    return await $fetch<string>(
      `${runtimeConfig.risBackendUrl}/v1/eclicrawler/robots.txt`,
      {
        headers: { Authorization: `Bearer ${token}` },
        method: "GET",
      },
    );
  }

  const url = `${origin}/${file}`;
  return await $fetch<string>(url, { method: "GET" });
});
