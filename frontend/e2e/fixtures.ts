import { test as base, expect as baseExpect } from "@playwright/test";
import type { Locator } from "@playwright/test";

type WorkerFixtures = {
  isMobileTest: boolean;
};

/* typescript-eslint-disable @typescript-eslint/no-empty-object-type */
export const test = base.extend<{}, WorkerFixtures>({
  isMobileTest: [
    async ({}, use, workerInfo) => {
      const isMobileTest = workerInfo.project.name.toLowerCase() === "mobile";
      await use(isMobileTest);
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
