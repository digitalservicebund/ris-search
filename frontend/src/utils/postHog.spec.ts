import { describe, it, expect, vi, beforeEach, afterEach } from "vitest";
import { getAccessibilityRelatedMetrics } from "~/utils/postHog";

describe("getAccessibilityRelatedMetrics", () => {
  beforeEach(() => {
    Object.defineProperty(globalThis, "devicePixelRatio", {
      value: 1.25,
      configurable: true,
    });
    vi.spyOn(globalThis, "getComputedStyle").mockImplementation(() => {
      return { fontSize: "16px" } as CSSStyleDeclaration;
    });
  });

  afterEach(() => {
    vi.restoreAllMocks();
    document.body.innerHTML = "";
  });

  it("returns correct page info for dark theme", () => {
    vi.spyOn(globalThis, "matchMedia").mockImplementation(() => {
      return {
        matches: true,
        addListener: vi.fn(),
        removeListener: vi.fn(),
        addEventListener: vi.fn(),
        removeEventListener: vi.fn(),
      } as unknown as MediaQueryList;
    });

    const info = getAccessibilityRelatedMetrics();

    expect(info.zoomLevel).toBe(125);
    expect(info.defaultTextSize).toBe("16px");
    expect(info.themePreference).toBe("dark");
  });

  it("returns correct page info for light theme", () => {
    vi.spyOn(globalThis, "matchMedia").mockImplementation(() => {
      return {
        matches: false,
        addListener: vi.fn(),
        removeListener: vi.fn(),
        addEventListener: vi.fn(),
        removeEventListener: vi.fn(),
      } as unknown as MediaQueryList;
    });

    const info = getAccessibilityRelatedMetrics();

    expect(info.themePreference).toBe("light");
  });
});
