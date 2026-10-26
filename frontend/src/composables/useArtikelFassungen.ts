import type { JSONLDList, LegislationExpression } from "~/types/api.ts";
import { parseDocument } from "~/utils/htmlParser.ts";

interface ArtikelFassungCacheEntry {
  html?: string;
  gesamtausgaben: LegislationExpression[];
  error: boolean;
}

const errorEntry = { error: true, gesamtausgaben: [] };

export function useArtikelFassungen() {
  const { $risBackend } = useNuxtApp();
  const fassungenCache = ref(new Map<string, ArtikelFassungCacheEntry>());

  async function fetchFassungHtml(contentUrl: string) {
    try {
      const html = await $risBackend<string>(contentUrl, {
        headers: {
          Accept: "text/html",
        },
      });

      const document = parseDocument(html);
      return document.body.innerHTML;
    } catch {
      return undefined;
    }
  }

  async function fetchGesamtausgaben(
    revision: string,
  ): Promise<LegislationExpression[] | undefined> {
    try {
      const data = await $risBackend<JSONLDList<LegislationExpression>>(
        `/v1/article/${revision}/legislations`,
      );

      return data.member ?? [];
    } catch {
      return undefined;
    }
  }

  async function updateCache({
    key,
    contentUrl,
    revision,
  }: {
    key: string;
    contentUrl?: string;
    revision?: string;
  }) {
    const existingEntry = fassungenCache.value.get(key);
    if (existingEntry && !existingEntry.error) return;

    if (!contentUrl || !revision) {
      fassungenCache.value.set(key, errorEntry);
      return;
    }

    const [html, gesamtausgaben] = await Promise.all([
      fetchFassungHtml(contentUrl),
      fetchGesamtausgaben(revision),
    ]);

    if (html === undefined || gesamtausgaben === undefined) {
      fassungenCache.value.set(key, errorEntry);
      return;
    }

    fassungenCache.value.set(key, {
      html,
      gesamtausgaben,
      error: false,
    });
  }

  return {
    fassungenCache,
    updateCache,
  };
}
