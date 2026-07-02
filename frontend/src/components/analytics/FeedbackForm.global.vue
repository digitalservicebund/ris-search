<script setup lang="ts">
import { Button, Textarea } from "primevue";
import ErrorOutline from "~icons/ic/outline-error-outline";
import { NuxtLink } from "#components";

defineProps<{ hideIntro?: boolean }>();

const route = useRoute();

const { sendFeedbackToPostHog } = usePostHog();
const errorMessage: Ref<string | undefined> = ref();
const isSent = ref(false);
const sendingError =
  "Es gab leider einen Fehler. Probieren Sie es zu einem späteren Moment noch einmal.";

const feedback = ref("");
const feedbackMessageId = useId();

const honeypot = ref("");
const honeypotId = useId();

async function submitFeedback() {
  errorMessage.value = undefined;

  if (isStringEmpty(feedback.value)) {
    errorMessage.value = "Geben Sie Ihr Feedback in das obere Textfeld ein.";
    return;
  }

  try {
    await sendFeedbackToPostHog(feedback.value, honeypot.value);
    isSent.value = true;
  } catch {
    errorMessage.value = sendingError;
  }
}

watch(
  () => route.query.feedback,
  (value) => {
    if (value === "sent") isSent.value = true;
    else if (value === "error") errorMessage.value = sendingError;
  },
  { immediate: true },
);
</script>

<template>
  <div class="flex flex-col">
    <div
      v-if="isSent"
      class="space-y-24"
      data-test-id="feedback-sent-confirmation"
    >
      <div class="typo-headline3-bold">Vielen Dank für Ihr Feedback!</div>
      <p>
        Möchten Sie unseren Service auch darüber hinaus mitgestalten? Nehmen Sie
        an einer Nutzungsstudie teil und helfen Sie uns, Rechtsinformationen
        leichter zugänglich zu machen.
      </p>
      <Button :as="NuxtLink" :to="{ name: 'usage-tests' }">
        Zur Registrierungsseite
      </Button>
    </div>

    <form
      v-else
      action="/api/feedback"
      class="space-y-16"
      method="POST"
      @submit.prevent="submitFeedback"
    >
      <div v-if="!hideIntro" class="flex flex-col space-y-8">
        <h2 class="typo-headline2-bold">Geben Sie uns Feedback</h2>
        <p class="">
          Ihr Feedback hilft uns, den Service an den Bedürfnissen der
          Nutzerinnen und Nutzer auszurichten. Beschreiben Sie Ihr Anliegen so
          detailliert wie möglich. Geben Sie keine persönlichen Daten ein.
        </p>
      </div>
      <div class="flex flex-col space-y-2">
        <div class="name-field" aria-hidden="true">
          <label :for="honeypotId">Name</label>
          <input
            :id="honeypotId"
            v-model="honeypot"
            type="text"
            name="name"
            tabindex="-1"
            autocomplete="off"
          />
        </div>

        <div class="text-field">
          <label :for="feedbackMessageId" class="typo-label2-regular">
            Feedback
          </label>
          <Textarea
            :id="feedbackMessageId"
            v-model="feedback"
            :invalid="!isStringEmpty(errorMessage)"
            class="block min-h-160 w-full"
            placeholder="Feedback eingeben"
            name="text"
            @update:model-value="
              () => {
                errorMessage = undefined;
              }
            "
          />
          <small v-if="errorMessage" data-test-id="feedback-error-message">
            <ErrorOutline />
            {{ errorMessage }}
          </small>
        </div>
      </div>

      <div class="flex flex-row">
        <Button
          data-test-id="submit-feedback-button"
          class="w-auto"
          type="submit"
          >Feedback senden</Button
        >
      </div>
    </form>
  </div>
</template>

<style scoped>
.name-field {
  @apply sr-only;
}
</style>
