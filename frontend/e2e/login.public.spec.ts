import { test } from "@playwright/test";

const displayName = process.env.E2E_KEYCLOAK_USER_DISPLAY_NAME ?? "Jane Doe";

test("is authenticated", async ({ page }, workerInfo) => {
  await page.goto("/");
  if (workerInfo.project.name === "mobile")
    await page.getByRole("button", { name: "Menu" }).click();
  await page.getByRole("button", { name: `Abmelden` }).isVisible();
  await page.getByText(displayName).isVisible();
});
