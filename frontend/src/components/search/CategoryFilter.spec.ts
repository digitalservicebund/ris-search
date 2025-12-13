import { renderSuspended } from "@nuxt/test-utils/runtime";
import userEvent from "@testing-library/user-event";
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

  it("strips .all suffix when selecting 'all' subcategory", async () => {
    const user = userEvent.setup();

    const { emitted } = await renderSuspended(CategoryFilter, {
      props: { modelValue: "A" },
    });

    await user.click(screen.getByText("Gerichtsentscheidungen"));
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
});
