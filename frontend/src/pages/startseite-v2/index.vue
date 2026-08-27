<script setup lang="ts">
import IcBaselineArrowForward from "~icons/ic/baseline-arrow-forward";
import { ExternalLink, NuxtLink } from "#components";
import { DocumentKind } from "~/types/api";

function redirectToSearch(searchStr?: string) {
  navigateTo({ name: "suche", query: searchStr ? { query: searchStr } : {} });
}

definePageMeta({
  skipLinks: [
    { label: "Zum Inhalt", to: "#main" },
    { label: "Zum Fußbereich", to: "#footer" },
  ],
  middleware: () => {
    // For some reason our private feature flag composable doesn't work in this
    // context, falling back to the runtime config directly instead
    const config = useRuntimeConfig();
    if (!config.public.privateFeaturesEnabled)
      return navigateTo({ name: "index" });
  },
});

useSeo({
  title: "Schneller und direkter Zugang zu Rechtsinformationen",
  description:
    "Nutzen Sie das neue Rechtsinformationsportal des Bundes – Gesetze, Verordnungen und Urteile auf einen Blick.",
  ogTitle: "Rechtsinformationen des Bundes",
});
</script>

<template>
  <section class="bg-white">
    <div class="bg-blue-800 text-white">
      <div
        class="content-wrapper content-grid pt-40 pb-56 md:pt-64 md:pb-96 2xl:pt-80 2xl:pb-112"
      >
        <div class="content-grid-textblock text-balance xl:col-start-2">
          <h1
            class="typo-headline1-bold mb-8 text-balance wrap-break-word hyphens-auto md:mb-16 2xl:mb-24"
          >
            Rechtsinformationen des Bundes
          </h1>
          <p class="typo-body-regular 2xl:ris-subhead-regular">
            Ein schneller und direkter Zugang zu Gesetzen, Verordnungen,
            Gerichtsentscheidungen, Verwaltungsvorschriften und
            Literaturnachweisen des Bundes – zentral an einem Ort.
          </p>
        </div>
      </div>
    </div>

    <nav class="content-wrapper content-grid pb-8 2xl:pb-16">
      <ul
        class="lg-gap-x-24 col-span-12 -mt-24 flex flex-col gap-8 md:-mt-48 md:flex-row md:gap-x-16 lg:gap-24 xl:col-span-10 xl:col-start-2"
      >
        <li class="flex-1">
          <NuxtLink
            class="document-link-card"
            :to="{
              name: 'suche',
              query: { documentKind: DocumentKind.Norm },
            }"
          >
            Gesetze und Verordnungen <span class="sr-only">öffnen</span>
            <IcBaselineArrowForward class="mt-auto ml-auto" />
          </NuxtLink>
        </li>
        <li class="flex-1">
          <NuxtLink
            class="document-link-card"
            :to="{
              name: 'suche',
              query: { documentKind: DocumentKind.CaseLaw },
            }"
          >
            Gerichtsentscheidungen <span class="sr-only">öffnen</span>
            <IcBaselineArrowForward class="mt-auto ml-auto" />
          </NuxtLink>
        </li>
        <li class="flex-1">
          <NuxtLink
            class="document-link-card"
            :to="{
              name: 'suche',
              query: { documentKind: DocumentKind.AdministrativeDirective },
            }"
          >
            Verwaltungsvorschriften <span class="sr-only">öffnen</span>
            <IcBaselineArrowForward class="mt-auto ml-auto" />
          </NuxtLink>
        </li>
        <li class="flex-1">
          <NuxtLink
            class="document-link-card"
            :to="{
              name: 'suche',
              query: { documentKind: DocumentKind.Literature },
            }"
          >
            Literaturnachweise <span class="sr-only">öffnen</span>
            <IcBaselineArrowForward class="mt-auto ml-auto" />
          </NuxtLink>
        </li>
      </ul>
    </nav>
  </section>

  <section class="bg-white">
    <div class="content-wrapper content-grid py-24 md:py-40 lg:py-48 2xl:py-56">
      <div class="content-grid-textblock xl:col-start-2">
        <h2 class="typo-headline2-bold mb-8 wrap-break-word hyphens-auto">
          Rechtsinformationen finden
        </h2>
        <p class="typo-body-regular mb-16 md:mb-24">
          Nutzen Sie Stichwörter, Themen oder direkte Angaben wie Paragrafen,
          Normen oder Aktenzeichen.
        </p>

        <SearchSimpleSearchInput
          full-width
          input-placeholder="z.B. Mietrecht, § 535 BGB, 1 BvR 123/20 …"
          model-value=""
          @update:model-value="(query) => redirectToSearch(query)"
          @empty-search="() => redirectToSearch()"
        />
      </div>
    </div>
  </section>

  <section class="border-t border-gray-400">
    <div class="content-wrapper content-grid py-24 md:py-40 lg:py-48 2xl:py-56">
      <div class="col-span-12 xl:col-span-10 xl:col-start-2">
        <RecentUpdates />
      </div>
    </div>
  </section>

  <section class="border-t border-gray-400 bg-white">
    <div
      class="content-wrapper content-grid gap-y-16 py-24 md:gap-y-24 md:py-40 lg:py-48 2xl:py-56"
    >
      <div
        class="col-span-12 md:col-span-6 lg:col-span-6 xl:col-span-5 xl:col-start-2"
      >
        <h2 class="typo-headline2-bold mb-8 wrap-break-word hyphens-auto">
          Offene Rechtsdaten für neue Anwendungen
        </h2>
        <p class="typo-body-regular mb-8">
          Unsere Rechtsinformationen stehen als Open Data zur Verfügung. Über
          unsere Programmierschnittstelle (API) lassen sich die Daten einfach
          abrufen, weiterverarbeiten und für eigene Anwendungen und Services
          nutzen.
        </p>
        <p class="typo-body-regular">
          Analysieren Sie Trends oder integrieren Sie Rechtstexte in Ihre
          Anwendungen. Die API-Dokumentation steht in englischer Sprache zur
          Verfügung.
        </p>
      </div>

      <AppCodeExample
        class="col-span-12 self-start md:col-span-6 md:row-span-2 lg:col-span-5 lg:col-start-8 xl:col-span-4 xl:col-start-8"
      />

      <div class="col-span-12 md:col-span-6 md:row-start-2 xl:col-start-2">
        <UiButton
          class="w-full md:w-auto"
          :as="ExternalLink"
          url="https://docs.rechtsinformationen.bund.de"
        >
          Zur API-Dokumentation
        </UiButton>
      </div>
    </div>
  </section>

  <section class="border-t border-gray-400 bg-gray-100">
    <div
      class="content-wrapper content-grid gap-y-24 py-24 md:py-40 lg:py-48 2xl:py-56"
    >
      <div
        class="col-span-12 flex flex-col items-start md:col-span-6 xl:col-span-5 xl:col-start-2"
      >
        <h2 class="typo-headline2-bold mb-8 wrap-break-word hyphens-auto">
          Das Portal im Überblick
        </h2>
        <p class="typo-body-regular mb-16 md:mb-24">
          Hier erfahren Sie, welche Features und Funktionen zur Verfügung
          stehen, welchen Umfang die Daten haben und was die Open-Data-Strategie
          ist.
        </p>
        <UiButton
          class="w-full md:w-auto"
          :as="NuxtLink"
          :to="{ name: 'ueber' }"
        >
          Mehr Infos zum Portal
        </UiButton>
      </div>

      <div
        class="col-span-12 flex flex-col items-start md:col-span-6 xl:col-span-5"
      >
        <h2 class="typo-headline2-bold mb-8 wrap-break-word hyphens-auto">
          Zahlen und Fakten
        </h2>
        <p class="typo-body-regular mb-16 md:mb-24">
          Hier erfahren Sie, in welchem Umfang Rechtsinformationen der
          Öffentlichkeit zur Verfügung stehen und wie diese Anwendung finden.
        </p>
        <UiButton class="w-full md:w-auto" disabled>
          Zu den Zahlen und Fakten
        </UiButton>
      </div>
    </div>
  </section>
</template>

<style scoped>
@reference "~/assets/main.css";

.document-link-card {
  @apply typo-body-bold 2xl:ris-subhead-bold flex cursor-pointer flex-row items-center bg-blue-300 p-16 wrap-break-word hyphens-auto text-blue-800 -outline-offset-4 outline-blue-800 hover:bg-blue-500 focus-visible:shadow-[0px_0px_0px_4px_white] focus-visible:outline-4 md:h-128 md:flex-col md:items-start;
}
</style>
