import { describe, expect, it } from "vitest";
import { useNavigateActionItem } from "~/composables/useActionMenuItem/useNavigateActionItem";
import PdfIcon from "~icons/custom/pdf";

describe("useNavigateActionItem", () => {
  it("creates an ActionMenuItem with the given values", async () => {
    const item = useNavigateActionItem(
      "Some label",
      PdfIcon,
      "https://example.com",
    );

    expect(item.label).toEqual("Some label");
    expect(item.iconComponent).toEqual(PdfIcon);
    expect(item.url).toEqual("https://example.com");
    expect(item.disabled).toBeFalsy();
  });

  it("sets 'disabled' to true if no url is provided", async () => {
    const item = useNavigateActionItem("Some label", PdfIcon);

    expect(item.url).toBeUndefined();
    expect(item.disabled).toBeTruthy();
  });
});
