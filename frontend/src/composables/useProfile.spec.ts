import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { describe, it, expect } from "vitest";
import { useProfile } from "~/composables/useProfile";

const { useRuntimeConfigMock } = vi.hoisted(() => ({
  useRuntimeConfigMock: vi.fn(),
}));

describe("useProfile composable", () => {
  beforeEach(() => {
    mockNuxtImport("useRuntimeConfig", () => useRuntimeConfigMock);
  });

  it("detects public profile", () => {
    useRuntimeConfigMock.mockReturnValue({ public: { profile: "public" } });

    const { isPublicProfile, isInternalProfile, isPrototypeProfile } =
      useProfile();

    expect(isPublicProfile()).toBe(true);
    expect(isInternalProfile()).toBe(false);
    expect(isPrototypeProfile()).toBe(false);
  });

  it("detects internal profile", () => {
    useRuntimeConfigMock.mockReturnValue({ public: { profile: "internal" } });

    const { isPublicProfile, isInternalProfile, isPrototypeProfile } =
      useProfile();

    expect(isPublicProfile()).toBe(false);
    expect(isInternalProfile()).toBe(true);
    expect(isPrototypeProfile()).toBe(false);
  });

  it("detects prototype profile", () => {
    useRuntimeConfigMock.mockReturnValue({ public: { profile: "prototype" } });

    const { isPublicProfile, isInternalProfile, isPrototypeProfile } =
      useProfile();

    expect(isPublicProfile()).toBe(false);
    expect(isInternalProfile()).toBe(false);
    expect(isPrototypeProfile()).toBe(true);
  });
});
