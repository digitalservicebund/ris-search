import { renderSuspended } from "@nuxt/test-utils/runtime";
import userEvent from "@testing-library/user-event";
import { screen, waitFor } from "@testing-library/vue";
import { vi } from "vitest";
import SimpleSearch from "./index.vue";

vi.mock("~/services/searchService", () => {
  return { search: () => ({}) };
});

describe("SimpleSearch", () => {
  it("sets the title attribute", async () => {
    const user = userEvent.setup();

    await renderSuspended(SimpleSearch);
    await nextTick();

    await waitFor(() => {
      expect(document.title).toBe("Suche");
    });

    await user.click(
      screen.getByRole("button", { name: "Gerichtsentscheidungen" }),
    );

    expect(document.title).toBe("Rechtsprechung — Suche");

    await user.type(
      screen.getByRole("searchbox", { name: "Suchbegriff" }),
      "frühstück brötchen",
    );
    await user.click(screen.getByRole("button", { name: "Suchen" }));
    await nextTick();

    expect(document.title).toBe("frühstück brötchen — Suche");
  });

  for (const testCase of [
    { category: "Alle Dokumentarten", filterShouldBeVisible: false },
    { category: "Gesetze & Verordnungen", filterShouldBeVisible: false },
    { category: "Gerichtsentscheidungen", filterShouldBeVisible: true },
    { category: "Literaturnachweise", filterShouldBeVisible: true },
  ]) {
    it(`sets the visibility of the duration filter to ${testCase.filterShouldBeVisible} when the category is ${testCase.category}`, async () => {
      const user = userEvent.setup();

      await renderSuspended(SimpleSearch);
      await user.click(screen.getByRole("button", { name: testCase.category }));

      const dateRangeSelect = screen.queryByRole("combobox", {
        name: "Keine zeitliche Begrenzung",
      });

      if (testCase.filterShouldBeVisible) {
        expect(dateRangeSelect).toBeInTheDocument();
      } else {
        expect(dateRangeSelect).not.toBeInTheDocument();
      }
    });
  }
});
