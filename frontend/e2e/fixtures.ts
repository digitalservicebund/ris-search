import os from "os";
import path from "path";
import { test as base, expect as baseExpect, chromium } from "@playwright/test";
import type { Locator, BrowserContext } from "@playwright/test";
import { loginUser } from "./auth.utils";
import { environment } from "~~/playwright.config";

type WorkerFixtures = {
  isMobileTest: boolean;
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
});

type SeoWorkerFixtures = {
  persistentContext: BrowserContext;
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

      const bootstrapPage = await context.newPage();
      await bootstrapPage.goto(environment.baseUrl);
      await loginUser(bootstrapPage);
      await bootstrapPage.close();

      await use(context);
      await context.close();
    },
    { scope: "worker" },
  ],

  page: async ({ persistentContext }, use) => {
    const page = await persistentContext.newPage();
    await use(page);
    await page.close();
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
          select.options[select.options.selectedIndex].textContent,
        options,
      );
      await baseExpect(matcherResult).toBe(expected);
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
