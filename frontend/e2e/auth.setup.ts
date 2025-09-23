import { test as setup } from "@playwright/test";
import { authFile } from "../playwright.config";

const credentials = {
  username: process.env.E2E_KEYCLOAK_USERNAME ?? "jane.doe",
  password: process.env.E2E_KEYCLOAK_PASSWORD ?? "test",
};

setup("authenticate", async ({ page }) => {
  await page.goto("/");

  // going against Playwright recommendations, these locators use XPath expressions
  // to ensure compatibility with different Keycloak implementations
  await page
    .getByRole("textbox", { name: "Username or email" })
    .fill(credentials.username);
  await page
    .getByRole("textbox", { name: "Password" })
    .fill(credentials.password);
  await page.getByRole("button", { name: "Sign In" }).click();
  // Wait until the page receives the cookies.
  //
  // Sometimes login flow sets cookies in the process of several redirects.
  // Wait for the final URL to ensure that the cookies are actually set.
  await page.waitForURL("/");

  // End of authentication steps.

  await page.context().storageState({ path: authFile });
});
