import type { BrowserContext, Locator, Page } from "@playwright/test";
import { expect as baseExpect, test as base } from "@playwright/test";

type WorkerFixtures = {
  isMobileTest: boolean;
  privateFeaturesEnabled: boolean;
};

export const test = base.extend<{}, WorkerFixtures>({
  isMobileTest: [
    // oxlint-disable-next-line no-empty-pattern -- Playwright fixture syntax requires this
    async ({}, use, workerInfo) => {
      const isMobileTest = workerInfo.project.name.toLowerCase() === "mobile";
      await use(isMobileTest);
    },
    { scope: "worker" },
  ],
  privateFeaturesEnabled: [
    // oxlint-disable-next-line no-empty-pattern -- Playwright fixture syntax requires this
    async ({}, use) => {
      const privateFeaturesEnabled =
        process.env.NUXT_PUBLIC_PRIVATE_FEATURES_ENABLED === "true";
      await use(privateFeaturesEnabled);
    },
    { scope: "worker" },
  ],
});

export const expect = baseExpect.extend({
  async toHaveSelectedOptionText(
    locator: Locator,
    expected: string,
    options?: { timeout?: number },
  ) {
    const assertionName = "toHaveSelectedOptionText";
    let pass: boolean;
    let matcherResult: string | undefined;
    try {
      matcherResult = await locator.evaluate(
        (select: HTMLSelectElement) =>
          select.options[select.options.selectedIndex]?.textContent,
        options,
      );
      baseExpect(matcherResult).toBe(expected);
      pass = true;
    } catch (e: unknown) {
      if ((e as { matcherResult: string }).matcherResult) {
        matcherResult = (e as { matcherResult: string }).matcherResult;
      }
      pass = false;
    }

    const message = pass
      ? () =>
          this.utils.matcherHint(assertionName, undefined, undefined, {
            isNot: this.isNot,
          }) +
          "\n\n" +
          `Locator: ${locator}\n` +
          `Expected: ${this.isNot ? "not" : ""}${this.utils.printExpected(expected)}\n` +
          (matcherResult
            ? `Received: ${this.utils.printReceived(matcherResult)}`
            : "")
      : () =>
          this.utils.matcherHint(assertionName, undefined, undefined, {
            isNot: this.isNot,
          }) +
          "\n\n" +
          `Locator: ${locator}\n` +
          `Expected: ${this.utils.printExpected(expected)}\n` +
          (matcherResult
            ? `Received: ${this.utils.printReceived(matcherResult)}`
            : "");

    return {
      message,
      pass,
      name: assertionName,
      expected,
      actual: matcherResult,
    };
  },
});

type NoJsFixtures = {
  noJsContext: BrowserContext;
  page: Page;
};

export const noJsTest = base.extend<NoJsFixtures>({
  noJsContext: async ({ browser }, use) => {
    const context = await browser.newContext({ javaScriptEnabled: false });
    await use(context);
    await context.close();
  },

  page: async ({ noJsContext }, use) => {
    const page = await noJsContext.newPage();
    await use(page);
    await page.close();
  },
});

export async function navigate(page: Page, url: string) {
  await page.goto(url, { waitUntil: "networkidle" });
}
