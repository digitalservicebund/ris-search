import { mountSuspended } from "@nuxt/test-utils/runtime";
import { describe, expect, it } from "vitest";
import { nextTick } from "vue";
import { useDraftValue } from "./useDraftValue";

describe("useDraftValue", () => {
  it("initializes the draft from the committed value", async () => {
    let draftValue!: ReturnType<typeof useDraftValue<string>>;
    const committed = ref("initial");
    const active = ref(false);

    await mountSuspended(
      defineComponent({
        setup() {
          draftValue = useDraftValue(committed, active, "default");
        },
        template: "<div/>",
      }),
    );

    expect(draftValue.draft.value).toBe("initial");
  });

  it("re-seeds the draft from the committed value when active becomes true", async () => {
    let draftValue!: ReturnType<typeof useDraftValue<string>>;
    const committed = ref("initial");
    const active = ref(false);

    await mountSuspended(
      defineComponent({
        setup() {
          draftValue = useDraftValue(committed, active, "default");
        },
        template: "<div/>",
      }),
    );

    draftValue.draft.value = "unsaved edit";
    committed.value = "changed elsewhere";

    active.value = true;
    await nextTick();

    expect(draftValue.draft.value).toBe("changed elsewhere");
  });

  it("does not change the draft while active stays false", async () => {
    let draftValue!: ReturnType<typeof useDraftValue<string>>;
    const committed = ref("initial");
    const active = ref(false);

    await mountSuspended(
      defineComponent({
        setup() {
          draftValue = useDraftValue(committed, active, "default");
        },
        template: "<div/>",
      }),
    );

    draftValue.draft.value = "unsaved edit";
    committed.value = "changed elsewhere";
    await nextTick();

    expect(draftValue.draft.value).toBe("unsaved edit");
  });

  it("does not mutate the committed value", async () => {
    let draftValue!: ReturnType<typeof useDraftValue<string>>;
    const committed = ref("initial");
    const active = ref(false);

    await mountSuspended(
      defineComponent({
        setup() {
          draftValue = useDraftValue(committed, active, "default");
        },
        template: "<div/>",
      }),
    );

    draftValue.draft.value = "unsaved edit";

    expect(committed.value).toBe("initial");
  });

  it("resets the draft to the default value", async () => {
    let draftValue!: ReturnType<typeof useDraftValue<string>>;
    const committed = ref("initial");
    const active = ref(false);

    await mountSuspended(
      defineComponent({
        setup() {
          draftValue = useDraftValue(committed, active, "default");
        },
        template: "<div/>",
      }),
    );

    draftValue.draft.value = "unsaved edit";
    draftValue.reset();

    expect(draftValue.draft.value).toBe("default");
    expect(committed.value).toBe("initial");
  });
});
