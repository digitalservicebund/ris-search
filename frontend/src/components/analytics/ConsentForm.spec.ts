import { mockNuxtImport, renderSuspended } from "@nuxt/test-utils/runtime";
import { userEvent } from "@testing-library/user-event";
import { screen } from "@testing-library/vue";
import { beforeEach, describe, expect, it, vi } from "vitest";
import ConsentForm from "./ConsentForm.vue";

const mockSetConsent = vi.fn();

vi.mock("~/composables/useConsent", () => ({
  useConsent: () => ({ setConsent: mockSetConsent }),
}));

mockNuxtImport("useRoute", () => () => ({ fullPath: "/suche?query=test" }));

describe("ConsentForm", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("renders a submit button with the label", async () => {
    await renderSuspended(ConsentForm, {
      props: { isGranted: true, label: "Akzeptieren" },
    });

    const button = screen.getByRole("button", { name: "Akzeptieren" });
    expect(button).toHaveAttribute("type", "submit");
  });

  it("posts to the consent endpoint", async () => {
    const { container } = await renderSuspended(ConsentForm, {
      props: { isGranted: true, label: "Akzeptieren" },
    });

    const form = container.querySelector("form");
    expect(form).toHaveAttribute("action", "/api/consent");
    expect(form).toHaveAttribute("method", "POST");
  });

  it.each([
    [true, "granted"],
    [false, "denied"],
  ])(
    "sends the consent decision for isGranted=%s",
    async (isGranted, value) => {
      const { container } = await renderSuspended(ConsentForm, {
        props: { isGranted, label: "Label" },
      });

      const input = container.querySelector('input[name="consent"]');
      expect(input).toHaveValue(value);
    },
  );

  it("sends the current page as redirect target", async () => {
    const { container } = await renderSuspended(ConsentForm, {
      props: { isGranted: true, label: "Akzeptieren" },
    });

    const input = container.querySelector('input[name="redirect"]');
    expect(input).toHaveValue("/suche?query=test");
  });

  it("passes attributes to the button", async () => {
    await renderSuspended(ConsentForm, {
      props: { isGranted: true, label: "Akzeptieren" },
      attrs: { "data-testid": "accept" },
    });

    expect(screen.getByTestId("accept")).toBe(
      screen.getByRole("button", { name: "Akzeptieren" }),
    );
  });

  it.each([true, false])(
    "sets consent to %s on submit without posting the form",
    async (isGranted) => {
      const user = userEvent.setup();
      const { container } = await renderSuspended(ConsentForm, {
        props: { isGranted, label: "Label" },
      });
      const onSubmit = vi.fn();
      container.querySelector("form")?.addEventListener("submit", onSubmit);

      await user.click(screen.getByRole("button", { name: "Label" }));

      expect(mockSetConsent).toHaveBeenCalledWith(isGranted);
      expect(onSubmit.mock.calls[0]?.[0].defaultPrevented).toBe(true);
    },
  );
});
