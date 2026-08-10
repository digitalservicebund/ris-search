import { renderSuspended } from "@nuxt/test-utils/runtime";
import { userEvent } from "@testing-library/user-event";
import { screen } from "@testing-library/vue";
import { beforeEach, describe, expect, it, vi } from "vitest";
import ConsentStatus from "./ConsentStatus.global.vue";

const mockUserConsent = ref<boolean | undefined>(undefined);
const mockSetTracking = vi.fn();
const mockInitialize = vi.fn();

vi.mock("~/composables/usePostHog", () => ({
  CONSENT_COOKIE_NAME: "consent_given",
  usePostHog: () => ({
    userConsent: mockUserConsent,
    setTracking: mockSetTracking,
    initialize: mockInitialize,
    postHog: ref(undefined),
  }),
}));

const factory = async (userConsent: boolean | undefined) => {
  mockUserConsent.value = userConsent;
  return renderSuspended(ConsentStatus, {
    global: {
      stubs: {
        NuxtLink: { template: "<a><slot /></a>" },
        ClientOnly: { template: "<slot />" },
      },
    },
  });
};

describe("ConsentStatus", () => {
  const acceptButtonTestId = "settings-accept-cookie";
  const declineButtonTestId = "settings-decline-cookie";

  beforeEach(() => {
    mockUserConsent.value = undefined;
    vi.clearAllMocks();
  });

  it("renders without error", async () => {
    await factory(undefined);
    expect(screen.getByTestId("consent-status-wrapper")).toBeInTheDocument();
  });

  it("shows accept button when consent is not given", async () => {
    await factory(false);
    expect(screen.getByTestId(acceptButtonTestId)).toBeInTheDocument();
    expect(screen.queryByTestId(declineButtonTestId)).not.toBeInTheDocument();
  });

  it("shows decline button when consent is given", async () => {
    await factory(true);
    expect(screen.getByTestId(declineButtonTestId)).toBeInTheDocument();
    expect(screen.queryByTestId(acceptButtonTestId)).not.toBeInTheDocument();
  });

  it("displays declined message when consent is false", async () => {
    await factory(false);
    expect(screen.getByTestId("consent-status-wrapper").textContent).toContain(
      "nicht einverstanden",
    );
  });

  it("displays accepted message when consent is true", async () => {
    await factory(true);
    expect(screen.getByTestId("consent-status-wrapper").textContent).toContain(
      "Ich bin mit der Nutzung von Analyse-Cookies einverstanden",
    );
  });

  it("calls setTracking(true) when clicking accept button", async () => {
    const user = userEvent.setup();
    await factory(false);
    await user.click(screen.getByTestId(acceptButtonTestId));
    expect(mockSetTracking).toHaveBeenCalledWith(true);
  });

  it("calls setTracking(false) when clicking decline button", async () => {
    const user = userEvent.setup();
    await factory(true);
    await user.click(screen.getByTestId(declineButtonTestId));
    expect(mockSetTracking).toHaveBeenCalledWith(false);
  });
});
