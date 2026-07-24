<script setup lang="ts">
import { ref } from "vue";

const feedback = ref("");
const errorMessage = ref<string | undefined>();
const isSent = ref(false);

const SENDING_ERROR_MESSAGE =
  "There has been an error submitting your feedback. Please try again later.";

const submitFeedback = async () => {
  errorMessage.value = undefined;
  if (!feedback.value.trim()) {
    errorMessage.value = "Please enter your feedback";
    return;
  }
  try {
    const params = new URLSearchParams({
      text: feedback.value,
      url: globalThis.location.href,
      user_id: "anonymous_api_documentation_user",
    });
    const result = await fetch(
      `https://testphase.rechtsinformationen.bund.de/v1/feedback?${params.toString()}`,
      { method: "GET" },
    );

    if (!result.ok) {
      errorMessage.value = SENDING_ERROR_MESSAGE;
      return;
    }

    isSent.value = true;
  } catch {
    errorMessage.value = SENDING_ERROR_MESSAGE;
  }
};
</script>

<template>
  <div :class="$style.form">
    <div v-if="isSent">
      <p><strong>Thank you for your Feedback!</strong></p>
    </div>
    <div v-else>
      <h3>Give us feedback</h3>
      <p>
        Your feedback will help us to improve the documentation. Describe your
        request in as much detail as possible. Do not enter any personal data.
      </p>
      <form novalidate @submit.prevent="submitFeedback">
        <div :class="$style.field">
          <textarea
            v-model="feedback"
            placeholder="Enter feedback"
            rows="6"
            :class="[$style.textarea]"
            @input="errorMessage = undefined"
            required
          />
          <small v-if="errorMessage" :class="$style.error">
            {{ errorMessage }}
          </small>
        </div>
        <button type="submit" class="ris-button">Send Feedback</button>
      </form>
    </div>
  </div>
</template>

<style module>
.form {
  & h3 {
    margin-top: 0;
    font-size: 1.25rem;
  }
}

.field {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  margin-block: 1rem;
}

.textarea {
  width: 100%;
  padding: 0.5rem;
  border: 2px solid var(--vp-c-brand-1);
  border-radius: 4px;
  font: inherit;
  font-size: 1rem;
  resize: vertical;
  background: var(--vp-c-bg);
  color: var(--vp-c-text-1);

  &:focus {
    outline: none;
    border-width: 3px;
  }

  &:user-invalid {
    border-color: var(--vp-c-danger-1);
  }
}

.error {
  color: var(--vp-c-danger-1);
}
</style>
