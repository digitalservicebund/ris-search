import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { render, screen } from "@testing-library/vue";
import { describe, it, expect, vi } from "vitest";
import AppFooter from "~/components/app/AppFooter.vue";
import { usePrivateFeaturesFlag } from "~/composables/usePrivateFeaturesFlag";

vi.mock("~/composables/usePrivateFeaturesFlag", () => ({
  usePrivateFeaturesFlag: vi.fn<() => boolean>(),
}));

const mockedPrivateFeaturesEnabled = vi.mocked(usePrivateFeaturesFlag);

const { useRouteMock } = vi.hoisted(() => ({
  useRouteMock: vi.fn(() => ({ path: "/" })),
}));

mockNuxtImport("useRoute", () => useRouteMock);

describe("AppFooter", () => {
  const globalStubs = {
    NuxtLink: {
      props: ["to"],
      template: `<a :href="to"><slot /></a>`,
    },
    AnalyticsFeedbackForm: { template: `<div />` },
  };

  it("renders the footer navigation", () => {
    mockedPrivateFeaturesEnabled.mockReturnValue(false);
    render(AppFooter, { global: { stubs: globalStubs } });
    expect(
      screen.getByRole("navigation", { name: "Weitere Informationen" }),
    ).toBeVisible();
  });

  it("renders the feedback form section", () => {
    useRouteMock.mockReturnValue({ path: "/" });
    mockedPrivateFeaturesEnabled.mockReturnValue(false);
    render(AppFooter, { global: { stubs: globalStubs } });
    expect(
      screen.getByRole("region", { name: "Geben Sie uns Feedback" }),
    ).toBeInTheDocument();
  });

  it("does not render the feedback form on the feedback page", () => {
    useRouteMock.mockReturnValue({ path: "/feedback" });
    mockedPrivateFeaturesEnabled.mockReturnValue(false);
    render(AppFooter, { global: { stubs: globalStubs } });
    expect(
      screen.queryByRole("region", { name: "Geben Sie uns Feedback" }),
    ).not.toBeInTheDocument();
  });

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
