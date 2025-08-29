<script setup lang="ts">
import Button from "primevue/button";
import InputText from "primevue/inputtext";
import { fetchTranslationList } from "./useTranslationData";
import type { TranslationContent } from "./useTranslationData";
import ContentWrapper from "~/components/CustomLayouts/ContentWrapper.vue";
import type { BreadcrumbItem } from "~/components/Ris/RisBreadcrumb.vue";
import IconSearch from "~icons/ic/search";

const breadcrumbItems: ComputedRef<BreadcrumbItem[]> = computed(() => {
  return [
    {
      label: "Translations",
      route: "/tranlsations",
    },
  ];
});

const { data: translationsList } = fetchTranslationList();

const sortedTranslations = computed<TranslationContent[] | undefined>(() => {
  if (!translationsList.value) return undefined;
  return [...translationsList.value].sort((a, b) =>
    a.name.localeCompare(b.name),
  );
});
</script>

<template>
  <ContentWrapper>
    <div class="container">
      <div class="flex items-center gap-8 print:hidden">
        <RisBreadcrumb type="norm" :items="breadcrumbItems" />
      </div>
      <h1
        class="ris-heading2-bold max-w-title mt-24 mb-48 overflow-x-auto text-balance"
      >
        English translations of German laws and regulations
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
        <form
          :data-full-width="true"
          role="search"
          class="my-48 flex max-w-md flex-row gap-8 data-[full-width='true']:max-w-full"
        >
          <InputField
            id="searchInput"
            label="Suche nach Rechtsinformationen"
            visually-hide-label
          >
            <InputText
              id="searchInput"
              aria-label="Suchbegriff"
              fluid
              placeholder="Enter search term"
              autofocus
              type="search"
            />
          </InputField>
          <Button
            aria-label="Suchen"
            class="h-[3rem] w-[3rem] shrink-0 justify-center"
          >
            <template #icon>
              <IconSearch />
            </template>
          </Button>
        </form>
      </section>

      <a
        v-for="t in sortedTranslations"
        :key="t['@id']"
        :href="`translations/${t['@id']}`"
        class="group my-16 block bg-white p-8 px-32 py-24 no-underline hover:no-underline"
      >
        <div class="max-w-prose space-y-24">
          <span class="mr-24">{{ t["@id"] }}</span>
          <span>{{ t.translationOfWork }}</span>

          <h3
            class="ris-heading3-bold max-w-title mt-8 block text-balance text-blue-800 group-hover:underline"
          >
            {{ t.name }}
          </h3>

          <p class="text-gray-900">{{ t.translator }}</p>
        </div>
      </a>
    </div>
  </ContentWrapper>
</template>
