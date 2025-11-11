import os from "node:os";
import path from "node:path";
import type { BrowserContext, Locator } from "@playwright/test";
import { chromium, expect as baseExpect, test as base } from "@playwright/test";
import { environment } from "../../playwright.config";

type WorkerFixtures = {
  isMobileTest: boolean;
  privateFeaturesEnabled: boolean;
};

// eslint-disable-next-line @typescript-eslint/no-empty-object-type
export const test = base.extend<{}, WorkerFixtures>({
  isMobileTest: [
    // eslint-disable-next-line no-empty-pattern
    async ({}, use, workerInfo) => {
      const isMobileTest = workerInfo.project.name.toLowerCase() === "mobile";
      await use(isMobileTest);
    },
    { scope: "worker" },
  ],
  privateFeaturesEnabled: [
    // eslint-disable-next-line no-empty-pattern
    async ({}, use) => {
      const privateFeaturesEnabled =
        process.env.NUXT_PUBLIC_PRIVATE_FEATURES_ENABLED === "true";
      await use(privateFeaturesEnabled);
    },
    { scope: "worker" },
  ],
});

type SeoWorkerFixtures = {
  persistentContext: BrowserContext;
  privateFeaturesEnabled: boolean;
};

// eslint-disable-next-line @typescript-eslint/no-empty-object-type
export const seoTest = base.extend<{}, SeoWorkerFixtures>({
  persistentContext: [
    // eslint-disable-next-line no-empty-pattern
    async ({}, use) => {
      const userDataDir = path.join(os.tmpdir(), "playwright-seo-userdata");

      const context = await chromium.launchPersistentContext(userDataDir, {
        headless: true,
        args: [`--remote-debugging-port=${environment.remoteDebuggingPort}`],
      });

      await use(context);
      await context.close();
    },
    { scope: "worker" },
  ],
  privateFeaturesEnabled: [
    // eslint-disable-next-line no-empty-pattern
    async ({}, use) => {
      const privateFeaturesEnabled =
        process.env.NUXT_PUBLIC_PRIVATE_FEATURES_ENABLED === "true";
      await use(privateFeaturesEnabled);
    },
    { scope: "worker" },
  ],
  page: async ({ persistentContext }, use) => {
    const page = await persistentContext.newPage();
    await use(page);
  },
});

export const expect = baseExpect.extend({
  async toHaveSelectedOptionText(
    locator: Locator,
    expected: string,
    options?: { timeout?: number },
  ) {
    const assertionName = "toHaveSelectedOptionText";
    let pass: boolean;
    let matcherResult: string | null = null;
    try {
      const matcherResult = await locator.evaluate(
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
  page: import("@playwright/test").Page;
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
  },
});
