import { renderSuspended } from "@nuxt/test-utils/runtime";
import { userEvent } from "@testing-library/user-event";
import { screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import CategoryFilter from "./CategoryFilter.vue";

describe("CategoryFilter", () => {
  it("renders filter menu with all categories", async () => {
    await renderSuspended(CategoryFilter, {
      props: { modelValue: "A" },
    });

    expect(screen.getByText("Dokumentarten")).toBeInTheDocument();
    expect(screen.getByText("Alle Dokumentarten")).toBeInTheDocument();
    expect(screen.getByText("Gesetze & Verordnungen")).toBeInTheDocument();
    expect(screen.getByText("Gerichtsentscheidungen")).toBeInTheDocument();
    expect(screen.getByText("Literaturnachweise")).toBeInTheDocument();
  });

  it.each([
    {
      selects: "a top-level category",
      modelValue: "A",
      rows: ["Gesetze & Verordnungen"],
      expected: "N",
    },
    {
      selects: "a category with subcategories",
      modelValue: "A",
      rows: ["Gerichtsentscheidungen"],
      expected: "R",
    },
    {
      selects: "the 'all' subcategory",
      modelValue: "R.urteil",
      rows: ["Alle Gerichtsentscheidungen"],
      expected: "R",
    },
    {
      selects: "a subcategory after opening its branch",
      modelValue: "A",
      rows: ["Gerichtsentscheidungen", "Urteil"],
      expected: "R.urteil",
    },
  ])(
    "emits $expected when selecting $selects",
    async ({ modelValue, rows, expected }) => {
      const user = userEvent.setup();

      const { emitted } = await renderSuspended(CategoryFilter, {
        props: { modelValue },
      });

      for (const row of rows) {
        await user.click(screen.getByText(row));
      }

      expect(emitted("update:modelValue")).toContainEqual([expected]);
    },
  );

  it("hides the subcategories while another category is selected", async () => {
    await renderSuspended(CategoryFilter, { props: { modelValue: "N" } });

    expect(screen.queryByText("Urteil")).not.toBeInTheDocument();
  });

  it("shows the subcategories while case law is selected", async () => {
    await renderSuspended(CategoryFilter, { props: { modelValue: "R" } });

    expect(
      screen.getByRole("radio", { name: "Alle Gerichtsentscheidungen" }),
    ).toBeChecked();
    expect(screen.getByRole("radio", { name: "Urteil" })).toBeInTheDocument();
  });
});
