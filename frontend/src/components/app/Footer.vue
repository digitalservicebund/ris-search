<script setup lang="ts">
import bmjvLogo from "~/assets/img/bmjv-de-v1-web-farbig.svg";

const route = useRoute();
const hideFeedbackForm = computed(() => route.path === "/feedback");
const privateFeaturesEnabled = usePrivateFeaturesFlag();
</script>

<template>
  <section
    v-if="!hideFeedbackForm"
    class="border-t border-t-gray-400 pt-40 pb-32 md:pt-64 md:pb-56 print:hidden"
    aria-label="Geben Sie uns Feedback"
  >
    <div class="content-wrapper content-grid">
      <AnalyticsFeedbackForm class="col-span-12 lg:col-span-8 xl:col-span-6" />
    </div>
  </section>

  <footer id="footer" class="relative print:hidden">
    <div class="border-t border-t-gray-400 py-40 md:py-64">
      <div class="content-wrapper content-grid gap-y-40">
        <div class="col-span-12 space-y-16 lg:col-span-6">
          <img
            :src="bmjvLogo"
            alt="Bundesministerium der Justiz und für Verbraucherschutz"
          />
          <div>
            <p class="typo-body-regular">
              Ein Service im Auftrag des
              <ExternalLink class="typo-link-regular" url="https://www.bmj.de"
                >Bundesministeriums der Justiz und für
                Verbraucherschutz</ExternalLink
              >
            </p>
            <p class="typo-body-regular">
              Umsetzung und Betrieb durch
              <ExternalLink
                class="typo-link-regular"
                url="https://digitalservice.bund.de"
                >DigitalService GmbH des Bundes</ExternalLink
              >
            </p>
          </div>
        </div>

        <nav
          aria-label="Weitere Informationen"
          class="col-span-12 lg:col-span-6"
        >
          <ul
            class="grid gap-x-8 gap-y-8 wrap-break-word sm:grid-flow-col sm:grid-cols-2 sm:grid-rows-5 md:gap-x-16 lg:gap-x-24"
          >
            <li>
              <NuxtLink
                class="typo-link-regular link-hover"
                :to="{ name: 'kontakt' }"
                >Kontakt</NuxtLink
              >
            </li>
            <li>
              <NuxtLink
                class="typo-link-regular link-hover"
                :to="{ name: 'impressum' }"
                >Impressum</NuxtLink
              >
            </li>
            <li>
              <NuxtLink
                class="typo-link-regular link-hover"
                :to="{ name: 'datenschutzerklaerung' }"
                >Datenschutzerklärung</NuxtLink
              >
            </li>
            <li>
              <NuxtLink
                class="typo-link-regular link-hover"
                :to="{ name: 'barrierefreiheit' }"
                >Barrierefreiheit</NuxtLink
              >
            </li>
            <li>
              <NuxtLink
                class="typo-link-regular link-hover"
                :to="{ name: 'cookie-einstellungen' }"
                >Cookie-Einstellungen</NuxtLink
              >
            </li>
            <li v-if="privateFeaturesEnabled">
              <NuxtLink
                class="typo-link-regular link-hover"
                :to="{ name: 'translations' }"
                >English translations</NuxtLink
              >
            </li>
            <li>
              <NuxtLink
                class="typo-link-regular link-hover"
                :to="{ name: 'open-source' }"
                >Open Source</NuxtLink
              >
            </li>
            <li>
              <ExternalLink
                class="typo-link-regular link-hover"
                url="https://docs.rechtsinformationen.bund.de"
                >API-Dokumentation</ExternalLink
              >
            </li>
            <li>
              <NuxtLink
                class="typo-link-regular link-hover"
                :to="{ name: 'nutzungstests' }"
                >Nutzungstests</NuxtLink
              >
            </li>
          </ul>
        </nav>
      </div>
    </div>

    <AppBackToTopLink />
  </footer>
</template>
