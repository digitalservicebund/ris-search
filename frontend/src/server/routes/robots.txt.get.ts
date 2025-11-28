import { getHeader, setHeader, defineEventHandler } from "h3";
import useBackendUrl from "~/composables/useBackendUrl";
import { usePrivateFeaturesFlag } from "~/composables/usePrivateFeaturesFlag";

export default defineEventHandler(async (event) => {
  const userAgent = (getHeader(event, "User-Agent") ?? "").toUpperCase();
  const privateFeaturesEnabled = usePrivateFeaturesFlag();

  setHeader(event, "Content-Type", "text/plain; charset=utf-8");
  setHeader(event, "Vary", "User-Agent");

  if (userAgent === "DG_JUSTICE_CRAWLER") {
    return await $fetch<string>(useBackendUrl(`/v1/eclicrawler/robots.txt`), {
      method: "GET",
    });
  }

  if (privateFeaturesEnabled) {
    return (
      "User-agent: *\n" +
      "Allow: /\n\n" +
      "Sitemap: https://ris-portal.dev.ds4g.net/sitemap_index.xml"
    );
  } else {
    return "User-agent: *\n" + "Disallow: /";
  }
});
