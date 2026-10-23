import { renderSuspended } from "@nuxt/test-utils/runtime";
import { userEvent } from "@testing-library/user-event";
import { screen } from "@testing-library/vue";
import { beforeEach, describe, expect, it, vi } from "vitest";
import ConsentStatus from "./ConsentStatus.global.vue";

const mockIsConsentGiven = ref(false);
const mockSetConsent = vi.fn();

vi.mock("~/composables/useConsent", () => ({
  useConsent: () => ({
    isConsentGiven: mockIsConsentGiven,
    setConsent: mockSetConsent,
  }),
}));

const factory = async (isConsentGiven: boolean) => {
  mockIsConsentGiven.value = isConsentGiven;
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
    mockIsConsentGiven.value = false;
    vi.clearAllMocks();
  });

  it("renders without error", async () => {
    await factory(false);
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

  it("calls setConsent(true) when clicking accept button", async () => {
    const user = userEvent.setup();
    await factory(false);
    await user.click(screen.getByTestId(acceptButtonTestId));
    expect(mockSetConsent).toHaveBeenCalledWith(true);
  });

  it("calls setConsent(false) when clicking decline button", async () => {
    const user = userEvent.setup();
    await factory(true);
    await user.click(screen.getByTestId(declineButtonTestId));
    expect(mockSetConsent).toHaveBeenCalledWith(false);
  });
});
