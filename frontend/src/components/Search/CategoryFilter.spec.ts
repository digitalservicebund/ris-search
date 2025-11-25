import { renderSuspended } from "@nuxt/test-utils/runtime";
import userEvent from "@testing-library/user-event";
import { screen } from "@testing-library/vue";
import { describe, it, expect, beforeEach } from "vitest";
import CategoryFilter from "./CategoryFilter.vue";
import { useSimpleSearchParamsStore } from "~/stores/searchParams";

describe("CategoryFilter", () => {
  beforeEach(() => {
    const store = useSimpleSearchParamsStore();
    store.$reset();
  });

  it("renders filter menu with all categories", async () => {
    await renderSuspended(CategoryFilter);

    expect(screen.getByText("Dokumentarten")).toBeInTheDocument();
    expect(screen.getByText("Alle Dokumentarten")).toBeInTheDocument();
    expect(screen.getByText("Gesetze & Verordnungen")).toBeInTheDocument();
    expect(screen.getByText("Gerichtsentscheidungen")).toBeInTheDocument();
    expect(screen.getByText("Literaturnachweise")).toBeInTheDocument();
  });

  it("updates store when category is selected", async () => {
    const user = userEvent.setup();
    await renderSuspended(CategoryFilter);
    const store = useSimpleSearchParamsStore();

    await user.click(screen.getByText("Gesetze & Verordnungen"));

    expect(store.category).toBe("N");
  });

  it("strips .all suffix when selecting 'all' subcategory", async () => {
    const user = userEvent.setup();
    await renderSuspended(CategoryFilter);
    const store = useSimpleSearchParamsStore();

    await user.click(screen.getByText("Gerichtsentscheidungen"));
    await user.click(screen.getByText("Alle Gerichtsentscheidungen"));

    expect(store.category).toBe("R");
  });

  it("sets specific subcategory when selected", async () => {
    const user = userEvent.setup();
    await renderSuspended(CategoryFilter);
    const store = useSimpleSearchParamsStore();

    await user.click(screen.getByText("Gerichtsentscheidungen"));
    await user.click(screen.getByText("Urteil"));

    expect(store.category).toBe("R.urteil");
  });
});
