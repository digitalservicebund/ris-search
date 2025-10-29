import { mount } from "@vue/test-utils";
import { describe, it, expect, vi } from "vitest";
import AppFooter from "~/components/AppFooter.vue";
import { isPrototypeProfile } from "~/utils/profile";
vi.mock("~/utils/profile", () => ({
  isPrototypeProfile: vi.fn(),
}));

describe("AppFooter", () => {
  it("renders correctly when profile is prototype", () => {
    isPrototypeProfile.mockReturnValue(true);

    const wrapper = mount(AppFooter);
    expect(wrapper.text()).not.toContain("English translations");
  });

  it("renders correctly when not prototype", () => {
    isPrototypeProfile.mockReturnValue(false);

    const wrapper = mount(AppFooter);
    expect(wrapper.text()).not.toContain("English translations");
  });
});
