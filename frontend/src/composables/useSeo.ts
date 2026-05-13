import { type MaybeRefOrGetter } from "vue";

export type SeoInput = {
  title: MaybeRefOrGetter<string>;
  description: MaybeRefOrGetter<string>;
  /** Falls back to title if omitted */
  ogTitle?: MaybeRefOrGetter<string>;
};

export function useSeo({ title, description, ogTitle }: SeoInput) {
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
