import type { Page } from "~/components/Pagination/Pagination.vue";
import { usePostHogStore } from "~/stores/usePostHogStore";

export const parsePageNumber = (id?: string) => {
  if (!id) return { page: undefined, size: undefined };
  const searchParamsPart = id.substring(id.indexOf("?"));
  const params = new URLSearchParams(searchParamsPart);
  const page = params.get("pageIndex");
  const size = params.get("size");
  return {
    page: page ? Number.parseInt(page) : undefined,
    size: size ? Number.parseInt(size) : undefined,
  };
};

export function buildResultCountString(currentPage: Page) {
  if (!currentPage || currentPage.totalItems === 0) {
    usePostHogStore().noSearchResults();
    return "Keine Suchergebnisse gefunden";
  }

  if (!currentPage.totalItems) {
    return "";
  }
  if (currentPage.totalItems === 1) {
    return "1 Suchergebnis";
  }
  if (currentPage.totalItems === 10000) {
    return "Mehr als 10.000 Suchergebnisse";
  }
  return `${currentPage.totalItems.toLocaleString("de-DE")} Suchergebnisse`;
}

export function buildItemsOnPageString(page?: Page | null) {
  if (!page?.member.length || !page?.totalItems) {
    return "";
  }

  const { page: pageIndex, size: pageSize } = parsePageNumber(page["@id"]);
  if (pageIndex === undefined || !pageSize) return "";

  const firstItemIndex = pageIndex * pageSize + 1;
  const lastItemIndex = firstItemIndex + page.member.length - 1;
  const rangeExpression =
    firstItemIndex === lastItemIndex
      ? firstItemIndex.toString()
      : `${firstItemIndex}–${lastItemIndex}`;

  const totalCount =
    page.totalItems === 10000
      ? "mehr als 10.000"
      : page.totalItems.toLocaleString("de-DE");
  return `Treffer ${rangeExpression} von ${totalCount}`;
}
