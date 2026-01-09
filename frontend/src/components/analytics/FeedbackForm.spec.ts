import type { VueWrapper } from "@vue/test-utils";
import { flushPromises, mount } from "@vue/test-utils";
import { beforeEach, describe, expect, it, vi } from "vitest";

async function clickSubmit(wrapper: VueWrapper) {
  await wrapper.find("form").trigger("submit");
  await flushPromises();
}

async function fillFeedbackForm(
  wrapper: VueWrapper,
  feedback: string = "Some feedback",
) {
  const textarea = wrapper.findComponent({ name: "Textarea" });
  await textarea.setValue(feedback);
}

function getErrorMessage(wrapper: VueWrapper) {
  return wrapper.find('[data-test-id="feedback-error-message"]').text();
}

function mockSendFeedbackToPostHog(mockedSend: () => Promise<void>) {
  vi.doMock("~/composables/usePostHog", () => ({
    usePostHog: () => ({
      sendFeedbackToPostHog: mockedSend,
    }),
  }));
}

const factory = async () => {
  // Dynamically import the component to be able to mock other imports the component depends on
  // a per-test basis
  const { default: FeedbackForm } = await import("./FeedbackForm.vue");
  return mount(FeedbackForm, {
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
    const wrapper = await factory();
    await clickSubmit(wrapper);
    expect(getErrorMessage(wrapper)).toBe(
      "Geben Sie Ihr Feedback in das obere Textfeld ein.",
    );

    const textarea = wrapper.findComponent({ name: "Textarea" });
    await textarea.setValue("A new message to clear the error");

    expect(
      wrapper.find('[data-test-id="feedback-error-message"]').exists(),
    ).toBe(false);
  });

  it("shows an error when sending feedback using posthog store fails", async () => {
    mockSendFeedbackToPostHog(async () => {
      throw new Error(`Error sending feedback`);
    });
    const wrapper = await factory();

    await fillFeedbackForm(wrapper);
    await clickSubmit(wrapper);
    expect(getErrorMessage(wrapper)).toBe(
      "Es gab leider einen Fehler. Probieren Sie es zu einem späteren Moment noch einmal.",
    );
  });

  it("shows a confirmation message when feedback is sent successfully", async () => {
    mockSendFeedbackToPostHog(async () => {});

    const wrapper = await factory();
    await fillFeedbackForm(wrapper);
    await clickSubmit(wrapper);
    const confirmationMessage = wrapper.find(
      '[data-test-id="feedback-sent-confirmation"]',
    );
    expect(confirmationMessage.exists()).toBe(true);
    expect(confirmationMessage.text()).toContain(
      "Vielen Dank für Ihr Feedback!",
    );
  });

  it("renders form with correct action URL for no-JS fallback", async () => {
    const wrapper = await factory();
    const form = wrapper.find("form");

    expect(form.attributes("action")).toBe("/api/feedback");
    expect(form.attributes("method")).toBe("POST");

    const textarea = wrapper.findComponent({ name: "Textarea" });
    expect(textarea.attributes("name")).toBe("text");
  });

  it("hides intro when hideIntro prop is true", async () => {
    const { default: FeedbackForm } = await import("./FeedbackForm.vue");
    const wrapper = mount(FeedbackForm, {
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

    expect(wrapper.find("h2").exists()).toBe(false);
    expect(wrapper.text()).not.toContain("Geben Sie uns Feedback");
  });

  it("clears error message when user types in textarea", async () => {
    const wrapper = await factory();

    await clickSubmit(wrapper);
    expect(getErrorMessage(wrapper)).toBeTruthy();

    const textarea = wrapper.findComponent({ name: "Textarea" });
    await textarea.setValue("New feedback");

    expect(
      wrapper.find('[data-test-id="feedback-error-message"]').exists(),
    ).toBe(false);
  });
});
