<script setup lang="ts">
import MiniSearch from "minisearch";
import ContentWrapper from "~/components/CustomLayouts/ContentWrapper.vue";
import type { BreadcrumbItem } from "~/components/Ris/RisBreadcrumb.vue";
import SimpleSearchInput from "~/components/Search/SimpleSearchInput.vue";
import { useStaticPageSeo } from "~/composables/useStaticPageSeo";
import type { TranslationContent } from "~/composables/useTranslationDetailData";
import { useTranslationListData } from "~/composables/useTranslationListData";
import { DocumentKind } from "~/types";
import { formatDocumentKind } from "~/utils/displayValues";

const activeSearchTerm = ref("");

const breadcrumbItems: ComputedRef<BreadcrumbItem[]> = computed(() => {
  return [
    {
      label: formatDocumentKind(DocumentKind.Norm),
      route: `/search?category=${DocumentKind.Norm}`,
    },
    {
      label: "Translations",
      route: "/tranlsations",
    },
  ];
});

const { translations } = await useTranslationListData();

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
  if (!translations.value) {
    return null;
  }

  let results: TranslationContent[] = [];

  if (activeSearchTerm.value === "") {
    results = [...translations.value];
  } else {
    results = minisearch.value
      .search(activeSearchTerm.value, { prefix: true, fuzzy: 0.2 })
      .map((r) => translationsMap.value.get(r.id))
      .filter((doc): doc is TranslationContent => !!doc);
  }

  return results.sort((a, b) => a.name.localeCompare(b.name));
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

useStaticPageSeo("translations-list");
</script>

<template>
  <ContentWrapper>
    <div class="container">
      <div class="flex items-center gap-8 print:hidden">
        <RisBreadcrumb :items="breadcrumbItems" />
      </div>
      <h1
        class="ris-heading2-bold max-w-title mt-24 mb-48 overflow-x-auto text-balance"
      >
        English Translations of German Federal Laws and Regulations
      </h1>
      <section class="max-w-prose space-y-24">
        <p>
          We provide translations of our German content to help you. Please note
          that the original German versions are the only authoritative source.
        </p>
        <p>
          The translations published on this website may be used in accordance
          with the applicable copyright exceptions. In particular, single copies
          may be made including in the form of downloads or printouts for
          private, non-commercial use. Any reproduction, processing,
          distribution or other type of use of these translations that does not
          fall within the relevant copyright exceptions requires the prior
          consent of the author or other rights holder.
        </p>
        <SimpleSearchInput
          v-model="activeSearchTerm"
          class="mt-48"
          input-label="Search term"
          input-placeholder="Search by title or abbreviation"
          submit-label="Search"
        />
      </section>

      <section aria-labelledby="translations-list" class="mt-48">
        <h2 id="translations-list" class="sr-only">Translations List</h2>

        <ul
          v-if="sortedTranslations !== null && sortedTranslations.length > 0"
          class="mt-48"
        >
          <li v-for="t in sortedTranslations" :key="t['@id']">
            <a
              :href="`/translations/${t['@id']}`"
              class="group my-16 block border-4 border-transparent bg-white p-8 px-32 py-24 no-underline hover:text-blue-800 hover:no-underline focus:border-blue-800"
            >
              <div class="max-w-prose space-y-24">
                <span class="mr-24 group-hover:underline">{{ t["@id"] }}</span>
                <span class="group-hover:underline">{{
                  t.translationOfWork
                }}</span>

                <h3
                  class="ris-heading3-bold max-w-title mt-8 block text-balance text-blue-800 group-hover:underline"
                >
                  {{ t.name }}
                </h3>

                <p class="text-gray-900">{{ t.translator }}</p>
              </div>
            </a>
          </li>
        </ul>
        <div v-else class="mt-8">
          <p class="font-bold">We didn’t find anything.</p>
          <p class="mb-16">
            Try checking the spelling or using a different title.
          </p>
        </div>
      </section>
    </div>
  </ContentWrapper>
</template>
