import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { describe, it, expect, vi, beforeEach } from "vitest";
import { useRedirectToSearch } from "./useRedirectToSearch";

const mockReset = vi.fn();

vi.mock("~/stores/searchParams", () => ({
  useSimpleSearchParamsStore: () => ({
    $reset: mockReset,
  }),
}));

const { navigateToMock } = vi.hoisted(() => ({
  navigateToMock: vi.fn(),
}));

mockNuxtImport("navigateTo", () => navigateToMock);

describe("useRedirectToSearch", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("navigates to search page", async () => {
    const redirect = useRedirectToSearch();
    await redirect();

    expect(navigateToMock).toHaveBeenCalledWith({
      path: "/search",
      query: undefined,
    });
  });

  it("navigates with query params", async () => {
    const redirect = useRedirectToSearch();
    await redirect({ q: "test", category: "N" });

    expect(navigateToMock).toHaveBeenCalledWith({
      path: "/search",
      query: { q: "test", category: "N" },
    });
  });

  it("resets the store after navigation", async () => {
    const redirect = useRedirectToSearch();
    await redirect();

    expect(mockReset).toHaveBeenCalled();
  });
});
