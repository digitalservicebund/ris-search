type FocusTarget = { focusRow: (index: number) => void };

export function useShowMore<T>(
  items: MaybeRefOrGetter<T[]>,
  displayLimit: number,
  focusTarget?: MaybeRefOrGetter<FocusTarget | null | undefined>,
) {
  const showingAll = ref(false);

  async function toggle(event?: Event) {
    const trigger = event?.currentTarget as HTMLElement | null | undefined;
    showingAll.value = !showingAll.value;
    await nextTick();

    if (showingAll.value) toValue(focusTarget)?.focusRow(displayLimit);
    else trigger?.scrollIntoView({ block: "nearest" });
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
