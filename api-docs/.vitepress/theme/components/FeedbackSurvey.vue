<script setup lang="ts">
import { data } from "../surveys.data";
import { computed, ref } from "vue";
import { useData } from "../composables/data";

const props = defineProps<{ id: string; context?: string }>();
const form = computed(() => data.find((f) => f.id === props.id));
const { lang, theme } = useData();
const values = ref<string[]>([]);
const isPending = ref<boolean>(false);
const state = ref<"default" | "success" | "error">("default");
const isEnabled = !import.meta.env.SSR && window.posthog;
const email = ref<string>(theme.value.contactEmail);

let pendingTimeout: NodeJS.Timeout;

const onSubmit = (/* e: Event */) => {
  clearTimeout(pendingTimeout);
  isPending.value = true;
  state.value = "default";

  // TODO: This can only handle open (textarea) inputs at the moment.
  const responses = values.value.reduce((acc, value, idx) => {
    acc[`$survey_response${idx >= 1 ? `_${idx + 1}` : ""}`] = value;
    return acc;
  }, {} as Record<string, string>);

  // TODO: At the moment we do not support multiple questions, so we
  // just append the context to the first response #hack
  if (props.context) {
    responses[
      "$survey_response"
    ] = `${props.context}: ${responses["$survey_response"]}`;
  }

  window.posthog.capture(
    "survey sent",
    { $survey_id: props.id, ...responses },
    { send_instantly: true, transport: "XHR" }
  );

  pendingTimeout = setTimeout(async () => {
    // PostHog has no way to check if the event was actually successfully
    // captured. We'll send a ping to the PostHog API to check if the request
    // is blocked (e.g., by an ad blocker).
    try {
      const response = await fetch(theme.value.posthog.host, {
        headers: { "Content-Type": "application/json" },
      });

      if (response.status === 200) {
        isPending.value = false;
        state.value = "success";
        values.value = [];
      } else {
        throw new Error();
      }
    } catch {
      isPending.value = false;
      state.value = "error";
      email.value =
        `${theme.value.contactEmail}?body=` +
        values.value.reduce(
          (acc, value) => acc + encodeURIComponent(value) + `\n\n`,
          ""
        );
    }
  }, 250);
};

const onRetry = () => {
  isPending.value = false;
  state.value = "default";
};

const onReset = () => {
  isPending.value = false;
  state.value = "default";
  values.value = [];
};
</script>

<template>
  <div
    class="bg-yellow-100 border border-yellow-300 rounded"
    v-if="!!form && isEnabled"
  >
    Feedback collection is disabled at the moment
  </div>
</template>
<!-- <template>
  <div
    class="bg-yellow-100 border border-yellow-300 rounded"
    v-if="!!form && isEnabled"
  >
    <div class="p-16 md:p-24">
      <div
        class="ris-subhead-regular mb-12 flex flex-row gap-4 align-center text-yellow-900"
      >
        <svg
          width="1.5em"
          height="1.5em"
          viewBox="0 0 25 25"
          fill="currentColor"
          xmlns="http://www.w3.org/2000/svg"
        >
          <path
            d="M18.8857 13.3494V11.3494H22.8857V13.3494H18.8857ZM20.0857 20.3494L16.8857 17.9494L18.0857 16.3494L21.2857 18.7494L20.0857 20.3494ZM18.0857 8.34937L16.8857 6.74937L20.0857 4.34937L21.2857 5.94937L18.0857 8.34937ZM5.88574 19.3494V15.3494H4.88574C4.33574 15.3494 3.86491 15.1535 3.47324 14.7619C3.08158 14.3702 2.88574 13.8994 2.88574 13.3494V11.3494C2.88574 10.7994 3.08158 10.3285 3.47324 9.93687C3.86491 9.5452 4.33574 9.34937 4.88574 9.34937H8.88574L13.8857 6.34937V18.3494L8.88574 15.3494H7.88574V19.3494H5.88574ZM11.8857 14.7994V9.89937L9.43574 11.3494H4.88574V13.3494H9.43574L11.8857 14.7994ZM14.8857 15.6994V8.99937C15.3357 9.39937 15.6982 9.88687 15.9732 10.4619C16.2482 11.0369 16.3857 11.666 16.3857 12.3494C16.3857 13.0327 16.2482 13.6619 15.9732 14.2369C15.6982 14.8119 15.3357 15.2994 14.8857 15.6994Z"
          />
        </svg>
        <span>Feedback</span>
      </div>

      <div v-if="state === 'success'">
        <div class="py-12">
          <p>
            Thank you for participating in our research. We appreciate any kind
            of feedback.
          </p>
        </div>
        <div>
          <button
            class="ds-button ds-button-small ds-button-with-icon"
            @click.prevent="onReset"
          >
            <span class="ds-button-label">Submit more feedback</span>
          </button>
        </div>
      </div>

      <div v-if="state === 'error'">
        <div class="py-12">
          <p>
            We could not submit your feedback due to a technical issue. Please
            make sure you don't have an ad blocker enabled and try again. If the
            issue keeps happening, contact us via
            <a :href="`mailto:${email}`">email</a>.
          </p>
        </div>
        <div>
          <button
            class="ds-button ds-button-small ds-button-with-icon"
            @click.prevent="onRetry"
          >
            <span class="ds-button-label">Try again</span>
          </button>
        </div>
      </div>

      <form
        class="flex flex-col gap-12"
        @submit.prevent="onSubmit"
        v-if="state === 'default'"
      >
        <div class="flex flex-col gap-8">
          <template v-for="(question, index) in form.questions">
            <div>
              <label
                :for="`${form.id}-${index}`"
                class="ris-label1-regular mb-4 inline-block"
              >
                {{ question.question[lang] }}
              </label>
              <textarea
                rows="3"
                class="ds-input h-auto ds-textarea"
                v-model="values[index]"
                :id="`${form.id}-${index}`"
                :name="`${form.id}-${index}`"
                required
                maxlength="2000"
              />
              <div class="text-sm pt-4">
                {{ (values[index] || "").length }} / 2000
              </div>
            </div>
          </template>
        </div>

        <div>
          <button
            class="ds-button ds-button-small ds-button-with-icon"
            type="submit"
          >
            <svg
              aria-hidden="true"
              role="status"
              class="animate-spin mr-8"
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              width="1.25em"
              height="1.25em"
              v-if="isPending"
            >
              <circle
                class="opacity-25"
                cx="12"
                cy="12"
                r="10"
                stroke="currentColor"
                stroke-width="4"
              />
              <path
                class="opacity-75"
                fill="currentColor"
                d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
              />
            </svg>
            <span class="ds-button-label">Submit feedback</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template> -->
