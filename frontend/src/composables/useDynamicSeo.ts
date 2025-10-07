import { computed, type ComputedRef } from "vue";
import { useHead, useRequestURL } from "#app";

export function useDynamicSeo(input: {
  title: ComputedRef<string | undefined>;
  description: ComputedRef<string | undefined>;
}) {
  const title = input.title;
  const description = input.description;

  const url = useRequestURL();
  const link = computed(() => [{ rel: "canonical", href: url.href }]);

  const meta = computed(() =>
    [
      { name: "description", content: description.value },
      { property: "og:type", content: "article" },
      { property: "og:title", content: title.value },
      { property: "og:description", content: description.value },
      { property: "og:url", content: url.href },
      { name: "twitter:title", content: title.value },
      { name: "twitter:description", content: description.value },
    ].filter((t) => typeof t.content === "string" && t.content.trim() !== ""),
  );

  useHead({ title, link, meta });
  return { title, description, link, meta };
}
