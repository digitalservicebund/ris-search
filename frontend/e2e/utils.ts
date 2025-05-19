import type { Page } from "@playwright/test";

export async function getDisplayedResultCount(page: Page) {
  const resultCountElement = page.locator("output", {
    hasText: "Suchergebnis",
  });
  const text = await resultCountElement.innerText();
  if (text.startsWith("Keine Suchergebnisse")) return 0;
  const digits = text.replace(/\D+/g, "");
  return Number.parseInt(digits);
}
