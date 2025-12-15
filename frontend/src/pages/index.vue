<script setup lang="ts">
import { Button } from "primevue";
import Message from "primevue/message";
import { ExternalLink, NuxtLink } from "#components";
import bmjvLogo from "~/assets/img/BMJV_de_v1__Web_farbig.svg";
import SimpleSearchInput from "~/components/search/SimpleSearchInput.vue";
import { usePrivateFeaturesFlag } from "~/composables/usePrivateFeaturesFlag";
import { useStaticPageSeo } from "~/composables/useStaticPageSeo";

function redirectToSearch(searchStr?: string) {
  navigateTo({ name: "search", query: searchStr ? { query: searchStr } : {} });
}

definePageMeta({ layout: "base" });

useStaticPageSeo("startseite");

const privateFeaturesEnabled = usePrivateFeaturesFlag();
</script>

<template>
  <div class="flex gap-16 bg-blue-800 pt-64 pb-96 text-white">
    <div class="container max-w-prose">
      <div
        class="ris-label3-bold mb-8 inline-block rounded-sm border border-white/10 bg-blue-700 px-8 py-4 uppercase"
      >
        Testphase
      </div>
      <h1 class="ris-heading1-regular wrap-break-word hyphens-auto">
        Rechtsinformationen des Bundes
      </h1>
      <p class="ris-subhead-regular mt-24">
        Schneller und direkter Zugang zu Gesetzen, Verordnungen,
        Gerichtsentscheidungen und künftig auch Verwaltungsvorschriften des
        Bundes – an einem zentralen Ort.
      </p>
    </div>
  </div>

  <div class="container flex max-w-prose flex-col gap-24 py-56">
    <FeatureCard>
      <div>
        <h2 class="ris-heading3-bold wrap-break-word hyphens-auto">
          Testen Sie die Suche
        </h2>
        <p class="mt-8">
          Finden Sie Gesetze, Verordnungen und Gerichtsentscheidungen der
          Bundesgerichte.
        </p>
      </div>
      <SimpleSearchInput
        full-width
        model-value=""
        @update:model-value="(query) => redirectToSearch(query)"
        @empty-search="() => redirectToSearch()"
      />
      <Message severity="warn" class="ris-body2-regular">
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
    </FeatureCard>

    <FeatureCard>
      <div>
        <h2 class="ris-heading3-bold wrap-break-word hyphens-auto">
          Testen Sie die Darstellung aktueller Gesetze, Verordnungen und
          Gerichtsentscheidungen
        </h2>
        <p class="mt-8">
          Prüfen Sie, wie Rechtsinformationen dargestellt werden und helfen Sie
          uns, die Inhalte klarer und zugänglicher zu machen.
        </p>
      </div>
      <div class="flex flex-wrap gap-16">
        <Button :as="NuxtLink" to="/search?category=N">
          Zu den Gesetzen und Verordnungen
        </Button>
        <Button :as="NuxtLink" to="/search?category=R">
          Zu den Gerichtsentscheidungen
        </Button>
      </div>
    </FeatureCard>

    <FeatureCard v-if="privateFeaturesEnabled">
      <div>
        <h2 class="ris-heading3-bold wrap-break-word hyphens-auto">
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
    </FeatureCard>

    <FeatureCard>
      <div>
        <h2 class="ris-heading3-bold wrap-break-word hyphens-auto">
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
    </FeatureCard>

    <FeatureCard inner-class="gap-x-64 gap-y-32 sm:flex-row">
      <img
        class="mt-20 ml-20 self-start sm:mt-4 md:ml-0"
        :src="bmjvLogo"
        alt="Bundesministerium der Justiz und für Verbraucherschutz"
      />
      <div class="space-y-8">
        <p class="ris-body2-bold wrap-break-word hyphens-auto">
          Ein Service im Auftrag des Bundesministeriums der Justiz und für
          Verbraucherschutz
        </p>
        <p class="ris-body2-regular">
          Dieser Service befindet sich in der Testphase. Sie haben die
          Möglichkeit, erste Funktionen frühzeitig zu testen und Feedback zu
          geben. Die Funktionen und der Umfang der Daten werden schrittweise
          erweitert.
        </p>
        <NuxtLink class="ris-link2-regular" :to="{ name: 'about' }"
          >Weitere Informationen zur Testphase</NuxtLink
        >
      </div>
    </FeatureCard>
  </div>
</template>
