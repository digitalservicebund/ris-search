import type { AsyncDataRequestStatus } from "#app";

/**
 * Drives Nuxt's global loading indicator for a search that is re-run from the
 * page already showing its results, so it looks like a full page load.
 *
 * Nuxt starts the indicator on every navigation, but ends it again on the next
 * tick when only the query changed, because the page component isn't re-created
 * and Suspense never goes pending. That would leave the indicator invisible for
 * the entire search, so this re-asserts it for as long as the search runs.
 *
 * Re-creating the page instead, via `key` in `definePageMeta`, would let Nuxt
 * drive the indicator on its own. It also discards the unsubmitted query draft
 * and the drawer state that the search pages keep in plain refs, which is why
 * we do it this way round.
 *
 * @param searchStatus - Request status of the search shown on this page
 * @returns Whether a search is currently running
 */
export function useSearchLoadingIndicator(
  searchStatus: MaybeRefOrGetter<AsyncDataRequestStatus>,
) {
  const nuxtApp = useNuxtApp();
  const { start, finish } = useLoadingIndicator();

  const isSearching = computed(() => toValue(searchStatus) === "pending");

  // This handler is registered after the indicator's own, which runs on the
  // same hook, so start() lands after the finish() that Nuxt triggers.
  onScopeDispose(
    nuxtApp.hook("page:loading:end", () => {
      if (isSearching.value) start();
    }),
  );

  watch(isSearching, (searching) => {
    if (!searching) finish();
  });

  return { isSearching };
}
