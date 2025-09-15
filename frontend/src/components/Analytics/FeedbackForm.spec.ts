import { mockNuxtImport, registerEndpoint } from "@nuxt/test-utils/runtime";
import { createTestingPinia } from "@pinia/testing";
import type { VueWrapper } from "@vue/test-utils";
import { flushPromises, mount } from "@vue/test-utils";
import { describe, expect, it, vi } from "vitest";
import FeedbackForm from "./FeedbackForm.vue";
import { useBackendURL } from "~/composables/useBackendURL";
import { getPostHogConfig } from "~/utils/testing/postHogUtils";

const { useRuntimeConfigMock } = vi.hoisted(() => {
  return {
    useRuntimeConfigMock: vi.fn(() => {
      return getPostHogConfig(undefined, undefined, "123");
    }),
  };
});

mockNuxtImport("useRuntimeConfig", () => {
  return useRuntimeConfigMock;
});

registerEndpoint(`${useBackendURL()}/v1/feedback`, () => ({
  message: "Success",
}));

const emptyErrorMessage = "Geben Sie Ihr Feedback in das obere Textfeld ein.";
const errorFeedbackMessage =
  "Es gab leider einen Fehler. Probieren Sie es zu einem späteren Moment noch einmal.";

async function clickSubmit(wrapper: VueWrapper) {
  await wrapper
    .find('[data-test-id="submit-feedback-button"]')
    .trigger("click");
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

const factory = () => {
  return mount(FeedbackForm, {
    global: {
      plugins: [
        createTestingPinia({
          stubActions: false,
          initialState: {
            postHog: {
              userConsent: undefined,
            },
          },
        }),
      ],
      stubs: {
        Textarea: {
          name: "Textarea",
          props: ["modelValue"],
          template:
            '<textarea :value="modelValue" @input="$emit(\'update:modelValue\', $event.target.value)" />',
        },
        Button: {
          name: "Button",
          template: "<button><slot /></button>",
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
  });

  it("shows an error when feedback input is empty", async () => {
    const wrapper = factory();
    await clickSubmit(wrapper);
    expect(getErrorMessage(wrapper)).toBe(emptyErrorMessage);
  });

  it("shows an error when postHog is not initialized", async () => {
    const wrapper = factory();
    await fillFeedbackForm(wrapper);
    await clickSubmit(wrapper);
    expect(getErrorMessage(wrapper)).toBe(errorFeedbackMessage);
  });

  it("shows a confirmation message when feedback is sent successfully", async () => {
    useRuntimeConfigMock.mockReturnValue(
      getPostHogConfig("key", "host", "123"),
    );
    const wrapper = factory();
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
});
