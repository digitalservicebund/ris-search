import { describe, expect, it } from "vitest";
import { useShowMore } from "./useShowMore";

describe("useShowMore", () => {
  it("limits the items to the display limit", () => {
    const { state } = useShowMore(["A", "B", "C", "D"], 2);

    expect(state.value).toEqual({
      itemsToDisplay: ["A", "B"],
      hasMore: true,
      showingAll: false,
    });
  });

  it("has no more items when the items fit into the display limit", () => {
    const { state } = useShowMore(["A", "B"], 2);

    expect(state.value.itemsToDisplay).toEqual(["A", "B"]);
    expect(state.value.hasMore).toBe(false);
  });

  it("toggles between all items and the limited items", () => {
    const { state, toggle } = useShowMore(["A", "B", "C", "D"], 2);

    toggle();

    expect(state.value.showingAll).toBe(true);
    expect(state.value.itemsToDisplay).toEqual(["A", "B", "C", "D"]);

    toggle();

    expect(state.value.showingAll).toBe(false);
    expect(state.value.itemsToDisplay).toEqual(["A", "B"]);
  });

  it("updates when the items passed as a ref change", () => {
    const items = ref<string[]>([]);
    const { state } = useShowMore(items, 2);

    expect(state.value.hasMore).toBe(false);

    items.value = ["A", "B", "C"];

    expect(state.value.itemsToDisplay).toEqual(["A", "B"]);
    expect(state.value.hasMore).toBe(true);
  });

  it("updates when the items passed as a getter change", () => {
    const source = ref<{ items?: string[] }>({});
    const { state } = useShowMore(() => source.value.items ?? [], 2);

    expect(state.value.itemsToDisplay).toEqual([]);

    source.value = { items: ["A", "B", "C"] };

    expect(state.value.itemsToDisplay).toEqual(["A", "B"]);
    expect(state.value.hasMore).toBe(true);
  });
});
