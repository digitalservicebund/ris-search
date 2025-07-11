import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { mount } from "@vue/test-utils";
import { describe, it, vi, expect } from "vitest";
import LoginActions from "./LoginActions.vue";
import { redirectToLogin } from "~/utils/redirectToLogin";

const { useAuthMock, mockUseAuth } = vi.hoisted(() => {
  const mockUseAuth = {
    loggedIn: true,
    user: { name: "Jane Doe" },
  };
  return {
    useAuthMock: vi.fn().mockImplementation(() => mockUseAuth),
    mockUseAuth,
  };
});
mockNuxtImport("useUserSession", () => useAuthMock);

describe("LoginActions", async () => {
  it("renders user info when authenticated", async () => {
    const wrapper = mount(LoginActions, {
      shallow: true,
    });

    // Check that the user's name is rendered
    expect(wrapper.text()).toContain("Jane Doe");
  });

  it("calls signOut when logout button is clicked", async () => {
    vi.stubGlobal(
      "fetch",
      vi.fn().mockImplementation(() => Promise.resolve()),
    );
    vi.mock("~/utils/redirectToLogin");

    const wrapper = mount(LoginActions, {
      shallow: true,
    });

    // Find the logout button and trigger a click
    const logoutButton = wrapper.find("button");
    await logoutButton.trigger("click");

    expect(fetch).toHaveBeenCalledWith("/auth/keycloak", { method: "DELETE" });
    expect(redirectToLogin).toHaveBeenCalledOnce();
  });

  it("does not render the logout button when not authenticated", async () => {
    useAuthMock.mockImplementation(() => {
      return { ...mockUseAuth, loggedIn: false, user: null };
    });

    const wrapper = mount(LoginActions, { shallow: true });

    expect(wrapper.findAll("button")).toHaveLength(0);
  });
});
