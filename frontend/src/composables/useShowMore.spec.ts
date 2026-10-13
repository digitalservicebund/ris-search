import { describe, expect, it } from "vitest";
import { useShowMore } from "./useShowMore";

describe("useShowMore", () => {
  it("limits the items to the display limit", () => {
    const { itemsToDisplay, hasMore, showingAll } = useShowMore(
      ["A", "B", "C", "D"],
      2,
    );

    expect(itemsToDisplay.value).toEqual(["A", "B"]);
    expect(hasMore.value).toBe(true);
    expect(showingAll.value).toBe(false);
  });

  it("has no more items when the items fit into the display limit", () => {
    const { itemsToDisplay, hasMore } = useShowMore(["A", "B"], 2);

    expect(itemsToDisplay.value).toEqual(["A", "B"]);
    expect(hasMore.value).toBe(false);
  });

  it("toggles between all items and the limited items", () => {
    const { itemsToDisplay, showingAll, toggle } = useShowMore(
      ["A", "B", "C", "D"],
      2,
    );

    toggle();

    expect(showingAll.value).toBe(true);
    expect(itemsToDisplay.value).toEqual(["A", "B", "C", "D"]);

    toggle();

    expect(showingAll.value).toBe(false);
    expect(itemsToDisplay.value).toEqual(["A", "B"]);
  });

  it("updates when the items passed as a ref change", () => {
    const items = ref<string[]>([]);
    const { itemsToDisplay, hasMore } = useShowMore(items, 2);

    expect(hasMore.value).toBe(false);

    items.value = ["A", "B", "C"];

    expect(itemsToDisplay.value).toEqual(["A", "B"]);
    expect(hasMore.value).toBe(true);
  });

  it("updates when the items passed as a getter change", () => {
    const source = ref<{ items?: string[] }>({});
    const { itemsToDisplay, hasMore } = useShowMore(
      () => source.value.items ?? [],
      2,
    );

    expect(itemsToDisplay.value).toEqual([]);

    source.value = { items: ["A", "B", "C"] };

    expect(itemsToDisplay.value).toEqual(["A", "B"]);
    expect(hasMore.value).toBe(true);
  });
});
