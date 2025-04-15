import { test, expect } from "@playwright/test";

const displayName = process.env.E2E_KEYCLOAK_USER_DISPLAY_NAME ?? "Jane Doe";

test("is authenticated", async ({ page }) => {
  // authentication should happen in the setup flow in auth.setup.ts
  await page.goto("/");

  const expectedLogoutButtonName = `${displayName} Abmelden`;
  await expect(page.getByText(expectedLogoutButtonName)).toBeVisible();
});
