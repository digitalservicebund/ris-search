<script setup lang="ts">
import { storeToRefs } from "pinia";
import PrimeVueButton from "primevue/button";
import { usePostHogStore } from "~/stores/usePostHogStore";
import IconCheck from "~icons/ic/check";
import IconClose from "~icons/ic/close";

const store = usePostHogStore();
const { isBannerVisible } = storeToRefs(store);
const handleSetTracking = (value: boolean) => {
  store.setTracking(value);
};
</script>

<template>
  <section
    v-if="isBannerVisible"
    class="ris-body2-regular lg:ris-body1-regular flex flex-col gap-24 bg-blue-200 px-8 py-16 lg:p-24"
    aria-label="Cookie-Einstellungen akzeptieren oder ablehnen"
    data-testid="cookie-banner"
  >
    <div class="container mx-auto">
      <h2 class="ris-heading3-bold text-xl lg:text-2xl">
        Cookie-Einstellungen akzeptieren oder ablehnen
      </h2>

      <div class="pt-8">
        <p>
          Um zu verstehen, wie Sie den Service nutzen und um Verbesserungen
          vornehmen zu können, kann ein Analyse-Cookie durch die Firma PostHog,
          Inc. eingesetzt werden. Der Einsatz des Analyse-Cookies ist freiwillig
          und erfolgt nur mit Ihrer Einwilligung. Sie können Ihre Einwilligung
          jederzeit in den Cookie-Einstellungen widerrufen.
        </p>
      </div>

      <div class="flex flex-wrap items-center gap-x-24 gap-y-12 pt-16 lg:pt-24">
        <PrimeVueButton
          aria-label="Cookie-Akzeptieren-Button"
          label="Akzeptieren"
          data-testid="accept-cookie"
          @click="handleSetTracking(true)"
        >
          <template #icon>
            <IconCheck />
          </template>
        </PrimeVueButton>
        <PrimeVueButton
          aria-label="Cookie-Ablehnen-Button"
          label="Ablehnen"
          data-testid="decline-cookie"
          @click="handleSetTracking(false)"
        >
          <template #icon>
            <IconClose />
          </template>
        </PrimeVueButton>
        <NuxtLink to="/datenschutz" class="ris-link1-regular">
          Datenschutzerklärung
        </NuxtLink>
        <NuxtLink to="/cookie-einstellungen" class="ris-link1-regular">
          Cookie-Einstellungen
        </NuxtLink>
      </div>
    </div>
  </section>
</template>
