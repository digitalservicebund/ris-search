import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { useTracking } from "./useTracking";

const { postHogConfig, postHogMock, postHogState } = vi.hoisted(() => {
  const mock = { capture: vi.fn() };
  return {
    postHogConfig: { publicKey: "key" },
    postHogMock: mock,
    postHogState: { instance: mock as typeof mock | undefined },
  };
});

mockNuxtImport<() => ReturnType<typeof useRuntimeConfig>>(
  "useRuntimeConfig",
  (original) => {
    return () =>
      ({
        ...original(),
        public: { ...original().public, posthog: postHogConfig },
      }) as ReturnType<typeof useRuntimeConfig>;
  },
);

mockNuxtImport("usePostHog", () => () => postHogState.instance);

describe("useTracking", () => {
  beforeEach(() => {
    postHogConfig.publicKey = "key";
    postHogState.instance = postHogMock;
    vi.clearAllMocks();
  });

  it("captures the event with its properties", () => {
    const { track } = useTracking();

    track("search_submitted", {
      query: "Mietrecht",
      document_kind: "gesetze",
    });

    expect(postHogMock.capture).toHaveBeenCalledWith("search_submitted", {
      query: "Mietrecht",
      document_kind: "gesetze",
    });
  });

  it("does nothing when PostHog is disabled", () => {
    postHogConfig.publicKey = "";
    const { track } = useTracking();

    track("search_submitted", {
      query: "Mietrecht",
      document_kind: "gesetze",
    });

    expect(postHogMock.capture).not.toHaveBeenCalled();
  });

  it("doesn't crash when PostHog isn't available", () => {
    postHogState.instance = undefined;
    const { track } = useTracking();

    expect(() =>
      track("search_submitted", {
        query: "Mietrecht",
        document_kind: "gesetze",
      }),
    ).not.toThrow();
  });
});
