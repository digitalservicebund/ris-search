import type { Page } from "~/components/Pagination.vue";

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
