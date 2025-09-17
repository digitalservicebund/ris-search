import { readFile } from "node:fs/promises";
import { join } from "node:path";
import { expect, test } from "@playwright/test";

const ROOT = process.cwd();
const PUBLIC = join(ROOT, "src/public");

async function readPublicFile(name: string) {
  return readFile(join(PUBLIC, name), "utf-8");
}

const file =
  process.env.NUXT_PUBLIC_PROFILE === "internal"
    ? "robots.staging.txt"
    : "robots.public.txt";
const testCases = [
  { userAgent: "Mozilla/5.0 (Macintosh)", file: file },
  { userAgent: "DG_JUSTICE_CRAWLER", file: "robots.dg.txt" },
];

test.describe("robots.public.txt dynamic handler", () => {
  testCases.forEach((testCase) => {
    test(`public: serves ${testCase.file} for ${testCase.userAgent} user agent`, async ({
      page,
    }) => {
      const expected = await readPublicFile(testCase.file);
      const response = await page.request.get("/robots.txt", {
        headers: { "User-Agent": testCase.userAgent },
      });
      expect(await response.text()).toBe(expected);
    });
  });
});
