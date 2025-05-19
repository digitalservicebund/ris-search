<script setup lang="ts">
import FeedbackForm from "~/components/Analytics/FeedbackForm.vue";
import bmjvLogo from "assets/img/BMJV_de_v1__Web_farbig.svg";
import BackToTopLink from "~/components/BackToTopLink.vue";

const route = useRoute();
const hideFeedbackForm = computed(() => route.path === "/feedback");
</script>

<template>
  <footer class="container flex flex-col gap-32 px-16 py-32">
    <client-only>
      <FeedbackForm v-if="!hideFeedbackForm" class="mb-80" />
    </client-only>
    <div class="grid grid-cols-1 gap-48 md:grid-cols-2">
      <div class="space-y-16">
        <img
          class="mt-4 self-start"
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
      <div class="space-y-16">
        <ul class="grid grid-flow-col grid-cols-2 grid-rows-4 gap-8">
          <li>
            <NuxtLink class="ris-link2-regular link-hover" to="/kontakt"
              >Kontakt</NuxtLink
            >
          </li>
          <li>
            <NuxtLink class="ris-link2-regular link-hover" to="/impressum"
              >Impressum</NuxtLink
            >
          </li>
          <li>
            <NuxtLink class="ris-link2-regular link-hover" to="/datenschutz"
              >Datenschutzerklärung</NuxtLink
            >
          </li>
          <li>
            <NuxtLink
              class="ris-link2-regular link-hover"
              to="/barrierefreiheit"
              >Barrierefreiheit</NuxtLink
            >
          </li>
          <li>
            <NuxtLink
              class="ris-link2-regular link-hover"
              to="/cookie-einstellungen"
              >Cookie-Einstellungen</NuxtLink
            >
          </li>
          <li>
            <NuxtLink class="ris-link2-regular link-hover" href="/opensource"
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
          <li>
            <NuxtLink class="ris-link2-regular link-hover" to="/nutzungstests"
              >Nutzungstests</NuxtLink
            >
          </li>
        </ul>
      </div>
    </div>
    <div class="mt-24 flex justify-end">
      <BackToTopLink />
    </div>
  </footer>
</template>
