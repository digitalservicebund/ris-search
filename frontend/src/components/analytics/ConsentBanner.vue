<script setup lang="ts">
const { isBannerVisible, setConsent } = useConsent();

const headingId = useId();
</script>

<template>
  <section
    v-if="isBannerVisible"
    class="typo-body-regular flex flex-col gap-24 bg-blue-200 px-16 py-24"
    :aria-labelledby="headingId"
  >
    <div class="content-wrapper">
      <p :id="headingId" class="typo-headline2-bold">
        Cookie-Einstellungen akzeptieren oder ablehnen
      </p>

      <div class="pt-8">
        <p>
          Um zu verstehen, wie Sie den Service nutzen und um Verbesserungen
          vornehmen zu können, kann ein Analyse-Cookie durch die Firma PostHog,
          Inc. eingesetzt werden. Der Einsatz des Analyse-Cookies ist freiwillig
          und erfolgt nur mit Ihrer Einwilligung. Sie können Ihre Einwilligung
          jederzeit in den Cookie-Einstellungen widerrufen.
        </p>
      </div>

      <div class="flex flex-wrap items-center gap-x-24 gap-y-8 pt-16 lg:pt-24">
        <UiButton label="Akzeptieren" @click="setConsent(true)" />
        <UiButton label="Ablehnen" @click="setConsent(false)" />
        <NuxtLink
          :to="{ name: 'datenschutzerklaerung' }"
          class="typo-link1-regular"
        >
          Datenschutzerklärung
        </NuxtLink>
        <NuxtLink
          :to="{ name: 'cookie-einstellungen' }"
          class="typo-link1-regular"
        >
          Cookie-Einstellungen
        </NuxtLink>
      </div>
    </div>
  </section>
</template>
