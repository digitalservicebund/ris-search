<script setup lang="ts">
import PrimevueTextarea from "primevue/textarea";
import { usePostHogStore } from "~/stores/usePostHogStore";
import ErrorOutline from "~icons/material-symbols/error-outline";

const store = usePostHogStore();
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
    await store.sendFeedbackToPostHog(feedback.value);
    isSent.value = true;
  } catch {
    errorMessage.value = sendingError;
  }
};
defineProps<{ hideIntro?: boolean }>();
</script>

<template>
  <div class="flex max-w-prose flex-col">
    <div
      v-if="isSent"
      class="space-y-24"
      data-test-id="feedback-sent-confirmation"
    >
      <div class="ris-heading3-bold">Vielen Dank für Ihr Feedback!</div>
      <div class="flex flex-col space-y-8">
        <div class="ris-heading3-bold">
          Möchten Sie an Nutzungsstudien teilnehmen?
        </div>
        <p class="ris-body1-regular">
          Nutzungsstudien helfen uns zu verstehen, wie Sie diesen Service nutzen
          und wie wir sie verbessern können. Wir suchen Menschen, die sich
          unentgeltlich an der Nutzungsstudien beteiligen möchten. Dafür ist nur
          etwas Zeit und kein Vorwissen notwendig. Sobald wir eine passende
          Nutzungsstudie planen, erhalten Sie eine Einladung per E‑Mail mit
          allen Details. Die meisten Studien führen wir online durch. Sie
          brauchen für die Teilnahme:
        </p>
      </div>
      <ul class="list-inside list-disc">
        <li>einen Computer oder ein Smartphone,</li>
        <li>eine stabile Internetverbindung</li>
        <li>und eine ruhige Umgebung</li>
      </ul>
      <ButtonLink href="/nutzungstests" class="flex flex-row"
        >Für Nutzungsstudien registrieren
      </ButtonLink>
    </div>
    <div v-else class="space-y-24">
      <div v-if="!hideIntro" class="flex flex-col space-y-8">
        <h3 class="ris-heading3-bold">Geben Sie uns Feedback</h3>
        <p class="ris-body1-regular">
          Ihr Feedback hilft uns, den Service an den Bedürfnissen der
          Nutzerinnen und Nutzer auszurichten. Beschreiben Sie Ihr Anliegen so
          detailliert wie möglich. Geben Sie keine persönlichen Daten ein.
        </p>
      </div>
      <div class="flex flex-col space-y-2">
        <p class="ris-label2-regular">Feedback</p>
        <PrimevueTextarea
          id="feedback-message"
          v-model="feedback"
          :invalid="!isStringEmpty(errorMessage)"
          class="min-h-160 w-full"
          placeholder="Feedback eingeben"
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
      <div class="flex flex-row">
        <Button
          data-test-id="submit-feedback-button"
          class="w-auto"
          @click="submitFeedback"
          >Feedback senden</Button
        >
      </div>
    </div>
  </div>
</template>
