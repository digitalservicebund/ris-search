import { expect, navigate, test } from "./utils/fixtures";

test.describe("cookie settings page", () => {
  test("shows declined state by default and can accept cookies", async ({
    page,
    context,
  }) => {
    await context.clearCookies();
    await navigate(page, "/cookie-settings");

    const acceptButton = page.getByRole("button", {
      name: "Cookies akzeptieren",
    });
    const declineButton = page.getByRole("button", {
      name: "Cookies ablehnen",
    });

    await expect(
      page.getByText(
        "Ich bin mit der Nutzung von Analyse-Cookies nicht einverstanden.",
      ),
    ).toBeVisible();
    await expect(acceptButton).toBeVisible();

    await acceptButton.click();

    await expect(
      page.getByText(
        "Ich bin mit der Nutzung von Analyse-Cookies einverstanden.",
      ),
    ).toBeVisible();
    await expect(declineButton).toBeVisible();
  });

  test("can change cookie consent from accepted to declined", async ({
    page,
    context,
  }) => {
    await navigate(page, "/cookie-settings");

    // Set consent to true via cookie
    const url = new URL(page.url());
    await context.addCookies([
      {
        name: "consent_given",
        value: "true",
        domain: url.hostname,
        path: "/",
      },
    ]);

    await page.reload({ waitUntil: "networkidle" });

    const acceptButton = page.getByRole("button", {
      name: "Cookies akzeptieren",
    });
    const declineButton = page.getByRole("button", {
      name: "Cookies ablehnen",
    });

    await expect(
      page.getByText(
        "Ich bin mit der Nutzung von Analyse-Cookies einverstanden.",
      ),
    ).toBeVisible();
    await expect(declineButton).toBeVisible();

    await declineButton.click();

    await expect(
      page.getByText(
        "Ich bin mit der Nutzung von Analyse-Cookies nicht einverstanden.",
      ),
    ).toBeVisible();
    await expect(acceptButton).toBeVisible();
  });
});
