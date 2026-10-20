type FocusTarget = { focusRow: (index: number) => void };

export function useShowMore<T>(
  items: MaybeRefOrGetter<T[]>,
  displayLimit: number,
  focusTarget?: MaybeRefOrGetter<FocusTarget | null | undefined>,
) {
  const showingAll = ref(false);

  async function toggle() {
    showingAll.value = !showingAll.value;
    if (!showingAll.value) return;

    await nextTick();
    toValue(focusTarget)?.focusRow(displayLimit);
  }

  const state = computed(() => {
    const allValues = toValue(items);
    return {
      hasMore: allValues.length > displayLimit,
      itemsToDisplay: showingAll.value
        ? allValues
        : toValue(items).slice(0, displayLimit),
      showingAll: showingAll.value,
    };
  });

  return { toggle, state };
}
