import { beforeEach, describe, expect, it, vi } from "vitest";
import { useCopyUrlActionItem } from "~/composables/useActionMenuItem/useCopyUrlActionItem";
import PdfIcon from "~icons/custom/pdf";
import IcBaselineCheck from "~icons/ic/baseline-check";
import IconLink from "~icons/ic/outline-link";

const { mockToastAdd } = vi.hoisted(() => ({
  mockToastAdd: vi.fn(),
}));

vi.mock("primevue/usetoast", () => ({
  useToast: () => ({
    add: mockToastAdd,
  }),
}));

describe("useCopyUrlActionItem", () => {
  beforeEach(() => {
    vi.resetAllMocks();
    vi.stubGlobal("navigator", {
      clipboard: {
        writeText: vi.fn(),
      },
    });
  });

  it("creates an ActionMenuItem with default values", async () => {
    const item = useCopyUrlActionItem();

    expect(item.label).toEqual("Link kopieren");
    expect(item.iconComponent).toEqual(IconLink);
    expect(item.disabled).toBeTruthy();

    expect(navigator.clipboard.writeText).not.toHaveBeenCalled();
    expect(mockToastAdd).not.toHaveBeenCalled();

    // command should do nothing when no url is given
    await item.command?.();

    expect(navigator.clipboard.writeText).not.toHaveBeenCalled();
    expect(mockToastAdd).not.toHaveBeenCalled();
  });

  it("creates an ActionMenuItem with provided values", async () => {
    const item = useCopyUrlActionItem("https://example.com", "Copy!", PdfIcon);

    expect(item.label).toEqual("Copy!");
    expect(item.iconComponent).toEqual(PdfIcon);
    expect(item.disabled).toBeFalsy();

    expect(navigator.clipboard.writeText).not.toHaveBeenCalled();
    expect(mockToastAdd).not.toHaveBeenCalled();

    await item.command?.();

    expect(navigator.clipboard.writeText).toHaveBeenCalledExactlyOnceWith(
      "https://example.com",
    );

    expect(mockToastAdd).toHaveBeenCalledExactlyOnceWith(
      expect.objectContaining({
        summary: "Kopiert!",
      }),
    );
  });

  it("reactively switches label and icon to confirmation state after copying", async () => {
    vi.useFakeTimers();
    const item = useCopyUrlActionItem("https://example.com", "Copy!", PdfIcon);

    expect(item.label).toEqual("Copy!");
    expect(item.iconComponent).toEqual(PdfIcon);

    await item.command?.();

    expect(item.label).toEqual("Link kopiert");
    expect(item.iconComponent).toEqual(IcBaselineCheck);

    vi.runAllTimers();

    expect(item.label).toEqual("Copy!");
    expect(item.iconComponent).toEqual(PdfIcon);

    vi.useRealTimers();
  });

  it("reactively switches label and icon to confirmation state using default values", async () => {
    vi.useFakeTimers();
    const item = useCopyUrlActionItem("https://example.com");

    expect(item.label).toEqual("Link kopieren");
    expect(item.iconComponent).toEqual(IconLink);

    await item.command?.();

    expect(item.label).toEqual("Link kopiert");
    expect(item.iconComponent).toEqual(IcBaselineCheck);

    vi.runAllTimers();

    expect(item.label).toEqual("Link kopieren");
    expect(item.iconComponent).toEqual(IconLink);

    vi.useRealTimers();
  });
});
