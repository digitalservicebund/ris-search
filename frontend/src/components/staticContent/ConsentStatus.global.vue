<script setup lang="ts">
import IconCheck from "~icons/ic/check";
import IconClose from "~icons/ic/close";

const { isConsentGiven } = useConsent();
</script>

<template>
  <div class="w-fit" data-testid="consent-status-wrapper">
    <UiMessage role="status" class="mb-24">
      <template #icon>
        <IconCheck v-if="isConsentGiven" />
        <IconClose v-else />
      </template>
      <div v-if="isConsentGiven" class="space-y-4">
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
    </UiMessage>
    <AnalyticsConsentForm
      v-if="isConsentGiven"
      :is-granted="false"
      label="Cookies ablehnen"
      data-testid="settings-decline-cookie"
    />
    <AnalyticsConsentForm
      v-else
      is-granted
      label="Cookies akzeptieren"
      data-testid="settings-accept-cookie"
    />
  </div>
</template>
