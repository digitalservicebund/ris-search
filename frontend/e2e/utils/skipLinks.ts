import type { Page } from "@playwright/test";
import { expect } from "./fixtures";

export type SkipLinkExpectation = {
  label: string;
  target: `#${string}`;
};

export async function expectPageSkipLinks(
  page: Page,
  skipLinks: readonly SkipLinkExpectation[],
) {
  const nav = page.getByRole("navigation", { name: "Sprunglinks" });
  await expect(nav).toBeAttached();

  for (const { label, target } of skipLinks) {
    const link = nav.getByRole("link", { name: label });
    const targetElement = page.locator(target);

    await link.focus();
    await expect(link).toBeVisible();

    await link.press("Enter");

    // The SkipLink component sets tabindex="-1" synchronously, then calls
    // focus(). Wait for the attribute first to let the async focus settle.
    await expect(targetElement).toHaveAttribute("tabindex", "-1");
    await expect(targetElement).toBeFocused();
  }
}
