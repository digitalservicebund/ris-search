export function useStaticPageSeo(
  title: string,
  description: string,
  ogTitle?: string,
) {
  const url = useRequestURL();

  useSeoMeta({
    title: title,
    description: description,
    ogType: "article",
    ogTitle: ogTitle ?? title,
    ogDescription: description,
    ogUrl: url.href,
    twitterTitle: ogTitle ?? title,
    twitterDescription: description,
  });

  useHead({
    link: [{ rel: "canonical", href: url.href }],
  });
}
