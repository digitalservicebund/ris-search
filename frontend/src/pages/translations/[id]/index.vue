<script setup lang="ts">
import Message from "primevue/message";
import Tab from "primevue/tab";
import TabList from "primevue/tablist";
import TabPanel from "primevue/tabpanel";
import TabPanels from "primevue/tabpanels";
import Tabs from "primevue/tabs";
import { computed } from "vue";
import ActionsMenu from "~/components/ActionMenu/ActionsMenu.vue";
import ContentWrapper from "~/components/CustomLayouts/ContentWrapper.vue";
import Properties from "~/components/Properties.vue";
import PropertiesItem from "~/components/PropertiesItem.vue";
import type { BreadcrumbItem } from "~/components/Ris/RisBreadcrumb.vue";
import {
  tabListStyles,
  tabPanelStyles,
  tabStyles,
} from "~/components/Tabs.styles";
import { fetchTranslationAndHTML } from "~/composables/useTranslationData";
import { removePrefix, truncateAtWord } from "~/utils/textFormatting";
import IcBaselineSubject from "~icons/ic/baseline-subject";
import IcOutlineInfo from "~icons/ic/outline-info";

definePageMeta({ layout: "base" });

const route = useRoute();
const id = route.params.id as string;

const { data } = await fetchTranslationAndHTML(id);
const currentTranslation = data.value?.content;
const html = data.value?.html;

const link = {
  url: globalThis?.location?.href,
  label: "Link to translation",
};

const permalink = {
  url: globalThis?.location?.href,
  label: "Link to translation",
};

const versionInformation = computed(() => {
  return removePrefix(currentTranslation.about, "Version information:");
});

const translatedBy = computed(() => {
  return removePrefix(currentTranslation.translator, "Translation provided by");
});

const breadcrumbItems = computed(() => {
  const items: BreadcrumbItem[] = [
    {
      label: "Translations",
      route: "/translations",
    },
  ];
  if (currentTranslation) {
    items.push({
      label: currentTranslation["@id"],
      route: "/translations",
    });
  }
  return items;
});

const buildOgForTranslation = (
  name?: string,
  translationOfWork?: string,
): { title?: string; description?: string } => {
  const base = name?.trim() || translationOfWork?.trim();
  if (!base) return {};

  const title = truncateAtWord(`${base} – English Translation`, 55);

  const description = truncateAtWord(
    `This is the English translation of the ${base}, provided by the German Federal Legal Information Portal. ` +
      `This translation is for informational purposes only. The German version is the only legally binding text.`,
    150,
  );

  return { title, description };
};

const translationSeo = computed(() =>
  currentTranslation
    ? buildOgForTranslation(
        currentTranslation?.name,
        currentTranslation?.translationOfWork,
      )
    : {},
);

const title = computed(() => translationSeo.value.title);
const description = computed(() => translationSeo.value.description);
const url = useRequestURL();

const meta = computed(() =>
  [
    { name: "description", content: description.value },
    { property: "og:type", content: "article" },
    { property: "og:title", content: title.value },
    { property: "og:description", content: description.value },
    { property: "og:url", content: url.href },
    { name: "twitter:title", content: title.value },
    { name: "twitter:description", content: description.value },
  ].filter(
    (tag) => typeof tag.content === "string" && tag.content.trim() !== "",
  ),
);

useHead({
  title,
  link: [{ rel: "canonical", href: url.href }],
  meta,
});
</script>

<template>
  <ContentWrapper border>
    <div v-if="currentTranslation" class="container">
      <div class="flex items-center gap-8 print:hidden">
        <RisBreadcrumb type="norm" :items="breadcrumbItems" class="grow" />
        <client-only>
          <ActionsMenu :link="link" :permalink="permalink" />
        </client-only>
      </div>

      <div class="dokumentenkopf mt-24 mb-48 max-w-prose">
        <hgroup>
          <p
            v-if="currentTranslation?.translationOfWork"
            class="word-wrap ris-heading3-regular mb-12 hyphens-auto"
          >
            {{ currentTranslation.translationOfWork }}
          </p>

          <h1
            v-if="currentTranslation?.name"
            class="ris-heading2-bold max-w-title mb-48 overflow-x-auto text-balance"
          >
            {{ currentTranslation.name }}
          </h1>
        </hgroup>
      </div>
      <Message :closable="false" class="mb-48 max-w-prose space-y-24"
        >TO DO</Message
      >
    </div>
    <Tabs value="0">
      <TabList :pt="tabListStyles">
        <Tab
          class="flex items-center gap-8"
          :pt="tabStyles"
          value="0"
          aria-label="Text of the translation"
        >
          <IcBaselineSubject />Text
        </Tab>
        <Tab
          data-attr="translation-metadata-tab"
          class="flex items-center gap-8"
          :pt="tabStyles"
          value="1"
          aria-label="Details of the translation"
        >
          <IcOutlineInfo />Details
        </Tab>
      </TabList>
      <TabPanels>
        <TabPanel value="0" :pt="tabPanelStyles">
          <div class="container">
            <main class="max-w-prose" v-html="html"></main>
          </div>
        </TabPanel>
        <TabPanel value="1" :pt="tabPanelStyles">
          <section aria-labelledby="detailsTabPanelTitle" class="container">
            <h2 id="detailsTabPanelTitle" class="ris-heading3-bold my-24">
              Details
            </h2>
            <Properties>
              <PropertiesItem
                label="Translation provided by:"
                :value="translatedBy"
              />
              <PropertiesItem
                label="Version information:"
                :value="versionInformation"
              />
            </Properties>
          </section>
        </TabPanel>
      </TabPanels>
    </Tabs>
  </ContentWrapper>
</template>
