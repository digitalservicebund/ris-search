import { readFile } from "node:fs/promises";
import { join } from "node:path";
import { expect } from "@playwright/test";
import { test } from "./utils/fixtures";

const ROOT = process.cwd();
const PUBLIC = join(ROOT, "src/public");

async function readPublicFile(name: string) {
  return readFile(join(PUBLIC, name), "utf-8");
}

test.describe("robots.public.txt dynamic handler", () => {
  test(`public: serves robots.public.txt for Mozilla/5.0 (Macintosh) user agent`, async ({
    page,
    privateFeaturesEnabled,
  }) => {
    const filename = privateFeaturesEnabled
      ? "robots.staging.txt"
      : "robots.public.txt";
    const expected = await readPublicFile(filename);
    const response = await page.request.get("/robots.txt", {
      headers: { "User-Agent": "Mozilla/5.0 (Macintosh)" },
    });
    expect(await response.text()).toBe(expected);
  });

  test(`public: serves ecli robots.txt for DG_JUSTICE_CRAWLER user agent`, async ({
    page,
    privateFeaturesEnabled,
  }) => {
    //currently only exists in prototype
    if (!privateFeaturesEnabled) {
      const response = await page.request.get("/robots.txt", {
        headers: { "User-Agent": "DG_JUSTICE_CRAWLER" },
      });

      const expected =
        "User-agent: *\n" +
        "Disallow: /\n" +
        "User-agent: DG_JUSTICE_CRAWLER\n";
      expect(await response.text()).toBe(expected);
    }
  });
});
