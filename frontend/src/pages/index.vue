<script setup lang="ts">
import { Button, Message } from "primevue";
import { ExternalLink, NuxtLink } from "#components";
import bmjvLogo from "~/assets/img/bmjv-de-v1-web-farbig.svg";

function redirectToSearch(searchStr?: string) {
  navigateTo({ name: "suche", query: searchStr ? { query: searchStr } : {} });
}

definePageMeta({
  skipLinks: [
    { label: "Zum Inhalt", to: "#main" },
    { label: "Zum Fußbereich", to: "#footer" },
  ],
});

useSeo({
  title: "Schneller und direkter Zugang zu Rechtsinformationen",
  description:
    "Nutzen Sie das neue Rechtsinformationsportal des Bundes – Gesetze, Verordnungen und Urteile auf einen Blick.",
  ogTitle: "Rechtsinformationen des Bundes",
});

const privateFeaturesEnabled = usePrivateFeaturesFlag();
</script>

<template>
  <div class="bg-blue-800 text-white">
    <div class="content-wrapper content-grid">
      <div
        class="col-span-12 flex flex-col items-start gap-16 px-16 py-40 sm:px-24 md:px-48 md:py-64 lg:col-span-10 lg:col-start-2 lg:px-80 xl:col-span-8 xl:col-start-3 2xl:gap-24 2xl:px-96 2xl:py-80"
      >
        <div
          class="typo-label2-bold inline-block rounded-sm border border-white/10 bg-blue-700 px-8 py-4"
        >
          Testphase
        </div>

        <h1 class="typo-headline1-bold wrap-break-word hyphens-auto">
          Rechtsinformationen des Bundes
        </h1>
        <p class="typo-body-regular 2xl:ris-subhead-regular">
          Schneller und direkter Zugang zu Gesetzen, Verordnungen,
          Gerichtsentscheidungen und künftig auch Verwaltungsvorschriften des
          Bundes – an einem zentralen Ort.
        </p>
      </div>
    </div>
  </div>

  <div
    class="content-wrapper content-grid gap-y-16 pt-16 pb-32 sm:gap-y-24 sm:pt-24 md:pb-56"
  >
    <div class="feature-card">
      <div>
        <h2 class="typo-headline2-bold wrap-break-word hyphens-auto">
          Testen Sie die Suche
        </h2>
        <p class="mt-8">
          Finden Sie Gesetze, Verordnungen und Gerichtsentscheidungen der
          Bundesgerichte.
        </p>
      </div>

      <SearchSimpleSearchInput
        full-width
        model-value=""
        @update:model-value="(query) => redirectToSearch(query)"
        @empty-search="() => redirectToSearch()"
      />

      <Message
        severity="warn"
        class="ris-body2-regular"
        role="status"
        aria-live="off"
      >
        <p class="ris-body2-bold mt-2">
          Dieser Service befindet sich in der Testphase.
        </p>
        <p>
          Der Datenbestand ist noch nicht vollständig und der Service in
          Entwicklung. Wir arbeiten an der Ergänzung und Darstellung aller
          Inhalte. Für Recherchen nutzen Sie bitte weiterhin die bestehenden
          Webseiten Gesetze-im-Internet und Rechtsprechung-im-Internet.
        </p>
      </Message>
    </div>

    <div class="feature-card">
      <div>
        <h2 class="typo-headline2-bold wrap-break-word hyphens-auto">
          Testen Sie die Darstellung aktueller Gesetze, Verordnungen und
          Gerichtsentscheidungen
        </h2>
        <p class="mt-8">
          Prüfen Sie, wie Rechtsinformationen dargestellt werden und helfen Sie
          uns, die Inhalte klarer und zugänglicher zu machen.
        </p>
      </div>

      <div class="flex flex-wrap gap-16">
        <Button :as="NuxtLink" to="/suche?documentKind=N">
          Zu den Gesetzen und Verordnungen
        </Button>
        <Button :as="NuxtLink" to="/suche?documentKind=R">
          Zu den Gerichtsentscheidungen
        </Button>
      </div>
    </div>

    <div class="feature-card" v-if="privateFeaturesEnabled">
      <div>
        <h2 class="typo-headline2-bold wrap-break-word hyphens-auto">
          English translation of German laws and regulations
        </h2>
        <p class="mt-8">
          We provide translations of our German content to help you. Please note
          that the original German versions are the only authoritative source.
        </p>
      </div>

      <div>
        <Button :as="NuxtLink" :to="{ name: 'translations' }">
          Go to translations
        </Button>
      </div>
    </div>

    <div class="feature-card">
      <div>
        <h2 class="typo-headline2-bold wrap-break-word hyphens-auto">
          Testen Sie die Programmierschnittstelle
        </h2>
        <p class="mt-8">
          Rufen Sie Rechtsinformationen direkt ab und prüfen Sie, wie gut die
          Programmierschnittstelle für Ihre Anwendung geeignet ist. Ihr Feedback
          hilft uns bei der Optimierung. Die API-Dokumentation steht in
          englischer Sprache zur Verfügung.
        </p>
      </div>

      <div>
        <Button
          :as="ExternalLink"
          url="https://docs.rechtsinformationen.bund.de"
        >
          Zur API-Dokumentation
        </Button>
      </div>
    </div>

    <div class="feature-card bmjv-card">
      <img
        class="mt-20 ml-20 self-start md:mt-4 md:ml-0"
        :src="bmjvLogo"
        alt="Bundesministerium der Justiz und für Verbraucherschutz"
      />
      <div class="space-y-8">
        <h3 class="typo-body-bold wrap-break-word hyphens-auto">
          Ein Service im Auftrag des Bundesministeriums der Justiz und für
          Verbraucherschutz
        </h3>
        <p class="typo-body-regular">
          Dieser Service befindet sich in der Testphase. Sie haben die
          Möglichkeit, erste Funktionen frühzeitig zu testen und Feedback zu
          geben. Die Funktionen und der Umfang der Daten werden schrittweise
          erweitert.
        </p>
        <NuxtLink class="typo-link-regular" :to="{ name: 'ueber' }"
          >Weitere Informationen zur Testphase</NuxtLink
        >
      </div>
    </div>
  </div>
</template>

<style scoped>
@reference "~/assets/main.css";

.feature-card {
  @apply col-span-12 flex flex-col gap-16 bg-white p-16 sm:p-24 md:gap-24 md:p-48 lg:col-span-10 lg:col-start-2 lg:px-80 xl:col-span-8 xl:col-start-3 2xl:px-96 2xl:py-56;
}

.bmjv-card {
  @apply gap-x-64 gap-y-32 md:flex-row;
}
</style>
