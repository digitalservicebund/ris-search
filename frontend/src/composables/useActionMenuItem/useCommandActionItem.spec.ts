import { describe, expect, it, vi } from "vitest";
import { useCommandActionItem } from "~/composables/useActionMenuItem/useCommandActionItem";
import PdfIcon from "~icons/custom/pdf";

describe("useCommandActionItem", () => {
  it("creates an ActionMenuItem with the given values", async () => {
    const someAction = vi.fn();
    const item = useCommandActionItem(
      "Some label",
      PdfIcon,
      async () => {
        someAction();
      },
      true,
    );

    expect(item.label).toEqual("Some label");
    expect(item.iconComponent).toEqual(PdfIcon);

    expect(someAction).not.toHaveBeenCalled();
    item.command();
    expect(someAction).toHaveBeenCalledOnce();

    expect(item.disabled).toBeTruthy();
  });

  it("sets 'disabled' to false by default", async () => {
    const item = useCommandActionItem("Some label", PdfIcon, async () => {});

    expect(item.disabled).toBeFalsy();
  });
});
