import { renderSuspended } from "@nuxt/test-utils/runtime";
import { userEvent } from "@testing-library/user-event";
import { screen } from "@testing-library/vue";
import { beforeEach, describe, expect, it, vi } from "vitest";
import ConsentBanner from "./ConsentBanner.vue";

const mockIsBannerVisible = ref(true);
const mockSetConsent = vi.fn();

vi.mock("~/composables/useConsent", () => ({
  useConsent: () => ({
    isBannerVisible: mockIsBannerVisible,
    setConsent: mockSetConsent,
  }),
}));

const factory = async (isBannerVisible: boolean) => {
  mockIsBannerVisible.value = isBannerVisible;
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
    mockIsBannerVisible.value = true;
    vi.clearAllMocks();
  });

  it("shows the banner when user has not decided on consent yet", async () => {
    await factory(true);
    expect(
      screen.getByRole("region", {
        name: "Cookie-Einstellungen akzeptieren oder ablehnen",
      }),
    ).toBeInTheDocument();
  });

  it("hides the banner when user has already decided on consent", async () => {
    await factory(false);
    expect(
      screen.queryByRole("region", {
        name: "Cookie-Einstellungen akzeptieren oder ablehnen",
      }),
    ).not.toBeInTheDocument();
  });

  it("grants consent when clicking the Accept button", async () => {
    const user = userEvent.setup();
    await factory(true);
    await user.click(screen.getByRole("button", { name: "Akzeptieren" }));
    expect(mockSetConsent).toHaveBeenCalledWith(true);
  });

  it("denies consent when clicking the Decline button", async () => {
    const user = userEvent.setup();
    await factory(true);
    await user.click(screen.getByRole("button", { name: "Ablehnen" }));
    expect(mockSetConsent).toHaveBeenCalledWith(false);
  });
});
