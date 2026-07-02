<script setup lang="ts">
import MiniSearch from "minisearch";
import type { BreadcrumbItem } from "~/components/Breadcrumbs.vue";

definePageMeta({
  skipLinks: [
    { label: "Skip to main", to: "#main" },
    { label: "Skip to footer", to: "#footer" },
  ],
  layout: false,
});

useHead({
  htmlAttrs: { lang: "en" },
});

const activeSearchTerm = ref("");

const breadcrumbItems: ComputedRef<BreadcrumbItem[]> = computed(() => {
  return [
    {
      label: "English translations",
      route: "/translations",
    },
  ];
});

const { translations } = await fetchTranslationList();

const translationsMap = computed(() => {
  const map = new Map<string, TranslationContent>();
  if (translations.value) {
    for (const t of translations.value) {
      map.set(t["@id"], t);
    }
  }
  return map;
});

const sortedTranslations = computed<TranslationContent[] | null>(() => {
  const translationList = translations.value;
  const searchTerm = activeSearchTerm.value.trim();

  if (!translationList) return null;

  if (!searchTerm) {
    return [...translationList].toSorted((a, b) =>
      a.name.localeCompare(b.name),
    );
  }

  return minisearch.value
    .search(searchTerm, { prefix: true, fuzzy: 0.2, boost: { "@id": 2 } })
    .map((result) => translationsMap.value.get(result.id))
    .filter((doc): doc is TranslationContent => Boolean(doc));
});

const minisearch = computed(() => {
  const miniSearch = new MiniSearch({
    fields: ["@id", "name", "translationOfWork"],
    storeFields: [
      "@id",
      "name",
      "inLanguage",
      "translator",
      "translationOfWork",
      "about",
      "ris:filename",
    ],
    idField: "@id",
  });
  miniSearch.addAll(translations.value ?? []);
  return miniSearch;
});

useSeo({
  title: "English Translations of German Federal Laws and Regulations",
  description:
    "Access official English translations of selected German laws and regulations. These translations are for informational purposes only and are not legally binding.",
});

const translationsListId = useId();
</script>

<template>
  <NuxtLayout name="breadcrumb-page">
    <template #breadcrumb>
      <div class="print:hidden">
        <Breadcrumbs :items="breadcrumbItems" />
      </div>
    </template>

    <div class="content-wrapper content-grid pb-32 md:pb-56">
      <section class="col-span-12 grid grid-cols-subgrid">
        <h1 class="typo-headline1-bold col-span-12 mb-8">
          English Translations of German Federal Laws and Regulations
        </h1>
        <div class="content-grid-textblock">
          <p class="mb-16">
            We provide translations of our German content to help you. Please
            note that the original German versions are the only authoritative
            source.
          </p>
          <p class="mb-16">
            The translations published on this website may be used in accordance
            with the applicable copyright exceptions. In particular, single
            copies may be made including in the form of downloads or printouts
            for private, non-commercial use. Any reproduction, processing,
            distribution or other type of use of these translations that does
            not fall within the relevant copyright exceptions requires the prior
            consent of the author or other rights holder.
          </p>
          <SearchSimpleSearchInput
            v-model="activeSearchTerm"
            class="my-48"
            input-label="Search term"
            input-placeholder="Search by title or abbreviation"
            submit-label="Search"
            full-width
          />
        </div>
      </section>

      <section
        :aria-labelledby="translationsListId"
        class="col-span-12 xl:col-span-8"
      >
        <h2 :id="translationsListId" class="sr-only">Translations List</h2>

        <ul
          v-if="sortedTranslations !== null && sortedTranslations.length > 0"
          class="flex flex-col gap-16"
        >
          <li
            v-for="t in sortedTranslations"
            :key="t['@id']"
            class="bg-white p-16 sm:py-24 md:px-24 lg:px-32"
          >
            <div class="flex flex-col gap-8">
              <SearchResultHeader
                lang="de"
                :items="[
                  { value: t['@id'] },
                  { value: t.translationOfWork ?? '' },
                ]"
              />

              <NuxtLink
                :to="{ name: 'translations-id', params: { id: t['@id'] } }"
                class="typo-headline-searchresult"
              >
                <h2>{{ t.name }}</h2>
              </NuxtLink>

              <p class="typo-body-regular text-gray-900">
                {{ t.translator }}
              </p>
            </div>
          </li>
        </ul>

        <div v-else class="mt-8">
          <p class="typo-body-bold">We didn’t find anything.</p>
          <p class="mb-16">
            Try checking the spelling or using a different title.
          </p>
        </div>
      </section>
    </div>
  </NuxtLayout>
</template>
