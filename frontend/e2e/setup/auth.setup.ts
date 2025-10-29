import { test as setup } from "@playwright/test";
import { authFile } from "../../playwright.config";
import { loginUser } from "../utils/auth";

setup("authenticate", async ({ page }) => {
  await page.goto("/");
  await loginUser(page);
  await page.context().storageState({ path: authFile });
});
