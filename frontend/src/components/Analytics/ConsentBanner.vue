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
  <div
    v-if="isBannerVisible"
    class="ris-body2-regular lg:ris-body1-regular flex flex-col gap-24 bg-blue-200 px-8 py-16 lg:p-24"
    aria-label="Cookie banner"
    data-testid="cookie-banner"
  >
    <div class="container mx-auto">
      <h2 class="ris-heading3-bold text-xl lg:text-2xl">
        Dürfen wir anonyme Nutzungsdaten speichern?
      </h2>

      <div class="pt-8">
        <p>
          Wir möchten verstehen, wie Sie die Website nutzen. Dadurch können wir
          sie für alle Nutzenden verbessern. Wir möchten zum Beispiel folgende
          Daten speichern: Wie Sie suchen, welche Seiten Sie besuchen und welche
          Funktionen Sie benutzen. Diese anonymen Nutzungsdaten werden nur mit
          Ihrer Einwilligung gespeichert.
        </p>
      </div>

      <div class="flex flex-wrap items-center gap-x-24 gap-y-12 pt-16 lg:pt-24">
        <PrimeVueButton
          aria-label="Cookie-Akzeptieren-Button"
          label="Ja, erlauben"
          data-testid="accept-cookie"
          @click="handleSetTracking(true)"
        >
          <template #icon>
            <IconCheck />
          </template>
        </PrimeVueButton>
        <PrimeVueButton
          aria-label="Cookie-Ablehnen-Button"
          label="Nein, nicht erlauben"
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
  </div>
</template>
