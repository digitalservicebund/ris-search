import { render, screen } from "@testing-library/vue";
import { describe, it, expect, vi } from "vitest";
import AppFooter from "~/components/AppFooter.vue";
import { privateFeaturesEnabled } from "~/utils/featureFlags";

vi.mock("~/utils/profile", () => ({
  privateFeaturesEnabled: vi.fn<() => boolean>(),
}));

const mockedPrivateFeaturesEnabled = vi.mocked(privateFeaturesEnabled);

describe("AppFooter", () => {
  const globalStubs = {
    NuxtLink: {
      props: ["to"],
      template: `<a :href="to"><slot /></a>`,
    },
  };

  it("does not render 'English translations' when profile is prototype", () => {
    mockedPrivateFeaturesEnabled.mockReturnValue(false);
    render(AppFooter, { global: { stubs: globalStubs } });
    expect(
      screen.queryByRole("link", { name: "English translations" }),
    ).toBeNull();
  });

  it("renders 'English translations' when profile is not prototype", () => {
    mockedPrivateFeaturesEnabled.mockReturnValue(true);
    render(AppFooter, { global: { stubs: globalStubs } });
    const link = screen.getByRole("link", { name: "English translations" });
    expect(link).toBeVisible();
    expect(link).toHaveAttribute("href", "/translations");
  });
});
