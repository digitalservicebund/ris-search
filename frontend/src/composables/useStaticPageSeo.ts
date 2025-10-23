import { staticPageSeo } from "~/utils/i18n/staticPageSeo";
import type { StaticPage } from "~/utils/i18n/staticPageSeo";

export function useStaticPageSeo(page: StaticPage) {
  const entry = staticPageSeo[page];

  const title = entry.title;
  const description = entry.description;
  const url = useRequestURL();

  useHead({
    title,
    link: [{ rel: "canonical", href: url.href }],
    meta: [
      { name: "description", content: description },
      { property: "og:type", content: "article" },
      { property: "og:title", content: title },
      { property: "og:description", content: description },
      { property: "og:url", content: url.href },
      { name: "twitter:title", content: title },
      { name: "twitter:description", content: description },
    ],
  });
}
