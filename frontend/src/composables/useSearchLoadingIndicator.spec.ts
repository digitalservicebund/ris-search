import { mockNuxtImport, mountSuspended } from "@nuxt/test-utils/runtime";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import type { AsyncDataRequestStatus } from "#app";
import { useSearchLoadingIndicator } from "./useSearchLoadingIndicator";

const { startMock, finishMock, useLoadingIndicatorMock } = vi.hoisted(() => {
  const start = vi.fn();
  const finish = vi.fn();

  return {
    startMock: start,
    finishMock: finish,
    useLoadingIndicatorMock: vi.fn(() => ({ start, finish })),
  };
});

mockNuxtImport("useLoadingIndicator", () => {
  return useLoadingIndicatorMock;
});

// The page:loading:end hook lives on the shared Nuxt app, so a component left
// mounted would keep reacting to it in later tests.
let mounted: { unmount: () => void } | undefined;

async function mountWithStatus(status: Ref<AsyncDataRequestStatus>) {
  let indicator!: ReturnType<typeof useSearchLoadingIndicator>;

  const wrapper = await mountSuspended(
    defineComponent({
      setup() {
        indicator = useSearchLoadingIndicator(status);
      },
      template: "<div/>",
    }),
  );
  mounted = wrapper;

  return { indicator, wrapper };
}

/** Simulates Nuxt ending the indicator, as it does on every navigation. */
async function endPageLoading() {
  await useNuxtApp().callHook("page:loading:end");
}

describe("useSearchLoadingIndicator", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  afterEach(() => {
    mounted?.unmount();
    mounted = undefined;
  });

  it("reports whether a search is currently running", async () => {
    const status = ref<AsyncDataRequestStatus>("pending");
    const { indicator } = await mountWithStatus(status);

    expect(indicator.isSearching.value).toBe(true);

    status.value = "success";
    expect(indicator.isSearching.value).toBe(false);
  });

  it("restarts the indicator when Nuxt ends it while a search runs", async () => {
    const status = ref<AsyncDataRequestStatus>("pending");
    await mountWithStatus(status);

    await endPageLoading();

    expect(startMock).toHaveBeenCalled();
  });

  it("leaves the indicator alone when no search is running", async () => {
    const status = ref<AsyncDataRequestStatus>("success");
    await mountWithStatus(status);

    await endPageLoading();

    expect(startMock).not.toHaveBeenCalled();
  });

  it("finishes the indicator once the search settles", async () => {
    const status = ref<AsyncDataRequestStatus>("pending");
    await mountWithStatus(status);

    expect(finishMock).not.toHaveBeenCalled();

    status.value = "success";
    await nextTick();

    expect(finishMock).toHaveBeenCalled();
  });

  it("finishes the indicator when the search fails", async () => {
    const status = ref<AsyncDataRequestStatus>("pending");
    await mountWithStatus(status);

    status.value = "error";
    await nextTick();

    expect(finishMock).toHaveBeenCalled();
  });

  it("stops restarting the indicator once unmounted", async () => {
    const status = ref<AsyncDataRequestStatus>("pending");
    const { wrapper } = await mountWithStatus(status);

    wrapper.unmount();
    await endPageLoading();

    expect(startMock).not.toHaveBeenCalled();
  });
});
