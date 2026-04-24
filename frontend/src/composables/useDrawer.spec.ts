import { mountSuspended } from "@nuxt/test-utils/runtime";
import { nextTick } from "vue";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { useDrawer } from "./useDrawer";

describe("useDrawer", () => {
  const TestComponent = defineComponent({
    setup() {
      const drawer = useDrawer();

      // Seems like refs aren't automatically bound in tests, exposing a setter
      // instead so we can define it manually.
      function setTrigger(el: HTMLElement | null) {
        drawer.triggerRef.value = el;
      }

      return { ...drawer, setTrigger };
    },
    template: `<div><div v-if="visible" role="dialog">Drawer</div></div>`,
  });

  let pushStateSpy: ReturnType<typeof vi.spyOn>;
  let backSpy: ReturnType<typeof vi.spyOn>;

  beforeEach(() => {
    pushStateSpy = vi.spyOn(history, "pushState");
    backSpy = vi.spyOn(history, "back").mockImplementation(() => {});
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it("pushes a history state when the drawer opens", async () => {
    const wrapper = await mountSuspended(TestComponent);

    wrapper.vm.visible = true;
    await nextTick();

    expect(pushStateSpy).toHaveBeenCalledWith(
      { drawerOpen: true },
      "",
      location.href,
    );
  });

  it("calls history.back() when the drawer closes normally", async () => {
    const wrapper = await mountSuspended(TestComponent);

    wrapper.vm.visible = true;
    await nextTick();

    wrapper.vm.visible = false;

    await vi.waitFor(() => {
      expect(backSpy).toHaveBeenCalledTimes(1);
    });
  });

  it("closes the drawer when a popstate event fires", async () => {
    const wrapper = await mountSuspended(TestComponent);

    wrapper.vm.visible = true;
    await nextTick();

    globalThis.dispatchEvent(new PopStateEvent("popstate"));

    await vi.waitFor(() => {
      expect(wrapper.vm.visible).toBe(false);
    });
  });

  it("does not call history.back() when closed via popstate", async () => {
    const wrapper = await mountSuspended(TestComponent);

    wrapper.vm.visible = true;
    await nextTick();

    globalThis.dispatchEvent(new PopStateEvent("popstate"));

    // Need to manually process all ticks instead of waiting for the assertion
    // to pass because this would pass by default as "has not been called" is
    // always true before anything happened. We need to make sure it's still
    // true after the changes.
    await nextTick();
    await nextTick();
    expect(backSpy).not.toHaveBeenCalled();
  });

  it("returns focus to triggerRef when the drawer closes", async () => {
    const wrapper = await mountSuspended(TestComponent);

    const button = document.createElement("button");
    document.body.appendChild(button);
    wrapper.vm.setTrigger(button);

    wrapper.vm.visible = true;
    await nextTick();

    wrapper.vm.visible = false;

    vi.waitFor(() => {
      expect(document.activeElement).toBe(button);
    });
  });
});
