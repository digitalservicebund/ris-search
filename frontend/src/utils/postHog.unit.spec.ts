import { describe, it, expect, vi, beforeEach, afterEach } from "vitest";
import { getAccessibilityRelatedMetrics } from "~/utils/postHog";

describe("getAccessibilityRelatedMetrics", () => {
  beforeEach(() => {
    Object.defineProperty(window, "devicePixelRatio", {
      value: 2,
      configurable: true,
    });
    vi.spyOn(window, "getComputedStyle").mockImplementation(() => {
      return { fontSize: "16px" } as CSSStyleDeclaration;
    });
  });

  afterEach(() => {
    vi.restoreAllMocks();
    document.body.innerHTML = "";
  });

  it("returns correct page info for dark theme", () => {
    vi.spyOn(window, "matchMedia").mockImplementation(() => {
      return {
        matches: true,
        addListener: vi.fn(),
        removeListener: vi.fn(),
        addEventListener: vi.fn(),
        removeEventListener: vi.fn(),
      } as unknown as MediaQueryList;
    });

    const info = getAccessibilityRelatedMetrics();

    expect(info.zoomLevel).toBe(2);
    expect(info.defaultTextSize).toBe("16px");
    expect(info.themePreference).toBe("dark");
  });

  it("returns correct page info for light theme", () => {
    vi.spyOn(window, "matchMedia").mockImplementation(() => {
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
