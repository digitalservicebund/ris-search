<script setup lang="ts">
import IconCheck from "~icons/ic/check";
import IconClose from "~icons/ic/close";

const { isConsentGiven, setConsent } = useConsent();
</script>

<template>
  <div class="w-fit" data-testid="consent-status-wrapper">
    <UiMessage role="status" class="mb-24">
      <template #icon>
        <IconCheck v-if="isConsentGiven" />
        <IconClose v-else />
      </template>
      <client-only>
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
        <template #fallback>
          <div v-if="isConsentGiven" class="space-y-4">
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
    <UiButton
      v-if="isConsentGiven"
      label="Cookies ablehnen"
      data-testid="settings-decline-cookie"
      @click="setConsent(false)"
    />
    <UiButton
      v-else
      label="Cookies akzeptieren"
      data-testid="settings-accept-cookie"
      @click="setConsent(true)"
    />
  </div>
</template>
