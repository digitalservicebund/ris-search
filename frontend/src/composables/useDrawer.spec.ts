import { mountSuspended } from "@nuxt/test-utils/runtime";
import { type ComponentPublicInstance, nextTick } from "vue";
import { describe, expect, it } from "vitest";
import { useDrawer } from "./useDrawer";

describe("useDrawer", () => {
  it("returns focus to triggerRef when the drawer closes", async () => {
    let drawer!: ReturnType<typeof useDrawer>;
    await mountSuspended(
      defineComponent({
        setup() {
          drawer = useDrawer();
        },
        template: "<div/>",
      }),
    );

    const button = document.createElement("button");
    document.body.appendChild(button);
    drawer.triggerRef.value = button;

    drawer.visible.value = true;
    await nextTick();
    drawer.visible.value = false;

    await vi.waitFor(() => expect(document.activeElement).toBe(button));
  });

  it("returns focus via $el when triggerRef holds a component instance", async () => {
    let drawer!: ReturnType<typeof useDrawer>;
    await mountSuspended(
      defineComponent({
        setup() {
          drawer = useDrawer();
        },
        template: "<div/>",
      }),
    );

    const button = document.createElement("button");
    document.body.appendChild(button);
    // Simulate a Vue component instance: an object with a $el property
    drawer.triggerRef.value = { $el: button } as ComponentPublicInstance;

    drawer.visible.value = true;
    await nextTick();
    drawer.visible.value = false;

    await vi.waitFor(() => expect(document.activeElement).toBe(button));
  });
});
