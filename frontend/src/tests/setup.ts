import "@testing-library/jest-dom";
import { config } from "@vue/test-utils";
// oxlint-disable-next-line no-restricted-imports
import PrimeVue from "primevue/config";
import { vi } from "vitest";
import "~/tests/cookieStoreMock";

vi.mock("~/middleware/checkLogin.global.ts", () => ({ default: vi.fn() }));

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

// maska (v3) registers its input listeners with an `AbortSignal` from Node's
// global AbortController. jsdom only accepts an AbortSignal it created itself
// and throws in addEventListener otherwise, so drop the signal option in tests.
// This only disables listener auto-removal on destroy, which tests don't rely
// on.
if (globalThis?.window) {
  const originalAddEventListener = EventTarget.prototype.addEventListener;
  EventTarget.prototype.addEventListener = function (
    type: string,
    listener: EventListenerOrEventListenerObject | null,
    options?: boolean | AddEventListenerOptions,
  ) {
    if (options && typeof options === "object" && "signal" in options) {
      const { signal: _signal, ...rest } = options;
      return originalAddEventListener.call(this, type, listener, rest);
    }
    return originalAddEventListener.call(this, type, listener, options);
  };
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
