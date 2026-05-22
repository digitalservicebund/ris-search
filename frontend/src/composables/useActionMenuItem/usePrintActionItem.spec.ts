import { describe, expect, it, vi } from "vitest";
import IconPrint from "~icons/ic/baseline-print";
import { usePrintActionItem } from "~/composables/useActionMenuItem/usePrintActionItem";

describe("usePrintActionItem", () => {
  it("creates an ActionMenuItem with print label, icon and action", async () => {
    globalThis.print = vi.fn();

    const printItem = usePrintActionItem();

    expect(printItem.label).toEqual("Drucken");
    expect(printItem.iconComponent).toEqual(IconPrint);
    expect(printItem.disabled).toBeFalsy();
    expect(printItem.url).toBeUndefined();

    printItem.command?.();
    expect(globalThis.print).toHaveBeenCalledOnce();
  });
});
