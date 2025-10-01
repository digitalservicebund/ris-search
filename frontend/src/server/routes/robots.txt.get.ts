import { getRequestURL, getHeader, setHeader } from "h3";
import { isInternalProfile } from "~/utils/config";

export default defineEventHandler(async (event) => {
  const userAgent = (getHeader(event, "User-Agent") ?? "").toUpperCase();
  let file = isInternalProfile() ? "robots.staging.txt" : "robots.public.txt";
  if (userAgent === "DG_JUSTICE_CRAWLER") file = "robots.dg.txt";

  setHeader(event, "Content-Type", "text/plain; charset=utf-8");
  setHeader(event, "Vary", "User-Agent");

  const { origin } = getRequestURL(event);
  const url = `${origin}/${file}`;

  return await $fetch<string>(url, { method: "GET" });
});
