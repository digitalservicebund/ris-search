import type { Ref } from "vue";

/**
 * Manages a local "draft" copy of a value that is only meant to be committed
 * back to `committed` explicitly (e.g. when a user taps "Anwenden" in a modal),
 * rather than on every change.
 *
 * The draft is re-seeded from `committed` every time `active` transitions to
 * `true`, so edits that were never committed are discarded the next time
 * editing starts again (e.g. a modal is reopened).
 */
export function useDraftValue<T>(
  committed: Ref<T>,
  active: Ref<boolean>,
  defaultValue: T,
) {
  const draft = ref(committed.value) as Ref<T>;

  watch(active, (isActive) => {
    if (isActive) draft.value = committed.value;
  });

  function reset() {
    draft.value = defaultValue;
  }

  return { draft, reset };
}
