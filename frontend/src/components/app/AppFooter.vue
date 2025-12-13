<script setup lang="ts">
import bmjvLogo from "~/assets/img/BMJV_de_v1__Web_farbig.svg";
import FeedbackForm from "~/components/analytics1/FeedbackForm.vue";
import BackToTopLink from "~/components/app/BackToTopLink.vue";
import { usePrivateFeaturesFlag } from "~/composables/usePrivateFeaturesFlag";

const route = useRoute();
const hideFeedbackForm = computed(() => route.path === "/feedback");
const privateFeaturesEnabled = usePrivateFeaturesFlag();
</script>

<template>
  <footer class="relative container flex flex-col gap-80 py-80 print:hidden">
    <FeedbackForm v-if="!hideFeedbackForm" />

    <div class="grid grid-cols-1 gap-48 md:grid-cols-2">
      <div class="space-y-16">
        <img
          :src="bmjvLogo"
          alt="Bundesministerium der Justiz und für Verbraucherschutz"
        />
        <div>
          <p class="ris-body2-regular">
            Ein Service im Auftrag des
            <ExternalLink class="ris-link2-regular" url="https://www.bmj.de"
              >Bundesministeriums der Justiz und für
              Verbraucherschutz</ExternalLink
            >
          </p>
          <p class="ris-body2-regular">
            Umsetzung und Betrieb durch
            <ExternalLink
              class="ris-link2-regular"
              url="https://digitalservice.bund.de"
              >DigitalService GmbH des Bundes</ExternalLink
            >
          </p>
        </div>
      </div>

      <div>
        <ul
          class="grid gap-8 wrap-break-word sm:grid-flow-col sm:grid-cols-2 sm:grid-rows-5"
        >
          <li>
            <NuxtLink
              class="ris-link2-regular link-hover"
              :to="{ name: 'contact' }"
              >Kontakt</NuxtLink
            >
          </li>
          <li>
            <NuxtLink
              class="ris-link2-regular link-hover"
              :to="{ name: 'imprint' }"
              >Impressum</NuxtLink
            >
          </li>
          <li>
            <NuxtLink
              class="ris-link2-regular link-hover"
              :to="{ name: 'data-protection' }"
              >Datenschutzerklärung</NuxtLink
            >
          </li>
          <li>
            <NuxtLink
              class="ris-link2-regular link-hover"
              :to="{ name: 'accessibility' }"
              >Barrierefreiheit</NuxtLink
            >
          </li>
          <li>
            <NuxtLink
              class="ris-link2-regular link-hover"
              :to="{ name: 'cookie-settings' }"
              >Cookie-Einstellungen</NuxtLink
            >
          </li>
          <li v-if="privateFeaturesEnabled">
            <NuxtLink
              class="ris-link2-regular link-hover"
              :to="{ name: 'translations' }"
              >English translations</NuxtLink
            >
          </li>
          <li>
            <NuxtLink
              class="ris-link2-regular link-hover"
              :to="{ name: 'open-source' }"
              >Open Source</NuxtLink
            >
          </li>
          <li>
            <ExternalLink
              class="ris-link2-regular link-hover"
              url="https://docs.rechtsinformationen.bund.de"
              >API-Dokumentation</ExternalLink
            >
          </li>
          <!-- Temporarily disabled due to data issue with Form bricks -->
          <li v-if="false">
            <NuxtLink
              class="ris-link2-regular link-hover"
              :to="{ name: 'usage-tests' }"
              >Nutzungstests</NuxtLink
            >
          </li>
        </ul>
      </div>
    </div>

    <BackToTopLink class="absolute right-16 bottom-32" />
  </footer>
</template>
