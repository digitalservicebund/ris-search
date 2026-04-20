import { computed } from "vue";
import { ensureStartingSlash, prependBase, removeBase } from "../utils";
import { useData } from "./data";

export function useLangs() {
  const { site, localeIndex, page } = useData();
  const base = site.value.base || "/";

  const currentLang = computed(() => ({
    label: site.value.locales[localeIndex.value]?.label,
    link: prependBase(
      site.value.locales[localeIndex.value]?.link ||
        (localeIndex.value === "root" ? "/" : `/${localeIndex.value}/`),
      base
    ),
  }));

  const localeLinks = computed(() => {
    const currentLink = removeBase(currentLang.value.link, base);
    return Object.entries(site.value.locales).flatMap(([key, value]) =>
      currentLang.value.label === value.label
        ? []
        : {
            text: value.label,
            link: prependBase(
              normalizeLink(
                value.link || (key === "root" ? "/" : `/${key}/`),
                page.value.relativePath.slice(currentLink.length - 1),
                !site.value.cleanUrls
              ),
              base
            ),
          }
    );
  });

  return { localeLinks, currentLang };
}

function normalizeLink(link: string, path: string, addExt: boolean) {
  return (
    link.replace(/\/$/, "") +
    ensureStartingSlash(
      path
        .replace(/(^|\/)index\.md$/, "$1")
        .replace(/\.md$/, addExt ? ".html" : "")
    )
  );
}
