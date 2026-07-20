import { renderSuspended } from "@nuxt/test-utils/runtime";
import { fireEvent, screen } from "@testing-library/vue";
import { beforeEach, describe, expect, it, vi } from "vitest";
import ConsentBanner from "./ConsentBanner.vue";

const mockUserConsent = ref<boolean | undefined>(undefined);
const mockSetTracking = vi.fn();

vi.mock("~/composables/usePostHog", () => ({
  usePostHog: () => ({
    userConsent: mockUserConsent,
    isBannerVisible: computed(() => mockUserConsent.value === undefined),
    setTracking: mockSetTracking,
    initialize: vi.fn(),
    postHog: ref(undefined),
  }),
}));

const factory = async (userConsent: boolean | undefined) => {
  mockUserConsent.value = userConsent;
  return renderSuspended(ConsentBanner, {
    global: {
      stubs: {
        NuxtLink: { template: "<a><slot /></a>" },
      },
    },
  });
};

describe("ConsentBanner", () => {
  beforeEach(() => {
    mockUserConsent.value = undefined;
    vi.clearAllMocks();
  });

  it("shows the banner when user has not given consent yet", async () => {
    await factory(undefined);
    expect(
      screen.getByRole("region", {
        name: "Cookie-Einstellungen akzeptieren oder ablehnen",
      }),
    ).toBeInTheDocument();
  });

  it("hides the banner when user has already given consent", async () => {
    await factory(true);
    expect(
      screen.queryByRole("region", {
        name: "Cookie-Einstellungen akzeptieren oder ablehnen",
      }),
    ).not.toBeInTheDocument();
  });

  it("sets tracking when clicking the Decline button", async () => {
    await factory(undefined);
    const declineButton = screen.getByRole("button", { name: "Ablehnen" });
    await fireEvent.submit(declineButton.closest("form")!);
    expect(mockSetTracking).toHaveBeenCalledWith(false);
  });
});
