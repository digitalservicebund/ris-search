import type { Locator } from "@playwright/test";
import { expect as baseExpect } from "@playwright/test";

export { test } from "@playwright/test";

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
