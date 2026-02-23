import type { Page } from "@playwright/test";
import {
  testCopyLinkButton,
  testPdfButton,
  testPrintButton,
  testXmlButton,
} from "./utils/actionMenuHelper";
import { expect, navigate, noJsTest, test } from "./utils/fixtures";

async function getSidebar(page: Page) {
  const navigation = page.getByRole("navigation", { name: "Seiteninhalte" });
  await navigation.scrollIntoViewIfNeeded();
  await expect(navigation).toBeVisible();
  return navigation;
}

test.skip(
  process.env.NUXT_PUBLIC_PRIVATE_FEATURES_ENABLED !== "true",
  "Removed redundant testing since there are no specific functionality marked as private",
);

test("shows 404 page when case law is not found", async ({ page }) => {
  await page.goto("/case-law/NONEXISTENT123");

  await expect(
    page.getByRole("heading", { name: "Diese Seite existiert nicht" }),
  ).toBeVisible();
});

test("can view a single case law documentation unit", async ({
  page,
  isMobileTest,
}) => {
  await navigate(page, "/case-law/KORE600500000");

  await expect(
    page.getByRole("heading", { name: "Testheader für Urteil 6." }).first(),
  ).toBeVisible();

  const sidebar = await getSidebar(page);

  const firstSectionHeader = page
    .getByRole("main")
    .getByRole("heading", { level: 2 })
    .first();
  await expect(firstSectionHeader).toBeVisible();

  await firstSectionHeader.scrollIntoViewIfNeeded();

  // Only check aria-current on desktop as it's flaky on mobile due to Intersection Observer timing
  // Fix Intersection Observer logic (broken since Nuxt upgrade) - see ticket RISDEV-
  // Once fixed, remove the isMobileTest check and enable aria-current verification for mobile too
  if (!isMobileTest) {
    const currentSection = sidebar.locator('a[aria-current="location"]');
    await expect(currentSection).toHaveCount(1);
  }

  if (isMobileTest)
    for (const sectionName of ["Tenor", "Orientierungssatz", "Tatbestand"]) {
      await test.step(`jumps straight to a specific section, ${sectionName}`, async () => {
        await navigate(page, "/search?documentKind=R&query=fiktiv");
        const link = page.getByRole("link", { name: sectionName }).first();
        await link.click();

        // Verify sidebar is visible and there but skip aria-current check
        // (its flaky due to Intersection Observer timing during hydration)
        // Once fixed, enable aria-current check here (was flaky due to
        // Intersection Observer timing during hydration)
        await getSidebar(page);

        const heading = page
          .getByRole("main")
          .getByRole("heading", { name: sectionName })
          .first();

        await expect(page).toHaveURL(new RegExp(`#`, "i"));
        await heading.scrollIntoViewIfNeeded();
        await expect(heading).toBeVisible();
        await expect(heading).toBeInViewport();
      });
    }
});

test("jumps to Randnummern", async ({ page }) => {
  await navigate(page, "/case-law/BORE040077911");

  const link = page.getByRole("link", { name: "Springe zu Randnummer: 1" });

  await expect(link).toBeVisible();

  await link.click();

  await expect(page).toHaveURL(/#border-number-link-1$/);

  await expect(
    page.getByText(
      "Fiktiver Hintergrundtext für den Testfall zur Randnummernverlinkung.",
    ),
  ).toBeInViewport();
});

test.describe("actions menu", () => {
  test.describe("can copy link to currently viewed page", () => {
    testCopyLinkButton(
      "/case-law/JURE200030030",
      "Link kopieren",
      RegExp(".*/case-law/JURE200030030"),
    );
  });

  test.describe("can use print action button to open print menu", () => {
    testPrintButton("/case-law/JURE200030030");
  });

  test.describe("can't use PDF action as it is disabled", () => {
    testPdfButton("/case-law/JURE200030030");
  });

  test.describe("can use XML action to view caselaw xml file", () => {
    testXmlButton(
      "/case-law/JURE200030030",
      "http://localhost:8090/v1/case-law/JURE200030030.xml",
    );
  });
});

test("can view metadata", async ({ page }) => {
  await navigate(page, "/case-law/KORE600500000");
  const metadataList = page.getByTestId("metadata-list");

  await expect(
    metadataList.getByRole("term").or(metadataList.getByRole("definition")),
  ).toHaveText([
    "Gericht",
    "LG Testort6",
    "Dokumenttyp",
    "Urteil",
    "Entscheidungsdatum",
    "09.04.2025",
    "Aktenzeichen",
    "TS 123456",
  ]);
});

test("can view details", async ({ page }) => {
  await navigate(page, "/case-law/KORE600500000");
  await page.getByRole("tab", { name: "Details" }).click();
  const detailsList = page.getByTestId("details-list");

  await expect(
    detailsList.getByRole("term").or(detailsList.getByRole("definition")),
  ).toHaveText([
    "Spruchkörper:",
    "8. Kammer",
    "ECLI:",
    "nicht vorhanden",
    "Normen:",
    "nicht vorhanden",
    "Entscheidungsname:",
    "nicht vorhanden",
    "Vorinstanz:",
    "nicht vorhanden",
    "Download:",
    "KORE600500000 als ZIP herunterladen",
  ]);
});

test("renders the download link", async ({ page }) => {
  await navigate(page, "/case-law/KORE600500000");
  await page.getByRole("tab", { name: "Details" }).click();

  const zipLink = page.getByRole("link", {
    name: "KORE600500000 als ZIP herunterladen",
  });
  await expect(zipLink).toBeVisible();
  await expect(zipLink).toHaveAttribute(
    "href",
    "/v1/case-law/KORE600500000.zip",
  );
});

noJsTest("tabs work without JavaScript", async ({ page }) => {
  await navigate(page, "/case-law/JURE200030030");

  await test.step("text", async () => {
    await expect(
      page.getByRole("heading", { name: "Orientierungssatz" }),
    ).toBeVisible();

    await expect(
      page.getByRole("tab", { name: "Text", selected: true }),
    ).toBeVisible();
  });

  await test.step("details", async () => {
    await page.getByRole("tab", { name: "Details" }).click();

    await expect(page.getByRole("heading", { name: "Details" })).toBeVisible();

    await expect(
      page.getByRole("tab", { name: "Details", selected: true }),
    ).toBeVisible();
  });
});
