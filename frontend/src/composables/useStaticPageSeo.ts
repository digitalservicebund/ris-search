import staticPageSeo from "~/i18n/staticPageSeo.json";

export function useStaticPageSeo(page: keyof typeof staticPageSeo) {
  const entry = staticPageSeo[page];
  if (!entry) return;

  const title = entry.titel;
  const description = entry.beschreibung;
  const url = useRequestURL();

  useHead({
    title,
    link: [{ rel: "canonical", href: url.href }],
    meta: [
      { name: "description", content: description },
      { property: "og:title", content: title },
      { property: "og:description", content: description },
      { property: "og:url", content: url.href },
      { name: "twitter:title", content: title },
      { name: "twitter:description", content: description },
    ],
  });
}
