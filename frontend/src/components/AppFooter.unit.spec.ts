import { mount } from "@vue/test-utils";
import { describe, it, expect, vi } from "vitest";
import AppFooter from "~/components/AppFooter.vue";
import { isPrototypeProfile } from "~/utils/profile";

vi.mock("~/utils/profile", () => ({
  isPrototypeProfile: vi.fn<() => boolean>(),
}));

const mockedIsPrototypeProfile = vi.mocked(isPrototypeProfile);

describe("AppFooter", () => {
  it("renders correctly when profile is prototype", () => {
    mockedIsPrototypeProfile.mockReturnValue(true);

    const wrapper = mount(AppFooter);
    expect(wrapper.text()).not.toContain("English translations");
  });

  it("renders correctly when not prototype", () => {
    mockedIsPrototypeProfile.mockReturnValue(false);

    const wrapper = mount(AppFooter);
    expect(wrapper.text()).not.toContain("English translations");
  });
});
