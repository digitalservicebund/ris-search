import { test } from "./utils/fixtures";

const displayName = process.env.E2E_KEYCLOAK_USER_DISPLAY_NAME ?? "Jane Doe";

test("is authenticated", async ({ page, isMobileTest }) => {
  await page.goto("/");
  if (isMobileTest) await page.getByRole("button", { name: "Menu" }).click();
  await page.getByRole("button", { name: `Abmelden` }).isVisible();
  await page.getByText(displayName).first().isVisible();
});
