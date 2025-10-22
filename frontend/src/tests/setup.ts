import "@testing-library/jest-dom";
import { config } from "@vue/test-utils";
import PrimeVue from "primevue/config";
import { vi } from "vitest";

vi.mock("~/middleware/check-login.global.ts", () => ({ default: vi.fn() }));

// Enable PrimeVue plugin because we need that in many tests
config.global.plugins = [PrimeVue];

// see https://jestjs.io/docs/manual-mocks#mocking-methods-which-are-not-implemented-in-jsdom
if (globalThis?.window) {
  Object.defineProperty(globalThis, "matchMedia", {
    writable: true,
    value: vi.fn().mockImplementation((query) => ({
      matches: false,
      media: query,
      onchange: null,
      addListener: vi.fn(), // deprecated
      removeListener: vi.fn(), // deprecated
      addEventListener: vi.fn(),
      removeEventListener: vi.fn(),
      dispatchEvent: vi.fn(),
    })),
  });
}

class ResizeObserver {
  observe() {
    // empty mock method
  }
  unobserve() {
    // empty mock method
  }
  disconnect() {
    // empty mock method
  }
}

globalThis.ResizeObserver = ResizeObserver;
