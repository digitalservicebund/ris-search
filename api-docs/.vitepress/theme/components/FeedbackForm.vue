<script setup lang="ts">
import {ref} from 'vue'
import ErrorOutline from '~icons/material-symbols/error-outline'
import PrimevueTextarea from "primevue/textarea";
import {isStringEmpty} from "../utils";

const props = defineProps<{ hideHeader?: boolean }>()

const feedback = ref("")
const errorMessage = ref<string | undefined>()
const isSent = ref(false)
const emptyMessageError = "Please enter your feedback in the box provided"
const sendingError = "There has been an error submitting your feedback. Please try again later."

const submitFeedback = async () => {
  errorMessage.value = undefined
  if (isStringEmpty(feedback.value)) {
    errorMessage.value = emptyMessageError
    return
  }
  try {
    await sendSurveyResponse(feedback.value)
  } catch (error) {
    console.error("Error sending survey response:", error)
    errorMessage.value = sendingError
  }
}

async function sendSurveyResponse(surveyResponse: string) {
  const params = new URLSearchParams({
    text: surveyResponse,
    url: window.location.href,
    user_id: 'anonymous_api_documentation_user',
  });
  const result = await fetch(
      `https://testphase.rechtsinformationen.bund.de/v1/feedback?${params.toString()}`,
      {method: "GET"}
  );

  if (!result.ok) {
    throw new Error(`Error sending feedback`);
  }
  isSent.value = true;
}
</script>

<template>
  <div class="flex max-w-prose flex-col">
    <div v-if="isSent" class="space-y-24" data-test-id="feedback-sent-confirmation">
      <div class="ris-heading3-bold">Thank you for your Feedback!</div>
    </div>
    <div v-else class="space-y-24">
      <div class="flex flex-col space-y-8">
        <h3 v-if="!hideHeader" class="ris-heading3-bold">Give us feedback</h3>
        <p class="ris-body1-regular text-justify">
          Your feedback will help us to improve the documentation. Describe your request in as much detail as possible.
          Do not enter any personal data.</p>
      </div>
      <div class="flex flex-col space-y-2">
        <PrimevueTextarea
            id="feedback-message"
            v-model="feedback"
            :invalid="!isStringEmpty(errorMessage)"
            class="min-h-160 w-full border-2 focus:border-4 focus:border-blue-800 border-blue-800 p-8 text-black dark:text-white"
            placeholder="Enter feedback"
            @update:model-value="() => { errorMessage = undefined }"
        />
        <small v-if="errorMessage" data-test-id="feedback-error-message"
               class="ris-caption-regular text-red-800 flex flex-row gap-4 items-center">
          <ErrorOutline/>
          {{ errorMessage }}
        </small>
      </div>
      <div class="flex flex-row">
        <Button data-test-id="submit-feedback-button"
                class="w-auto bg-blue-800 hover:bg-blue-600 !p-16 ris-label2-bold text-white" @click="submitFeedback">
          Send Feedback
        </Button>
      </div>
    </div>
  </div>
</template>
