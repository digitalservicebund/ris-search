import { readFile } from "node:fs/promises";
import { join } from "node:path";
import { expect } from "@playwright/test";
import { test } from "./utils/fixtures";

const ROOT = process.cwd();
const PUBLIC = join(ROOT, "src/public");

async function readPublicFile(name: string) {
  return readFile(join(PUBLIC, name), "utf-8");
}

test("public: serves robots.staging.txt for Mozilla/5.0 (Macintosh) user agent when private features are enabled", async ({
  page,
  privateFeaturesEnabled,
}) => {
  test.skip(!privateFeaturesEnabled);
  const expected = await readPublicFile("robots.staging.txt");
  const response = await page.request.get("/robots.txt", {
    headers: { "User-Agent": "Mozilla/5.0 (Macintosh)" },
  });
  expect(await response.text()).toBe(expected);
});

test("public: serves robots.public.txt for Mozilla/5.0 (Macintosh) user agent when private features are disabled", async ({
  page,
  privateFeaturesEnabled,
}) => {
  test.skip(privateFeaturesEnabled);
  const expected = await readPublicFile("robots.public.txt");
  const response = await page.request.get("/robots.txt", {
    headers: { "User-Agent": "Mozilla/5.0 (Macintosh)" },
  });
  expect(await response.text()).toBe(expected);
});

test("public: serves ecli robots.txt for DG_JUSTICE_CRAWLER user agent", async ({
  page,
  privateFeaturesEnabled,
}) => {
  //currently only exists in prototype
  test.skip(privateFeaturesEnabled);
  const response = await page.request.get("/robots.txt", {
    headers: { "User-Agent": "DG_JUSTICE_CRAWLER" },
  });

  const expected =
    "User-agent: *\nDisallow: /\nUser-agent: DG_JUSTICE_CRAWLER\n";
  expect(await response.text()).toBe(expected);
});
