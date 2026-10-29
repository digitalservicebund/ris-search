import { expect, navigate, noJsTest, test } from "./utils/fixtures";

test("shows declined state by default and can accept cookies", async ({
  page,
  context,
}) => {
  await context.clearCookies();
  await navigate(page, "/cookie-einstellungen");

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
  await navigate(page, "/cookie-einstellungen");

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

noJsTest(
  "can change cookie consent without JavaScript",
  { tag: ["@RISDEV-12927"] },
  async ({ page, noJsContext }) => {
    await navigate(page, "/cookie-einstellungen");

    await page.getByRole("button", { name: "Cookies akzeptieren" }).click();

    await expect(page).toHaveURL(/\/cookie-einstellungen$/);
    await expect(
      page.getByText(
        "Ich bin mit der Nutzung von Analyse-Cookies einverstanden.",
      ),
    ).toBeVisible();
    const cookies = await noJsContext.cookies();
    expect(cookies.find((c) => c.name === "consent_given")?.value).toBe("1");

    await page.getByRole("button", { name: "Cookies ablehnen" }).click();

    await expect(page).toHaveURL(/\/cookie-einstellungen$/);
    await expect(
      page.getByText(
        "Ich bin mit der Nutzung von Analyse-Cookies nicht einverstanden.",
      ),
    ).toBeVisible();
    const updatedCookies = await noJsContext.cookies();
    expect(updatedCookies.find((c) => c.name === "consent_given")?.value).toBe(
      "0",
    );
  },
);

noJsTest(
  "keeps consent given without JavaScript once JavaScript is enabled",
  { tag: ["@RISDEV-12927"] },
  async ({ page, noJsContext, browser }) => {
    await navigate(page, "/cookie-einstellungen");
    await page.getByRole("button", { name: "Cookies akzeptieren" }).click();
    await expect(
      page.getByRole("button", { name: "Cookies ablehnen" }),
    ).toBeVisible();

    const jsContext = await browser.newContext();
    await jsContext.addCookies(await noJsContext.cookies());
    const jsPage = await jsContext.newPage();
    await navigate(jsPage, "/cookie-einstellungen");

    await expect(
      jsPage.getByText(
        "Ich bin mit der Nutzung von Analyse-Cookies einverstanden.",
      ),
    ).toBeVisible();

    await jsPage.getByRole("button", { name: "Cookies ablehnen" }).click();

    await expect(
      jsPage.getByText(
        "Ich bin mit der Nutzung von Analyse-Cookies nicht einverstanden.",
      ),
    ).toBeVisible();

    await jsContext.close();
  },
);
