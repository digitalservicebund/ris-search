import { render, screen } from "@testing-library/vue";
import { describe, it, expect, vi } from "vitest";
import AppFooter from "~/components/AppFooter.vue";
import { isPrototypeProfile } from "~/utils/profile";

vi.mock("~/utils/profile", () => ({
  isPrototypeProfile: vi.fn<() => boolean>(),
}));

const mockedIsPrototypeProfile = vi.mocked(isPrototypeProfile);

describe("AppFooter", () => {
  const globalStubs = {
    NuxtLink: {
      props: ["to"],
      template: `<a :href="to"><slot /></a>`,
    },
  };

  it("does not render 'English translations' when profile is prototype", () => {
    mockedIsPrototypeProfile.mockReturnValue(true);
    render(AppFooter, { global: { stubs: globalStubs } });
    expect(
      screen.queryByRole("link", { name: "English translations" }),
    ).toBeNull();
  });

  it("renders 'English translations' when profile is not prototype", () => {
    mockedIsPrototypeProfile.mockReturnValue(false);
    render(AppFooter, { global: { stubs: globalStubs } });
    const link = screen.getByRole("link", { name: "English translations" });
    expect(link).toBeVisible();
    expect(link).toHaveAttribute("href", "/translations");
  });
});
