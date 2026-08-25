<script setup lang="ts">
import IcBaselineArrowForward from "~icons/ic/baseline-arrow-forward";
import { DocumentKind } from "~/types/api";

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
        <div class="col-span-12 md:col-span-8 2xl:col-span-6 2xl:col-start-2">
          <h1
            class="typo-headline1-bold mb-8 wrap-break-word hyphens-auto md:mb-16 2xl:mb-24"
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

    <nav class="content-wrapper content-grid mb-8 2xl:mb-16">
      <ul
        class="lg-gap-x-24 col-span-12 -mt-24 flex flex-col gap-8 md:-mt-48 md:flex-row md:gap-x-16 2xl:col-span-10 2xl:col-start-2"
      >
        <li class="flex-1">
          <NuxtLink
            class="document-link-card"
            :to="{
              name: 'suche',
              query: { documentKind: DocumentKind.Norm },
            }"
          >
            Gesetze und Verordnungen
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
            Gerichtsentscheidungen
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
            Verwaltungsvorschriften
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
            Literaturnachweise
            <IcBaselineArrowForward class="mt-auto ml-auto" />
          </NuxtLink>
        </li>
      </ul>
    </nav>
  </section>
</template>

<style scoped>
@reference "~/assets/main.css";

.document-link-card {
  @apply typo-body-bold 2xl:ris-subhead-bold hover:bg-blue-500 flex cursor-pointer flex-row items-center bg-blue-300 p-16 wrap-break-word hyphens-auto text-blue-800 -outline-offset-4 outline-blue-800 focus-visible:shadow-[0px_0px_0px_4px_white] focus-visible:outline-4 md:h-128 md:flex-col md:items-start;
}
</style>
