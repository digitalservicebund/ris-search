import { mount } from "@vue/test-utils";
import { describe, it, expect, vi } from "vitest";
import BackToTopLink from "./BackToTopLink.vue";

describe("BackToTopLink.vue", () => {
  // Setup mock for scrollIntoView
  const mockScrollIntoView = vi.fn();

  beforeEach(() => {
    // Reset mock before each test
    mockScrollIntoView.mockReset();

    // Mock getElementById and scrollIntoView
    document.getElementById = vi.fn().mockImplementation((id) => {
      if (id === "top") {
        return {
          scrollIntoView: mockScrollIntoView,
        };
      }
      return null;
    });
  });

  it("renders the link with correct attributes", () => {
    const wrapper = mount(BackToTopLink);
    const link = wrapper.find("a");

    expect(link.exists()).toBe(true);
    expect(link.attributes("href")).toBe("#top");
    expect(link.attributes("aria-label")).toBe("zum Seitenanfang");
  });

  it("calls scrollIntoView with smooth behavior when clicked", async () => {
    const wrapper = mount(BackToTopLink);
    const link = wrapper.find("a");

    await link.trigger("click");

    expect(document.getElementById).toHaveBeenCalledWith("top");
    expect(mockScrollIntoView).toHaveBeenCalledWith({ behavior: "smooth" });
  });

  it("does not throw error if header element is not found", async () => {
    // Override getElementById mock to return null
    document.getElementById = vi.fn().mockReturnValue(null);

    const wrapper = mount(BackToTopLink);
    const link = wrapper.find("a");

    // Should not throw an error
    await expect(link.trigger("click")).resolves.not.toThrow();
    expect(mockScrollIntoView).not.toHaveBeenCalled();
  });
});
