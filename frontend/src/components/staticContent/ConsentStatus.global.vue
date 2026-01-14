<script setup lang="ts">
import PrimeVueButton from "primevue/button";
import Message from "primevue/message";
import { CONSENT_COOKIE_NAME, usePostHog } from "~/composables/usePostHog";
import IconCheck from "~icons/ic/check";
import IconClose from "~icons/ic/close";

const { userConsent, initialize, setTracking } = usePostHog();

if (import.meta.server) {
  const cookie = useCookie<boolean>(CONSENT_COOKIE_NAME);
  userConsent.value = cookie.value ?? undefined;
}

onMounted(async () => {
  await initialize();
});

async function handleSetTracking(value: boolean) {
  await setTracking(value);
}
</script>

<template>
  <div class="w-fit" data-testid="consent-status-wrapper">
    <Message severity="info" class="ris-body2-regular mb-24 bg-white">
      <template #icon>
        <IconCheck v-if="userConsent" class="text-blue-800" />
        <IconClose v-else class="text-blue-800" />
      </template>
      <client-only>
        <div v-if="userConsent">
          <p class="ris-body2-bold">
            Ich bin mit der Nutzung von Analyse-Cookies einverstanden.
          </p>
          <p>Damit helfen Sie uns, das Portal weiter zu verbessern.</p>
        </div>
        <div v-else>
          <p class="ris-body2-bold">
            Ich bin mit der Nutzung von Analyse-Cookies nicht einverstanden.
          </p>
          <p>Ihre Nutzung des Portals wird nicht zu Analysezwecken erfasst.</p>
        </div>
        <template #fallback>
          <div v-if="userConsent">
            <p class="ris-body2-bold">
              Ich bin mit der Nutzung von System-Cookies einverstanden.
            </p>
            <p>
              Wir verwenden aktuell keine Analyse-Cookies, weil JavaScript
              ausgeschaltet ist.
            </p>
          </div>
          <div v-else>
            <p class="ris-body2-bold">
              Ich bin mit der Nutzung von Analyse-Cookies nicht einverstanden.
            </p>
            <p>
              Ihre Nutzung des Portals wird nicht zu Analysezwecken erfasst.
            </p>
          </div>
        </template>
      </client-only>
    </Message>
    <form
      v-if="userConsent"
      action="/api/cookie-consent"
      method="POST"
      @submit.prevent="handleSetTracking(false)"
    >
      <input type="hidden" name="consent" value="false" />
      <PrimeVueButton
        label="Cookies ablehnen"
        data-testid="settings-decline-cookie"
        type="submit"
      />
    </form>
    <form
      v-else
      action="/api/cookie-consent"
      method="POST"
      @submit.prevent="handleSetTracking(true)"
    >
      <input type="hidden" name="consent" value="true" />
      <PrimeVueButton
        label="Cookies akzeptieren"
        data-testid="settings-accept-cookie"
        type="submit"
      />
    </form>
  </div>
</template>
