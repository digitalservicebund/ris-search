import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { mount } from "@vue/test-utils";
import { vi, describe, it, expect } from "vitest";
import NavbarTop from "./NavbarTop.vue";
import { redirectToLogin } from "~/utils/redirectToLogin";

mockNuxtImport("useUserSession", () => {
  return () => ({
    loggedIn: true,
    user: { name: "Jane Doe" },
  });
});

describe("NavbarTop", async () => {
  it("renders user info when authenticated", async () => {
    const wrapper = mount(NavbarTop, {
      shallow: true,
    });

    // Check that the user's name is rendered
    expect(wrapper.text()).toContain("Jane Doe");
  });

  it("signs out and redirects when the logout button is clicked", async () => {
    vi.stubGlobal(
      "fetch",
      vi.fn().mockImplementation(() => Promise.resolve()),
    );
    vi.mock("~/utils/redirectToLogin");

    const wrapper = mount(NavbarTop, { shallow: true });

    // Find the logout button and trigger a click
    const logoutButton = wrapper.find("button");
    await logoutButton.trigger("click");

    expect(fetch).toHaveBeenCalledWith("/auth/keycloak", { method: "DELETE" });
    expect(redirectToLogin).toHaveBeenCalledOnce();
  });
});
