export function useShowMore<T>(
  items: MaybeRefOrGetter<T[]>,
  displayLimit: number,
) {
  const showingAll = ref(false);

  const toggle = () => (showingAll.value = !showingAll.value);

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
