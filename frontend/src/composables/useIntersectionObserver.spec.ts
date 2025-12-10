/* eslint-disable @typescript-eslint/no-explicit-any */
import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { mount } from "@vue/test-utils";
import { describe, it, expect, vi, beforeEach } from "vitest";
import { useIntersectionObserver } from "./useIntersectionObserver";

mockNuxtImport("useRoute", () => {
  return () => ({
    hash: "#section1",
  });
});

describe("useIntersectionObserver", () => {
  let originalWindow: Window & typeof globalThis;

  const TestComponent = defineComponent({
    props: {},
    setup() {
      return {
        // Call the composable and expose all return values into our
        // component instance so we can access them with wrapper.vm
        ...useIntersectionObserver(),
      };
    },
    template: "<div />",
  });

  beforeEach(() => {
    // Store original window to restore after each test
    originalWindow = window;

    // Create a mock window with necessary properties
    global.window = {
      ...originalWindow,
      scrollY: 0,
      addEventListener: vi.fn(),
      removeEventListener: vi.fn(),
    } as any;

    Object.defineProperty(import.meta, "client", {
      value: true,
      writable: true,
    });
  });

  afterEach(() => {
    // Restore original window
    global.window = originalWindow;
  });

  it("initializes with hash from route", async () => {
    const wrapper = mount(TestComponent, {
      global: { mocks: { $router: undefined } },
    });

    expect(wrapper.vm.selectedEntry).toBe("section1");
  });

  it("handles intersection observation", () => {
    const mockObserver = {
      observe: vi.fn(),
      disconnect: vi.fn(),
    };

    // Mock IntersectionObserver constructor
    global.IntersectionObserver = vi.fn().mockImplementation(function () {
      return mockObserver;
    });

    const wrapper = mount(TestComponent, {});
    const { vObserveElements } = wrapper.vm;

    // Mock HTMLElement and IntersectionObserver
    const mockElement = document.createElement("div");
    mockElement.innerHTML = `
      <section id="section1"></section>
      <section id="section2"></section>
    `;

    // Call mounted directive
    vObserveElements.mounted(mockElement);

    // Verify sections were observed
    expect(mockObserver.observe).toHaveBeenCalledTimes(2);
  });

  it("updates observation on element updates", () => {
    const mockObserver = {
      observe: vi.fn(),
      disconnect: vi.fn(),
    };

    global.IntersectionObserver = vi.fn().mockImplementation(function () {
      return mockObserver;
    });

    const wrapper = mount(TestComponent, {});

    const { vObserveElements } = wrapper.vm;

    const mockElement = document.createElement("div");

    mockElement.innerHTML = `
      <section id="section1"></section>
      <section id="section2"></section>
      <section id="section3"></section>
    `;

    // Call updated directive
    vObserveElements.updated(mockElement);

    // Verify previous observer was disconnected and new sections are observed
    expect(mockObserver.disconnect).toHaveBeenCalled();
    expect(mockObserver.observe).toHaveBeenCalledTimes(3);
  });

  describe("handles intersection entries", () => {
    const wrapper = mount(TestComponent, {});
    const { handleIntersection } = wrapper.vm;

    // Simulate scrolling down
    window.scrollY = 100;

    const mockEntries = [
      {
        isIntersecting: true,
        target: { id: "section2" },
      },
      {
        isIntersecting: false,
        target: { id: "section1" },
      },
    ] as IntersectionObserverEntry[];
    it("ignores the initial intersection if a section was provided by hash", () => {
      handleIntersection(mockEntries);
      expect(wrapper.vm.selectedEntry).toBe("section1");
    });
    it("considers the values on subsequent events", () => {
      handleIntersection(mockEntries);
      expect(wrapper.vm.selectedEntry).toBe("section2");
    });
  });

  it("handles server-side rendering scenario", () => {
    Object.defineProperty(import.meta, "server", {
      value: true,
      writable: false,
    });
    const wrapper = mount(TestComponent, {});
    const { vObserveElements } = wrapper.vm;

    const mockElement = document.createElement("div");

    // These should not throw errors
    expect(() => vObserveElements.mounted(mockElement)).not.toThrow();
    expect(() => vObserveElements.updated(mockElement)).not.toThrow();
    expect(() => vObserveElements.beforeUnmount()).not.toThrow();
  });
});
