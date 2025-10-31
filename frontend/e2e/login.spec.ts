import { test } from "./utils/fixtures";

const displayName = process.env.E2E_KEYCLOAK_USER_DISPLAY_NAME ?? "Jane Doe";

test.beforeAll(async ({ isMobileTest, privateFeaturesEnabled }) => {
  test.skip(isMobileTest || !privateFeaturesEnabled);
});

test("is authenticated", async ({ page }) => {
  await page.goto("/");
  await page.getByRole("button", { name: `Abmelden` }).isVisible();
  await page.getByText(displayName).first().isVisible();
});
