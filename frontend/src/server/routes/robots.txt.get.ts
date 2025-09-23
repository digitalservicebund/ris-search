import { getRequestURL, getHeader, setHeader } from "h3";
import { useProfile } from "~/composables/useProfile";

export default defineEventHandler(async (event) => {
  const { isPublicProfile } = useProfile();
  const userAgent = (getHeader(event, "User-Agent") ?? "").toUpperCase();
  let file = isPublicProfile() ? "robots.public.txt" : "robots.staging.txt";
  if (userAgent === "DG_JUSTICE_CRAWLER") file = "robots.dg.txt";

  setHeader(event, "Content-Type", "text/plain; charset=utf-8");
  setHeader(event, "Vary", "User-Agent");

  const { origin } = getRequestURL(event);
  const url = `${origin}/${file}`;

  return await $fetch<string>(url, { method: "GET" });
});
