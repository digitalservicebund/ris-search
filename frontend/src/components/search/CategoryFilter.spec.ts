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

  it("emits update when category is selected", async () => {
    const user = userEvent.setup();

    const { emitted } = await renderSuspended(CategoryFilter, {
      props: { modelValue: "A" },
    });

    await user.click(screen.getByText("Gesetze & Verordnungen"));

    expect(emitted("update:modelValue")).toContainEqual(["N"]);
  });

  it("clears the subcategory when selecting 'all' subcategory", async () => {
    const user = userEvent.setup();

    const { emitted } = await renderSuspended(CategoryFilter, {
      props: { modelValue: "R.urteil" },
    });

    await user.click(screen.getByText("Alle Gerichtsentscheidungen"));

    expect(emitted("update:modelValue")).toContainEqual(["R"]);
  });

  it("sets specific subcategory when selected", async () => {
    const user = userEvent.setup();

    const { emitted } = await renderSuspended(CategoryFilter, {
      props: { modelValue: "A" },
    });

    await user.click(screen.getByText("Gerichtsentscheidungen"));
    await user.click(screen.getByText("Urteil"));

    expect(emitted("update:modelValue")).toContainEqual(["R.urteil"]);
  });

  it("selects all case law when the parent category is selected", async () => {
    const user = userEvent.setup();

    const { emitted } = await renderSuspended(CategoryFilter, {
      props: { modelValue: "A" },
    });

    await user.click(screen.getByText("Gerichtsentscheidungen"));

    expect(emitted("update:modelValue")).toContainEqual(["R"]);
  });

  it("hides the subcategories while another category is selected", async () => {
    await renderSuspended(CategoryFilter, { props: { modelValue: "N" } });

    expect(screen.queryByText("Urteil")).not.toBeInTheDocument();
  });

  it("shows the subcategories while case law is selected", async () => {
    await renderSuspended(CategoryFilter, { props: { modelValue: "R" } });

    expect(
      screen.getByRole("radio", { name: "Gerichtsentscheidungen" }),
    ).toBeChecked();
    expect(screen.getByRole("radio", { name: "Urteil" })).toBeInTheDocument();
  });
});
