import { getHeader, setHeader, defineEventHandler } from "h3";
import { useStorage } from "#imports";
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

  const storage = useStorage("assets:server");
  if (privateFeaturesEnabled) {
    return storage.getItem("robots.staging.txt");
  } else {
    return storage.getItem("robots.public.txt");
  }
});
