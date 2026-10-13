export function useShowMore<T>(
  items: MaybeRefOrGetter<T[]>,
  displayLimit: number,
) {
  const showingAll = ref(false);
  const hasMore = computed(() => toValue(items).length > displayLimit);
  const itemsToDisplay = computed(() =>
    showingAll.value ? toValue(items) : toValue(items).slice(0, displayLimit),
  );
  const toggle = () => (showingAll.value = !showingAll.value);

  return { hasMore, showingAll, itemsToDisplay, toggle };
}
