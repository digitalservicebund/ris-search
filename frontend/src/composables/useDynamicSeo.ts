import { computed, type ComputedRef } from "vue";
import { useHead, useRequestURL } from "#app";

export type DynamicSeoInput = {
  title: ComputedRef<string | undefined>;
  description: ComputedRef<string | undefined>;
};

export type CanonicalLink = { rel: "canonical"; href: string };

export type SeoMetaTag =
  | {
      name: "description" | "twitter:title" | "twitter:description";
      content: string;
    }
  | {
      property: "og:type" | "og:title" | "og:description" | "og:url";
      content: string;
    };

function isNonEmpty(value?: string): value is string {
  return typeof value === "string" && value.trim() !== "";
}

export function useDynamicSeo(input: DynamicSeoInput) {
  const { title, description } = input;

  const url = useRequestURL();
  const link = computed<CanonicalLink[]>(() => [
    { rel: "canonical", href: url.href },
  ]);

  const meta = computed<SeoMetaTag[]>(() => {
    const tags: SeoMetaTag[] = [
      { property: "og:type", content: "article" },
      { property: "og:url", content: url.href },
    ];

    if (isNonEmpty(description.value)) {
      tags.push(
        { name: "description", content: description.value },
        { property: "og:description", content: description.value },
        { name: "twitter:description", content: description.value },
      );
    }

    if (isNonEmpty(title.value)) {
      tags.push(
        { property: "og:title", content: title.value },
        { name: "twitter:title", content: title.value },
      );
    }

    return tags;
  });

  useHead({ title, link, meta });
  return { title, description, link, meta };
}
