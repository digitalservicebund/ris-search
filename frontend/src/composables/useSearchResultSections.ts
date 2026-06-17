import type { TextMatch } from "~/types/api";

export interface FieldDisplayProperties {
  id: string;
  title: string;
}

export type ExtendedTextMatch = TextMatch & FieldDisplayProperties;

/**
 * Composable that computes the ordered, sanitised preview sections for a search
 * result card and provides the shared `trackResultClick` analytics handler.
 *
 * @param textMatches - The raw text matches returned by the API for this result
 * @param fields - Ordered map from API field name to display properties (id,
 *   title)
 * @param maxSections - Optional cap on the number of sections shown (default:
 *   unlimited)
 */
export function useSearchResultSections(
  textMatches: MaybeRefOrGetter<TextMatch[]>,
  fields: Map<string, FieldDisplayProperties>,
  maxSections?: number,
) {
  const previewSections = computed<ExtendedTextMatch[]>(() => {
    const matchesVal = toValue(textMatches);
    const keys = [...fields.keys()];
    const sections = matchesVal
      .filter((match) => fields.has(match.name))
      .toSorted((a, b) => keys.indexOf(a.name) - keys.indexOf(b.name))
      .map<ExtendedTextMatch>((match) => ({
        ...match,
        text: sanitizeSearchResult(addEllipsis(match.text) ?? ""),
        ...fields.get(match.name)!, // always defined since we filtered above
      }));

    return maxSections !== undefined
      ? sections.slice(0, maxSections)
      : sections;
  });

  return previewSections;
}
