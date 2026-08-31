import { renderSuspended } from "@nuxt/test-utils/runtime";
import { userEvent } from "@testing-library/user-event";
import { screen } from "@testing-library/vue";
import { describe, it, expect, vi } from "vitest";
import AppMainMenu from "./MainMenu.vue";

const mockPrivateFeaturesEnabled = vi.fn(() => false);
vi.mock("~/composables/usePrivateFeaturesFlag", () => ({
  usePrivateFeaturesFlag: () => mockPrivateFeaturesEnabled(),
}));

describe("MainMenu", () => {
  it('emits "selectItem" for each NuxtLink that has been clicked', async () => {
    const user = userEvent.setup();
    const { emitted } = await renderSuspended(AppMainMenu, {
      props: {
        listClass: "test-class",
      },
    });

    const searchLinks = screen.getAllByRole("link");
    for (const link of searchLinks) {
      await user.click(link);
    }
    expect(emitted("selectItem")).toBeTruthy();
    expect(emitted("selectItem")?.length).toBe(searchLinks.length);
  });

  it("displays links 'Suche', 'Erweitere Suche' and 'Über diesen Service' when private features enabled", async () => {
    mockPrivateFeaturesEnabled.mockReturnValue(true);
    await renderSuspended(AppMainMenu, {
      props: {
        listClass: "test-class",
      },
    });

    const searchLink = screen.getByRole("link", { name: "Suche" });
    expect(searchLink).toBeVisible();
    expect(searchLink).toHaveAttribute("href", "/suche");

    const advancedSearchLink = screen.getByRole("link", {
      name: "Erweiterte Suche",
    });
    expect(advancedSearchLink).toBeVisible();
    expect(advancedSearchLink).toHaveAttribute("href", "/erweiterte-suche");

    const aboutLink = screen.getByRole("link", { name: "Über den Service" });
    expect(aboutLink).toBeVisible();
    expect(aboutLink).toHaveAttribute("href", "/ueber");
  });

  it("displays links 'Suche', 'Feedback geben' and 'Über diesen Service' when private features disabled", async () => {
    mockPrivateFeaturesEnabled.mockReturnValue(false);
    await renderSuspended(AppMainMenu, {
      props: {
        listClass: "test-class",
      },
    });

    const searchLink = screen.getByRole("link", { name: "Suche" });
    expect(searchLink).toBeVisible();
    expect(searchLink).toHaveAttribute("href", "/suche");

    const advancedSearchLink = screen.getByRole("link", {
      name: "Feedback geben",
    });
    expect(advancedSearchLink).toBeVisible();
    expect(advancedSearchLink).toHaveAttribute("href", "/feedback");

    const aboutLink = screen.getByRole("link", { name: "Über den Service" });
    expect(aboutLink).toBeVisible();
    expect(aboutLink).toHaveAttribute("href", "/ueber");
  });
});
