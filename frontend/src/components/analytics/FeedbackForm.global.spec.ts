import { renderSuspended } from "@nuxt/test-utils/runtime";
import { fireEvent, screen, waitFor } from "@testing-library/vue";
import { beforeEach, describe, expect, it, vi } from "vitest";

async function clickSubmit() {
  await fireEvent.submit(document.querySelector("form")!);
  await waitFor(() => {}); // flush microtasks
}

async function fillFeedbackForm(feedback: string = "Some feedback") {
  await fireEvent.update(screen.getByRole("textbox"), feedback);
}

function getErrorMessage() {
  return document
    .querySelector('[data-test-id="feedback-error-message"]')!
    .textContent?.trim();
}

function mockSendFeedbackToPostHog(
  mockedSend: (text: string, honeypot: string) => Promise<void>,
) {
  vi.doMock("~/composables/usePostHog", () => ({
    usePostHog: () => ({
      sendFeedbackToPostHog: mockedSend,
    }),
  }));
}

const factory = async () => {
  // Dynamically import the component to be able to mock other imports the component depends on
  // a per-test basis
  const { default: FeedbackForm } = await import("./FeedbackForm.global.vue");
  return renderSuspended(FeedbackForm, {
    global: {
      stubs: {
        Textarea: {
          name: "Textarea",
          props: ["modelValue", "name"],
          template:
            '<textarea :value="modelValue" :name="name" @input="$emit(\'update:modelValue\', $event.target.value)" />',
        },
        Button: {
          name: "Button",
          props: ["type"],
          template: '<button :type="type"><slot /></button>',
        },
        "router-link": {
          template: "<a><slot /></a>",
        },
      },
    },
  });
};

describe("FeedbackForm", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    vi.resetModules();
  });

  it("shows an error when feedback input is empty and clears after the input is cleared", async () => {
    await factory();
    await clickSubmit();
    await waitFor(() =>
      expect(getErrorMessage()).toBe(
        "Geben Sie Ihr Feedback in das obere Textfeld ein.",
      ),
    );

    await fillFeedbackForm("A new message to clear the error");

    expect(
      document.querySelector('[data-test-id="feedback-error-message"]'),
    ).not.toBeInTheDocument();
  });

  it("shows an error when sending feedback using posthog store fails", async () => {
    mockSendFeedbackToPostHog(async () => {
      throw new Error(`Error sending feedback`);
    });
    await factory();

    await fillFeedbackForm();
    await clickSubmit();
    await waitFor(() =>
      expect(getErrorMessage()).toBe(
        "Es gab leider einen Fehler. Probieren Sie es zu einem späteren Moment noch einmal.",
      ),
    );
  });

  it("shows a confirmation message when feedback is sent successfully", async () => {
    mockSendFeedbackToPostHog(async () => {});

    await factory();
    await fillFeedbackForm();
    await clickSubmit();
    await waitFor(() => {
      const confirmationMessage = document.querySelector(
        '[data-test-id="feedback-sent-confirmation"]',
      );
      expect(confirmationMessage).toBeInTheDocument();
      expect(confirmationMessage!.textContent).toContain(
        "Vielen Dank für Ihr Feedback!",
      );
    });
  });

  it("renders form with correct action URL for no-JS fallback", async () => {
    await factory();
    const form = document.querySelector("form")!;

    expect(form.getAttribute("action")).toBe("/api/feedback");
    expect(form.getAttribute("method")).toBe("POST");

    const textarea = screen.getByRole("textbox");
    expect(textarea.getAttribute("name")).toBe("text");
  });

  it("hides intro when hideIntro prop is true", async () => {
    const { default: FeedbackForm } = await import("./FeedbackForm.global.vue");
    await renderSuspended(FeedbackForm, {
      props: { hideIntro: true },
      global: {
        stubs: {
          Textarea: {
            name: "Textarea",
            props: ["modelValue"],
            template:
              '<textarea :value="modelValue" @input="$emit(\'update:modelValue\', $event.target.value)" />',
          },
          Button: {
            name: "Button",
            props: ["type"],
            template: '<button :type="type"><slot /></button>',
          },
          "router-link": { template: "<a><slot /></a>" },
        },
      },
    });

    expect(screen.queryByRole("heading", { level: 2 })).not.toBeInTheDocument();
    expect(
      screen.queryByText("Geben Sie uns Feedback"),
    ).not.toBeInTheDocument();
  });

  it("clears error message when user types in textarea", async () => {
    await factory();

    await clickSubmit();
    await waitFor(() => expect(getErrorMessage()).toBeTruthy());

    await fillFeedbackForm("New feedback");

    expect(
      document.querySelector('[data-test-id="feedback-error-message"]'),
    ).not.toBeInTheDocument();
  });

  it("renders the honeypot field hidden from users", async () => {
    await factory();
    const honeypotContainer = document.querySelector(".name-field")!;
    const honeypotInput =
      document.querySelector<HTMLInputElement>('input[name="name"]')!;

    expect(honeypotContainer.getAttribute("aria-hidden")).toBe("true");
    expect(honeypotInput.getAttribute("tabindex")).toBe("-1");
    expect(honeypotInput.getAttribute("autocomplete")).toBe("off");
    expect(honeypotContainer.classList).toContain("name-field");
  });

  it("passes the honeypot value to the sendFeedbackToPostHog function", async () => {
    const mockedSend = vi.fn().mockResolvedValue(undefined);
    mockSendFeedbackToPostHog(mockedSend);

    await factory();

    await fillFeedbackForm("Real feedback");

    const honeypotInput =
      document.querySelector<HTMLInputElement>('input[name="name"]')!;
    await fireEvent.update(honeypotInput, "I am a bot");

    await clickSubmit();
    await waitFor(() =>
      expect(mockedSend).toHaveBeenCalledWith("Real feedback", "I am a bot"),
    );
  });
});
