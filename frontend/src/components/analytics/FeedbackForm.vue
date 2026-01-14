<script setup lang="ts">
import { Button } from "primevue";
import PrimevueTextarea from "primevue/textarea";
import { NuxtLink } from "#components";
import { usePostHog } from "~/composables/usePostHog";
import { isStringEmpty } from "~/utils/textFormatting";
import ErrorOutline from "~icons/material-symbols/error-outline";

const { sendFeedbackToPostHog } = usePostHog();
const honeypotId = useId();
const feedbackMessageId = useId();
const feedback = ref("");
const errorMessage: Ref<string | undefined> = ref();
const isSent = ref(false);
const emptyMessageError = "Geben Sie Ihr Feedback in das obere Textfeld ein.";
const sendingError =
  "Es gab leider einen Fehler. Probieren Sie es zu einem späteren Moment noch einmal.";

const submitFeedback = async () => {
  errorMessage.value = undefined;
  if (isStringEmpty(feedback.value)) {
    errorMessage.value = emptyMessageError;
    return;
  }
  try {
    await sendFeedbackToPostHog(feedback.value);
    isSent.value = true;
  } catch {
    errorMessage.value = sendingError;
  }
};
defineProps<{ hideIntro?: boolean }>();

const feedbackAction = "/api/feedback";

const route = useRoute();

watch(
  () => route.query.feedback,
  (feedbackParam) => {
    if (feedbackParam === "sent") {
      isSent.value = true;
    } else if (feedbackParam === "error") {
      errorMessage.value = sendingError;
    }
  },
  { immediate: true },
);
</script>

<template>
  <div class="flex max-w-prose flex-col">
    <div
      v-if="isSent"
      class="space-y-24"
      data-test-id="feedback-sent-confirmation"
    >
      <div class="ris-heading3-bold">Vielen Dank für Ihr Feedback!</div>
      <!-- Temporarily disabled due to data issue with Form bricks -->
      <div v-if="false" class="flex flex-col space-y-8">
        <div class="ris-heading3-bold">
          Möchten Sie an Nutzungsstudien teilnehmen?
        </div>
        <p class="">
          Nutzungsstudien helfen uns zu verstehen, wie Sie diesen Service nutzen
          und wie wir sie verbessern können. Wir suchen Menschen, die sich
          unentgeltlich an der Nutzungsstudien beteiligen möchten. Dafür ist nur
          etwas Zeit und kein Vorwissen notwendig. Sobald wir eine passende
          Nutzungsstudie planen, erhalten Sie eine Einladung per E‑Mail mit
          allen Details. Die meisten Studien führen wir online durch. Sie
          brauchen für die Teilnahme:
        </p>
      </div>
      <!-- Temporarily disabled due to data issue with Form bricks -->
      <ul v-if="false" class="list-inside list-disc">
        <li>einen Computer oder ein Smartphone,</li>
        <li>eine stabile Internetverbindung</li>
        <li>und eine ruhige Umgebung</li>
      </ul>
      <!-- Temporarily disabled due to data issue with Form bricks -->
      <Button
        v-if="false"
        :as="NuxtLink"
        :to="{ name: 'usage-tests' }"
        class="flex flex-row"
      >
        Für Nutzungsstudien registrieren
      </Button>
    </div>
    <form
      v-else
      class="space-y-24"
      :action="feedbackAction"
      method="POST"
      @submit.prevent="submitFeedback"
    >
      <div v-if="!hideIntro" class="flex flex-col space-y-8">
        <h2 class="ris-heading2-bold">Geben Sie uns Feedback</h2>
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
            type="text"
            name="name"
            tabindex="-1"
            autocomplete="off"
          />
        </div>

        <div class="text-field">
          <label :for="feedbackMessageId" class="ris-label2-regular"
            >Feedback</label
          >
          <PrimevueTextarea
            :id="feedbackMessageId"
            v-model="feedback"
            :invalid="!isStringEmpty(errorMessage)"
            class="min-h-160 w-full"
            placeholder="Feedback eingeben"
            name="text"
            @update:model-value="
              () => {
                errorMessage = undefined;
              }
            "
          />
        </div>

        <small v-if="errorMessage" data-test-id="feedback-error-message">
          <ErrorOutline />
          {{ errorMessage }}
        </small>
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
  position: absolute;
  left: -5000px;
  top: auto;
  width: 1px;
  height: 1px;
  overflow: hidden;
}
</style>
