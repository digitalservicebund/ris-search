import "@testing-library/jest-dom";
import { vi } from "vitest";
import "~/tests/cookieStoreMock";

vi.mock("~/middleware/checkLogin.global.ts", () => ({ default: vi.fn() }));

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

// jsdom doesn't implement dialog's showModal/close. Approximate them via the
// "open" attribute they're specified to toggle, and the "close" event
// close() is specified to dispatch. See
// https://github.com/jsdom/jsdom/issues/3294
if (globalThis?.window) {
  HTMLDialogElement.prototype.showModal = function (this: HTMLDialogElement) {
    this.setAttribute("open", "");
  };
  HTMLDialogElement.prototype.close = function (this: HTMLDialogElement) {
    if (!this.open) return;
    this.removeAttribute("open");
    this.dispatchEvent(new Event("close"));
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
