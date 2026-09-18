<script setup lang="ts">
import IcOutlineInfo from "~icons/ic/outline-info";
import type { DetailsListItem } from "~/components/documents/DetailsList.vue";
import type { MetadataItem } from "~/components/documents/Metadata.vue";
import type { TabView } from "~/components/documents/TabsLayout.vue";
import type { TreeItem } from "~/components/TreeView.vue";
import { useSearchBackLink } from "~/composables/useSearchBackLink";
import { DocumentKind, type Rechtsprechung } from "~/types/api";

definePageMeta({
  layout: false,
  skipLinks: [
    { label: "Zum Inhalt", to: "#main" },
    { label: "Zum Entscheidungstext", to: "#content" },
    { label: "Zum Fußbereich", to: "#footer" },
  ],
});

const route = useRoute();

const documentNumber = route.params.documentNumber?.toString();
if (!documentNumber) throw createError({ status: 404 });

const [
  { data: rechtsprechung, error: metadataError },
  { data: html, error: contentError },
] = await Promise.all([
  useRisBackend<Rechtsprechung>(`/v1/rechtsprechung/${documentNumber}`),
  useRisBackend<string>(`/v1/rechtsprechung/${documentNumber}.html`, {
    headers: { Accept: "text/html" },
  }),
]);

if (metadataError?.value) throw createError(metadataError.value);
if (contentError?.value) throw createError(contentError.value);

const document = computed(() => {
  if (html.value) {
    return parseDocument(html.value);
  }
});

const isEmptyDocument = computed(() => isDocumentEmpty(document.value));

useCaselawSeo({
  rechtsprechung: rechtsprechung.value,
  document: document.value,
});

const verweiseGroups = computed(() =>
  document.value ? getVerweiseGroups(document.value) : [],
);

const shouldRenderVerweise = computed(() => {
  return usePrivateFeaturesFlag() && verweiseGroups.value.length;
});

// Page contents ------------------------------------------

const views = computed<TabView[]>(() => {
  const tabViews = [];

  if (!isEmptyDocument.value) {
    tabViews.push({ path: "text", label: "Text" });
  }

  tabViews.push({
    path: "details",
    label: "Details",
    analyticsId: "caselaw-metadata-tab",
  });

  if (shouldRenderVerweise.value) {
    tabViews.push({
      path: "verweise",
      label: "Verweise",
    });
  }

  return tabViews;
});

const title = computed(() => {
  return rechtsprechung.value?.kurztitel
    ? removeOuterParentheses(rechtsprechung.value?.kurztitel)
    : undefined;
});

const secondaryTitle = computed(() =>
  getCaselawSecondaryTitle(
    {
      decisionNames: rechtsprechung.value?.entscheidungsnamen ?? [],
      titleLine: rechtsprechung.value?.titelzeile,
    },
    false,
  ),
);

const searchBackLink = useSearchBackLink(DocumentKind.CaseLaw);

const breadcrumbs = computed(() => [
  {
    label: searchBackLink.value.label,
    route: searchBackLink.value.route,
  },
  { label: title.value ?? "Titelzeile nicht vorhanden" },
]);

const tocEntries = computed<TreeItem[]>(() => {
  if (!document.value) return [];
  return getAllSectionsFromDocument(document.value, "section").map((entry) => ({
    key: entry.id,
    subtitle: entry.title, // Subtitle for more subtle appearance
    to: { hash: `#${entry.id}`, query: { from: route.query.from } },
  }));
});

const headerMetadata = computed<MetadataItem[]>(() => [
  { type: "text", label: "Gericht", value: rechtsprechung.value?.gericht },
  {
    type: "text",
    label: "Dokumenttyp",
    value: rechtsprechung.value?.dokumenttyp,
  },
  {
    type: "text",
    label: "Entscheidungsdatum",
    value: dateFormattedDDMMYYYY(rechtsprechung.value?.datum),
  },
  {
    type: "badge",
    label: "Aktenzeichen",
    values: rechtsprechung.value?.aktenzeichenListe ?? [],
    color: "gray",
  },
]);

const detailItems = computed<DetailsListItem[]>(() => [
  {
    type: "text",
    label: "Spruchkörper:",
    value: rechtsprechung.value?.spruchkoerper,
  },
  {
    type: "text",
    label: "ECLI:",
    value: rechtsprechung.value?.ecli,
    valueClass: "break-all",
  },
  {
    type: "text",
    label: "Entscheidungsname:",
    value: formatArray(rechtsprechung.value?.entscheidungsnamen ?? []),
  },
  {
    type: "list",
    label: "Gesetzeskraft:",
    values: rechtsprechung.value?.gesetzeskraft ?? [],
  },
  {
    type: "list",
    label: getSingularOrPlural(
      "Streitjahr:",
      "Streitjahre:",
      rechtsprechung.value?.streitjahre?.length,
    ),
    values: rechtsprechung.value?.streitjahre ?? [],
  },
  {
    type: "list",
    label: getSingularOrPlural(
      "Vorgehende Entscheidung:",
      "Vorgehende Entscheidungen:",
      rechtsprechung.value?.vorgehendeEntscheidungen?.length,
    ),
    values: rechtsprechung.value?.vorgehendeEntscheidungen ?? [],
  },
  {
    type: "list",
    label: getSingularOrPlural(
      "Nachgehende Entscheidung:",
      "Nachgehende Entscheidungen:",
      rechtsprechung.value?.nachgehendeEntscheidungen?.length,
    ),
    values: rechtsprechung.value?.nachgehendeEntscheidungen ?? [],
  },

  {
    type: "link",
    label: "Download:",
    url: getEncodingURL(rechtsprechung.value?.encoding, "application/zip"),
    text: "Diese Gerichtsentscheidung als ZIP herunterladen",
    dataAttr: "xml-zip-view",
  },
]);

const textSectionId = useId();
const detailsSectionId = useId();
const verweiseSectionId = useId();
</script>

<template>
  <NuxtLayout
    name="document"
    :breadcrumbs
    :metadata="headerMetadata"
    :secondary-title
    :title
    :views
  >
    <template #actionMenu>
      <DocumentsActionMenuCaseLawActionMenu :rechtsprechung class="mb-auto" />
    </template>

    <template #message>
      <UiMessage
        v-if="rechtsprechung?.vorabdokument"
        severity="info"
        class="typo-body-regular my-24 bg-white sm:my-32 md:my-40"
      >
        <template #icon>
          <IcOutlineInfo class="text-blue-800" />
        </template>
        <p>
          Die Metadaten dieser Gerichtsentscheidung wurden bereits
          veröffentlicht. Der Entscheidungstext ist derzeit noch nicht
          verfügbar, wird aber in Kürze ergänzt.
        </p>
      </UiMessage>
    </template>

    <template #details>
      <section
        role="tabpanel"
        :aria-labelledby="detailsSectionId"
        class="pt-32 pb-32 md:pb-56"
      >
        <h2 :id="detailsSectionId" class="typo-headline3-bold">Details</h2>
        <DocumentsIncompleteDataMessage class="my-24" />
        <DocumentsDetailsList :items="detailItems" />
      </section>
    </template>

    <template #text>
      <SidebarLayout>
        <section role="tabpanel" :aria-labelledby="textSectionId">
          <h2 :id="textSectionId" class="sr-only">Text</h2>
          <DocumentsIncompleteDataMessage class="my-24" />
          <div
            v-if="document"
            class="case-law"
            v-html="document.body.innerHTML"
          ></div>
        </section>

        <template #sidebar v-if="tocEntries.length">
          <client-only>
            <DocumentsTableOfContents :table-of-contents="tocEntries" />
          </client-only>
        </template>
      </SidebarLayout>
    </template>

    <template #verweise>
      <section
        v-if="shouldRenderVerweise"
        role="tabpanel"
        :aria-labelledby="verweiseSectionId"
        class="pt-32 pb-32 md:pb-56"
      >
        <h2 :id="verweiseSectionId" class="typo-headline3-bold">Verweise</h2>
        <div class="verweise">
          <div v-for="group in verweiseGroups" :key="group.id">
            <h3 class="typo-body-bold pt-16 pb-8">
              {{ group.label }}
            </h3>
            <div v-html="group.html" />
          </div>
        </div>
      </section>
    </template>
  </NuxtLayout>
</template>

<style scoped>
@reference "~/assets/main.css";

:deep(.verweise) {
  a {
    @apply typo-link1-regular;
  }

  ul {
    @apply mb-16;
  }

  li {
    @apply typo-label1-regular;
  }
}

.case-law {
  --border-number-min-width: 3rem;
}

:deep(.case-law table[border="1"] :is(th, td)) {
  @apply border border-solid border-black px-4;
}

:deep(.case-law table) {
  @apply inline-block max-w-full overflow-x-auto text-sm;

  td,
  th {
    @apply p-4 align-top;
  }
}

:deep(.case-law h2) {
  @apply typo-headline2-bold my-24 inline-block;
}

:deep(.case-law .border-number) {
  @apply flex items-start;
}

:deep(.case-law .border-number .number) {
  @apply mr-8 text-gray-900;
  min-width: calc(var(--border-number-min-width) - 0.5rem);
}

:deep(.case-law .border-number .content) {
  @apply min-w-0 flex-1 wrap-break-word hyphens-auto;
}

:deep(.case-law .border-number-link) {
  @apply typo-link1-regular pl-[0.25ch];
}

:deep(.case-law section > p) {
  @apply ml-(--border-number-min-width);
}

:deep(.case-law ul) {
  @apply mb-16 list-outside list-disc pl-24;
}

:deep(.case-law ul ul) {
  @apply mb-0 list-[circle];
}

:deep(.case-law ul ul ul) {
  @apply list-[square];
}

:deep(.case-law ul > li > p:last-child) {
  @apply mb-0;
}

/* nesting is often expressed as a list item that only wraps another list,
   which should show the nested marker instead of one of its own */
:deep(.case-law li:has(> ul:only-child)) {
  @apply list-none;
}

:deep(.case-law section > ul) {
  @apply ml-(--border-number-min-width);
}

:deep(.case-law #gliederung blockquote) {
  @apply ml-32;
}

:deep(.case-law #gruende blockquote) {
  @apply ml-(--border-number-min-width) border-l-2 border-gray-700 pl-16 sm:ml-[calc(var(--border-number-min-width)+2rem)];

  & + * {
    @apply mt-16;
  }

  & + blockquote {
    @apply mt-0;
  }
}

:deep(.case-law #title) {
  @apply hidden;
}

:deep(.case-law p) {
  @apply mb-16 wrap-break-word hyphens-auto;
  unicode-bidi: isolate;
}
</style>
