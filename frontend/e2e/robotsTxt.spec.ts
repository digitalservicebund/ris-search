import { readFile } from "node:fs/promises";
import { join } from "node:path";
import { expect } from "@playwright/test";
import { test } from "./utils/fixtures";

const ROOT = process.cwd();
const PUBLIC = join(ROOT, "src/public");

async function readPublicFile(name: string) {
  return readFile(join(PUBLIC, name), "utf-8");
}

test.describe("robots.txt dynamic handler", () => {
  test(`public: serves robots.txt for Mozilla/5.0 (Macintosh) user agent`, async ({
    page,
  }) => {
    const filename = "robots.txt";
    const expected = await readPublicFile(filename);
    const response = await page.request.get("/robots.txt", {
      headers: { "User-Agent": "Mozilla/5.0 (Macintosh)" },
    });
    expect(await response.text()).toBe(expected);
  });
});
