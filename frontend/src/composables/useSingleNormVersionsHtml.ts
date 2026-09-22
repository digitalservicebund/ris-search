import { parseDocument } from "~/utils/htmlParser.ts";

export interface HtmlCacheEntry {
  html?: string;
  error: boolean;
}

export function useSingleNormVersionsHtml() {
  const { $risBackend } = useNuxtApp();
  const rowsHtml = ref(new Map<string, HtmlCacheEntry>());

  async function updateRowsHtml(rowKey: string, rowContentUrl: string) {
    if (rowsHtml.value.get(rowKey)?.html) return;

    try {
      const html = await $risBackend<string>(rowContentUrl, {
        headers: {
          Accept: "text/html",
        },
      });

      const document = parseDocument(html);
      rowsHtml.value.set(rowKey, {
        html: document.body.innerHTML,
        error: false,
      });
    } catch {
      rowsHtml.value.set(rowKey, { error: true });
    }
  }

  return {
    rowsHtml,
    updateRowsHtml,
  };
}
