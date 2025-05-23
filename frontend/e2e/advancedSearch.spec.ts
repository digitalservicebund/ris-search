import { type Page, test } from "@playwright/test";
import { expect } from "./fixtures";
import { getDisplayedResultCount } from "./utils";

async function navigateToSearch(page: Page) {
  await page.goto("/advanced-search");
}

test("can go to search via URL", async ({ page }) => {
  await navigateToSearch(page);
  await expect(page.getByRole("heading", { name: "Suche" })).toBeVisible();
  await expect(page).toHaveTitle("Suche | Rechtsinformationen des Bundes");
});

test("the document type selection dropdown exists", async ({ page }) => {
  await navigateToSearch(page);

  const combobox = page.getByRole("combobox", { name: "Dokumentart" });
  await expect(combobox).toHaveText("Alle Dokumentarten");

  await combobox.click();
  const expectedOptionLabels = [
    "Alle Dokumentarten",
    "Rechtsprechung",
    "Normen",
  ];
  for (const option of expectedOptionLabels) {
    await expect(page.getByRole("option", { name: option })).toBeVisible();
  }
  await page.getByRole("option", { name: "Normen" }).click();
  await expect(combobox).toHaveText("Normen");
});

async function selectCaseLaw(page: Page) {
  const combobox = page.getByRole("combobox", { name: "Dokumentart" });
  await expect(combobox).toBeEnabled();
  await combobox.click();
  await page.getByRole("option", { name: "Rechtsprechung" }).click();
}

enum BuilderOperator {
  containsExactPhrase = "enthält genau diese Phrase",
  doesNotContainExactPhrase = "enthält nicht genau diese Phrase",
}

enum RowLogicOperator {
  and = "und",
  or = "oder",
}

async function addBuilderParameters(
  page: Page,
  params: {
    rowLogicOperator?: RowLogicOperator;
    leftHandSide: string;
    operator: BuilderOperator | string;
    rightHandSide: string;
  },
) {
  const searchParameterAddButton = page.getByLabel("Suchparameter hinzufügen");
  await expect(searchParameterAddButton).not.toBeDisabled();
  await searchParameterAddButton.click();

  if (params.rowLogicOperator) {
    await page.getByTestId("row-logic-operator").click();
    await page.getByRole("option", { name: params.rowLogicOperator }).click();
  }

  const builderRow = page.getByTestId("builder-row").last();
  await expect(builderRow).toBeVisible();
  const fieldComboBox = builderRow.getByTestId("field");
  await fieldComboBox.click();
  await page
    .getByRole("option", { name: params.leftHandSide, exact: true })
    .click();

  const typeComboBox = builderRow.getByTestId("operator");
  await typeComboBox.click();
  await page.getByRole("option", { name: params.operator }).click();

  await builderRow.getByRole("textbox").fill(params.rightHandSide);
}

async function getResultRows(page: Page) {
  // wait for at least one element to be visible, since locator.all() returns immediately
  await expect(page.getByTestId("searchResult")).not.toHaveCount(0);
  return await page.getByTestId("searchResult").all();
}

test("adding a search criterion for case law", async ({ page }) => {
  const fileNumber = "22 L 5678/90";

  await navigateToSearch(page);
  await selectCaseLaw(page);

  await addBuilderParameters(page, {
    leftHandSide: "Aktenzeichen",
    operator: BuilderOperator.containsExactPhrase,
    rightHandSide: fileNumber,
  });

  await page.getByLabel("Suchen").click();

  const resultRows = await getResultRows(page);
  expect(resultRows.length).toBeGreaterThanOrEqual(1);

  for (const resultRow of resultRows) {
    await expect(resultRow).toContainText(fileNumber);
  }
});

test.describe("additional search criteria", async () => {
  let baselineNumberOfResults = 0;
  test.beforeEach(async ({ page }) => {
    await navigateToSearch(page);
    await selectCaseLaw(page);
    await addBuilderParameters(page, {
      leftHandSide: "Orientierungssatz",
      operator: BuilderOperator.containsExactPhrase,
      rightHandSide: "Satz",
    });

    await page.getByLabel("Suchen").click();
    baselineNumberOfResults = await getDisplayedResultCount(page);
  });

  test("Adding a specific search parameter with AND reduces the result set", async ({
    page,
  }) => {
    await addBuilderParameters(page, {
      leftHandSide: "Gerichtsort",
      operator: BuilderOperator.containsExactPhrase,
      rightHandSide: "Testort6",
      rowLogicOperator: RowLogicOperator.and,
    });

    await page.getByLabel("Suchen").click();
    await expect
      .poll(() => getDisplayedResultCount(page), {
        message: "ensure that the correct results appear eventually",
      })
      .toBeLessThan(baselineNumberOfResults);
  });

  test("Adding a specific search parameter with OR enlarges the result set", async ({
    page,
  }) => {
    await addBuilderParameters(page, {
      leftHandSide: "Gerichtstyp",
      operator: BuilderOperator.containsExactPhrase,
      rightHandSide: "FG",
      rowLogicOperator: RowLogicOperator.or,
    });
    await page.getByLabel("Suchen").click();
    await expect
      .poll(() => getDisplayedResultCount(page), {
        message: "ensure that the correct results appear eventually",
      })
      .toBeGreaterThan(baselineNumberOfResults);
  });
});

test("Querying with conflicting search parameters yields no results", async ({
  page,
}) => {
  await navigateToSearch(page);
  const params = {
    leftHandSide: "Titel",
    operator: BuilderOperator.containsExactPhrase,
    rightHandSide: "Fiktiv",
  };
  await addBuilderParameters(page, params);
  await addBuilderParameters(page, {
    ...params,
    operator: BuilderOperator.doesNotContainExactPhrase,
  });

  await page.getByLabel("Suchen").click();
  await expect(page.getByText("Keine Suchergebnisse gefunden")).toBeVisible();
});

test("Querying for a non-existing attribute yields no results", async ({
  page,
}) => {
  await navigateToSearch(page);
  await selectCaseLaw(page);
  await addBuilderParameters(page, {
    leftHandSide: "Dokumentnummer",
    operator: BuilderOperator.containsExactPhrase,
    rightHandSide: "226-7709",
  });

  await page.getByLabel("Suchen").click();
  await expect(page.getByText("Keine Suchergebnisse gefunden")).toBeVisible();
});

test("Switching back to Lucene query input yields the correct parameters", async ({
  page,
}) => {
  await navigateToSearch(page);
  await addBuilderParameters(page, {
    leftHandSide: "Datum",
    operator: "ist am",
    rightHandSide: "31.12.1999",
  });

  await page.getByLabel("Sucheingabemodus").click();
  await page.getByRole("option", { name: FormSearchMode.text }).click();

  await expect(page.getByLabel("Suchanfrage")).toHaveValue("DATUM:1999-12-31");
});

enum FormSearchMode {
  builder = "Baukasten",
  text = "Text",
}
const formSearchModes = [FormSearchMode.builder, FormSearchMode.text];

for (const mode of formSearchModes) {
  test(`resetting the query works in mode "${mode}"`, async ({ page }) => {
    test.skip(mode === FormSearchMode.text, "not yet implemented");
    await navigateToSearch(page);
    await page.getByLabel("Sucheingabemodus").click();
    await page.getByRole("option", { name: mode }).click();

    if (mode === FormSearchMode.builder) {
      await addBuilderParameters(page, {
        leftHandSide: "Titel",
        operator: BuilderOperator.doesNotContainExactPhrase,
        rightHandSide: "Beschluss",
      });
    } else {
      await page.getByLabel("Suchanfrage").fill(`-TITEL:"Beschluss"`);
    }
    await page.getByLabel("Suchen").click();
    await page.getByLabel("Zurücksetzen").click();
    await page.getByLabel("Sucheingabemodus").click();
    await page.getByRole("option", { name: FormSearchMode.text }).click();
    await page.getByLabel("Sucheingabemodus").click();
    await page.getByRole("option", { name: FormSearchMode.builder }).click();
    await addBuilderParameters(page, {
      leftHandSide: "Titel",
      operator: BuilderOperator.doesNotContainExactPhrase,
      rightHandSide: "Beschluss",
    });
  });
}
