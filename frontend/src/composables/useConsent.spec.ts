import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { useConsent } from "./useConsent";

const { postHogConfig, postHogMock } = vi.hoisted(() => ({
  postHogConfig: { publicKey: "key" },
  postHogMock: {
    opt_in_capturing: vi.fn(),
    opt_out_capturing: vi.fn(),
  },
}));

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

mockNuxtImport("usePostHog", () => () => postHogMock);

function setConsentCookie(value: string) {
  document.cookie = `consent_given=${value}; path=/`;
}

function clearConsentCookie() {
  document.cookie =
    "consent_given=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT";
}

describe("useConsent", () => {
  beforeEach(() => {
    clearConsentCookie();
    clearNuxtState("consentStatus");
    postHogConfig.publicKey = "key";
    vi.clearAllMocks();
  });

  it("is pending without a consent cookie", () => {
    const { consentStatus, isConsentGiven } = useConsent();
    expect(consentStatus.value).toBe("pending");
    expect(isConsentGiven.value).toBe(false);
  });

  it("reads the consent written by PostHog from the cookie", () => {
    setConsentCookie("1");
    const { consentStatus, isConsentGiven } = useConsent();
    expect(consentStatus.value).toBe("granted");
    expect(isConsentGiven.value).toBe(true);
  });

  it("reads a denied consent from the cookie", () => {
    setConsentCookie("0");
    const { consentStatus } = useConsent();
    expect(consentStatus.value).toBe("denied");
  });

  it("shows the banner while consent is pending", () => {
    const { isBannerVisible } = useConsent();
    expect(isBannerVisible.value).toBe(true);
  });

  it("hides the banner once consent has been decided", () => {
    setConsentCookie("0");
    const { isBannerVisible } = useConsent();
    expect(isBannerVisible.value).toBe(false);
  });

  it("hides the banner if no key is configured", () => {
    postHogConfig.publicKey = "";
    const { isBannerVisible } = useConsent();
    expect(isBannerVisible.value).toBe(false);
  });

  it("opts in to tracking without capturing an opt-in event", () => {
    const { setConsent, consentStatus, isBannerVisible } = useConsent();
    setConsent(true);
    expect(postHogMock.opt_in_capturing).toHaveBeenCalledWith({
      captureEventName: false,
    });
    expect(consentStatus.value).toBe("granted");
    expect(isBannerVisible.value).toBe(false);
  });

  it("opts out of tracking", () => {
    setConsentCookie("1");
    const { setConsent, consentStatus } = useConsent();
    setConsent(false);
    expect(postHogMock.opt_out_capturing).toHaveBeenCalled();
    expect(consentStatus.value).toBe("denied");
  });

  it("shares the consent status between usages", () => {
    const first = useConsent();
    const second = useConsent();
    first.setConsent(true);
    expect(second.consentStatus.value).toBe("granted");
  });

  it("doesn't call PostHog if it is disabled", () => {
    postHogConfig.publicKey = "";
    const { setConsent, consentStatus } = useConsent();
    setConsent(true);
    expect(postHogMock.opt_in_capturing).not.toHaveBeenCalled();
    expect(consentStatus.value).toBe("granted");
  });
});
