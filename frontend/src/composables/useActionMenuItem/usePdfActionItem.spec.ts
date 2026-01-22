import { describe, expect, it } from "vitest";
import { usePdfActionItem } from "~/composables/useActionMenuItem/usePdfActionItem";
import PdfIcon from "~icons/custom/pdf";

describe("usePdfActionItem", () => {
  it("creates an ActionMenuItem with pdf label, icon", async () => {
    const pdfItem = usePdfActionItem();

    expect(pdfItem.label).toEqual("Als PDF speichern");
    expect(pdfItem.iconComponent).toEqual(PdfIcon);
    expect(pdfItem.disabled).toBeTruthy();
    expect(pdfItem.command).toBeUndefined();
  });
});
