import { render, screen } from "@testing-library/vue";
import { describe, it, expect, vi } from "vitest";
import AppFooter from "~/components/AppFooter.vue";
import { usePrivateFeaturesFlag } from "~/composables/usePrivateFeaturesFlag";

vi.mock("~/composables/usePrivateFeaturesFlag", () => ({
  usePrivateFeaturesFlag: vi.fn<() => boolean>(),
}));

const mockedPrivateFeaturesEnabled = vi.mocked(usePrivateFeaturesFlag);

describe("AppFooter", () => {
  const globalStubs = {
    NuxtLink: {
      props: ["to"],
      template: `<a :href="to"><slot /></a>`,
    },
  };

  it("does not render 'English translations' when privateFeatureEnabled is false", () => {
    mockedPrivateFeaturesEnabled.mockReturnValue(false);
    render(AppFooter, { global: { stubs: globalStubs } });
    expect(
      screen.queryByRole("link", { name: "English translations" }),
    ).toBeNull();
  });

  it("renders 'English translations' when privateFeatureEnabled is true", () => {
    mockedPrivateFeaturesEnabled.mockReturnValue(true);
    render(AppFooter, { global: { stubs: globalStubs } });
    const link = screen.getByRole("link", { name: "English translations" });
    expect(link).toBeVisible();
  });
});
