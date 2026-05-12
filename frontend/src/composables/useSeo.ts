import { computed, toValue, type MaybeRefOrGetter } from "vue";

export type SeoInput = {
  title: MaybeRefOrGetter<string>;
  description: MaybeRefOrGetter<string>;
  /** Falls back to title if omitted */
  ogTitle?: MaybeRefOrGetter<string>;
};

export function useSeo({ title, description, ogTitle }: SeoInput) {
  const url = useRequestURL();

  const resolvedTitle = computed(() => toValue(title));
  const resolvedDescription = computed(() => toValue(description));
  const resolvedOgTitle = computed(() => toValue(ogTitle ?? title));

  useSeoMeta({
    title: resolvedTitle,
    description: resolvedDescription,
    ogType: "article",
    ogTitle: resolvedOgTitle,
    ogDescription: resolvedDescription,
    ogUrl: url.href,
    twitterTitle: resolvedOgTitle,
    twitterDescription: resolvedDescription,
  });

  useHead({
    link: [{ rel: "canonical", href: url.href }],
  });
}
