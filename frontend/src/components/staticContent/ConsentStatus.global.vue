<script setup lang="ts">
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
    <UiMessage role="status" class="mb-24">
      <template #icon>
        <IconCheck v-if="userConsent" />
        <IconClose v-else />
      </template>
      <client-only>
        <div v-if="userConsent" class="space-y-4">
          <p class="typo-label2-bold">
            Ich bin mit der Nutzung von Analyse-Cookies einverstanden.
          </p>
          <p>Damit helfen Sie uns, das Portal weiter zu verbessern.</p>
        </div>
        <div v-else class="space-y-4">
          <p class="typo-label2-bold">
            Ich bin mit der Nutzung von Analyse-Cookies nicht einverstanden.
          </p>
          <p>Ihre Nutzung des Portals wird nicht zu Analysezwecken erfasst.</p>
        </div>
        <template #fallback>
          <div v-if="userConsent" class="space-y-4">
            <p class="typo-label2-bold">
              Ich bin mit der Nutzung von System-Cookies einverstanden.
            </p>
            <p>
              Wir verwenden aktuell keine Analyse-Cookies, weil JavaScript
              ausgeschaltet ist.
            </p>
          </div>
          <div v-else class="space-y-4">
            <p class="typo-label2-bold">
              Ich bin mit der Nutzung von Analyse-Cookies nicht einverstanden.
            </p>
            <p>
              Ihre Nutzung des Portals wird nicht zu Analysezwecken erfasst.
            </p>
          </div>
        </template>
      </client-only>
    </UiMessage>
    <form
      v-if="userConsent"
      action="/api/cookie-consent"
      method="POST"
      @submit.prevent="handleSetTracking(false)"
    >
      <input type="hidden" name="consent" value="false" />
      <UiButton
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
      <UiButton
        label="Cookies akzeptieren"
        data-testid="settings-accept-cookie"
        type="submit"
      />
    </form>
  </div>
</template>
