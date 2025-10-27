import { test, expect } from "@playwright/test";

test.describe("Cookie Consent", () => {
  test.beforeEach(async ({ context }) => {
    const cookies = await context.cookies();
    const consentCookie = cookies.find(
      (cookie) => cookie.name === "consent_given",
    );
    if (consentCookie) {
      await context.clearCookies({ name: "consent_given" });
    }
  });

  test.describe("With JavaScript enabled", () => {
    test("banner appears when no consent given", async ({ page }) => {
      await page.goto("/");
      await expect(page.getByTestId("cookie-banner")).toBeVisible();
    });

    test("accept button sets cookie and hides banner", async ({
      page,
      context,
    }) => {
      await page.goto("/");
      await page.getByTestId("accept-cookie").click();
      await expect(page.getByTestId("cookie-banner")).not.toBeVisible();
      const cookies = await context.cookies();
      const consentCookie = cookies.find(
        (cookie) => cookie.name === "consent_given",
      );
      expect(consentCookie?.value).toBe("true");
      await page.reload();
      await expect(page.getByTestId("cookie-banner")).not.toBeVisible();
    });

    test("decline button sets cookie and hides banner", async ({
      page,
      context,
    }) => {
      await page.goto("/");
      await page.getByTestId("decline-cookie").click();
      const cookies = await context.cookies();
      const consentCookie = cookies.find(
        (cookie) => cookie.name === "consent_given",
      );
      expect(consentCookie?.value).toBe("false");
      await page.reload();
      await expect(page.getByTestId("cookie-banner")).not.toBeVisible();
    });

    test("cookie settings page - accept button works", async ({
      page,
      context,
    }) => {
      await page.goto("/cookie-einstellungen");
      await expect(
        page.getByText("Es werden keine anonymen Nutzungsdaten gespeichert"),
      ).toBeVisible();
      await page.getByTestId("settings-accept-cookie").click();
      await expect(
        page.getByText("Es werden anonyme Nutzungsdaten gespeichert"),
      ).toBeVisible();
      const cookies = await context.cookies();
      const consentCookie = cookies.find(
        (cookie) => cookie.name === "consent_given",
      );
      expect(consentCookie?.value).toBe("true");
      expect(page.url()).toContain("/cookie-einstellungen");
    });

    test("cookie settings page - decline button works", async ({
      page,
      context,
    }) => {
      await context.addCookies([
        {
          name: "consent_given",
          value: "true",
          domain: "localhost",
          path: "/",
        },
      ]);

      await page.goto("/cookie-einstellungen");
      await expect(
        page.getByText("Es werden anonyme Nutzungsdaten gespeichert"),
      ).toBeVisible();

      await page.getByTestId("settings-decline-cookie").click();

      await expect(
        page.getByText("Es werden keine anonymen Nutzungsdaten gespeichert"),
      ).toBeVisible();

      const cookies = await context.cookies();
      const consentCookie = cookies.find(
        (cookie) => cookie.name === "consent_given",
      );
      expect(consentCookie?.value).toBe("false");
      expect(page.url()).toContain("/cookie-einstellungen");
    });

    test("banner links work", async ({ page }) => {
      await page.goto("/");
      await expect(page.getByTestId("cookie-banner")).toBeVisible();
      await page
        .getByTestId("cookie-banner")
        .getByRole("link", { name: "Cookie-Einstellungen" })
        .click();
      await expect(page).toHaveURL(/\/cookie-einstellungen/);
      await page.goto("/");
      await page
        .getByTestId("cookie-banner")
        .getByRole("link", { name: "Datenschutzerklärung" })
        .click();
      await expect(page).toHaveURL(/\/datenschutz/);
    });
  });

  test.describe("Without JavaScript (Progressive Enhancement)", () => {
    test("banner appears and accept button works", async ({ browser }) => {
      const context = await browser.newContext({
        javaScriptEnabled: false,
      });
      const page = await context.newPage();
      await page.goto("/", { waitUntil: "networkidle" });
      await expect(page.getByTestId("cookie-banner")).toBeVisible();
      await page.getByTestId("accept-cookie").click();
      await page.waitForLoadState("networkidle");
      await expect(page.getByTestId("cookie-banner")).not.toBeVisible();
      const cookies = await context.cookies();
      const consentCookie = cookies.find(
        (cookie) => cookie.name === "consent_given",
      );
      expect(consentCookie?.value).toBe("true");

      await context.close();
    });

    test("banner appears and decline button works", async ({ browser }) => {
      const context = await browser.newContext({
        javaScriptEnabled: false,
      });
      const page = await context.newPage();

      await page.goto("/", { waitUntil: "networkidle" });
      await expect(page.getByTestId("cookie-banner")).toBeVisible();
      await page.getByTestId("decline-cookie").click();
      await page.waitForLoadState("networkidle");
      await expect(page.getByTestId("cookie-banner")).not.toBeVisible();
      const cookies = await context.cookies();
      const consentCookie = cookies.find(
        (cookie) => cookie.name === "consent_given",
      );
      expect(consentCookie?.value).toBe("false");
      await context.close();
    });

    test("cookie settings page - accept button works", async ({ browser }) => {
      const context = await browser.newContext({
        javaScriptEnabled: false,
      });
      const page = await context.newPage();
      await page.goto("/cookie-einstellungen", { waitUntil: "networkidle" });
      await expect(
        page.getByText("Es werden keine anonymen Nutzungsdaten gespeichert"),
      ).toBeVisible();

      await page.getByTestId("settings-accept-cookie").click();
      await page.waitForURL(/\/cookie-einstellungen/, {
        waitUntil: "networkidle",
      });
      await expect(
        page.getByText("Es werden anonyme Nutzungsdaten gespeichert"),
      ).toBeVisible();
      const cookies = await context.cookies();
      const consentCookie = cookies.find(
        (cookie) => cookie.name === "consent_given",
      );
      expect(consentCookie?.value).toBe("true");

      await context.close();
    });

    test("cookie settings page - decline button works", async ({ browser }) => {
      const context = await browser.newContext({
        javaScriptEnabled: false,
      });

      await context.addCookies([
        {
          name: "consent_given",
          value: "true",
          domain: "localhost",
          path: "/",
        },
      ]);

      const page = await context.newPage();
      await page.goto("/cookie-einstellungen", { waitUntil: "networkidle" });

      await expect(
        page.getByText("Es werden anonyme Nutzungsdaten gespeichert"),
      ).toBeVisible();

      await page.getByTestId("settings-decline-cookie").click();

      await page.waitForURL(/\/cookie-einstellungen/, {
        waitUntil: "networkidle",
      });

      await expect(
        page.getByText("Es werden keine anonymen Nutzungsdaten gespeichert"),
      ).toBeVisible();

      const cookies = await context.cookies();
      const consentCookie = cookies.find(
        (cookie) => cookie.name === "consent_given",
      );
      expect(consentCookie?.value).toBe("false");

      await context.close();
    });
  });
});
