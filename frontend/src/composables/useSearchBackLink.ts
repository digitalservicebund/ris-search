import { useRoute } from "#app";
import type { RouteLocationRaw } from "#vue-router";
import type { DocumentKind } from "~/types/api";

export type SearchBackLink = {
  label: string;
  route: RouteLocationRaw;
};

const ALLOWED_PREFIXES = ["/suche", "/erweiterte-suche"] as const;

/**
 * Returns the label and route for the "back to search" breadcrumb on document
 * detail pages.
 *
 * If the `from` query parameter is present and points to one of the search
 * pages, it is used as the breadcrumb target so that the full search state
 * (query, filters, pagination, etc.) is restored. Otherwise the breadcrumb
 * falls back to a clean search filtered by `fallbackDocumentKind`.
 */
export function useSearchBackLink(
  fallbackDocumentKind: DocumentKind,
): ComputedRef<SearchBackLink> {
  const route = useRoute();

  return computed(() => {
    const from = searchParamToString(route.query.from);

    if (from) {
      for (const prefix of ALLOWED_PREFIXES) {
        if (from === prefix || from.startsWith(`${prefix}?`)) {
          const label = prefix === "/suche" ? "Suche" : "Erweiterte Suche";
          return { label, route: from };
        }
      }
    }

    return {
      label: "Suche",
      route: `/suche?documentKind=${fallbackDocumentKind}`,
    };
  });
}
