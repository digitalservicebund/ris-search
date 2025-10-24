<script setup lang="ts">
import type { AnyDocument, SearchResult } from "~/types";
import { buildItemsOnPageString, parsePageNumber } from "~/utils/pagination";
import IconArrowBack from "~icons/ic/baseline-arrow-back";
import IconArrowForward from "~icons/ic/baseline-arrow-forward";

export interface PartialCollectionView {
  first?: string;
  previous?: string;
  next?: string;
  last?: string;
}

export type Page = {
  member: SearchResult<AnyDocument>[];
  "@id": string;
  totalItems?: number;
  view: PartialCollectionView;
};

const props = withDefaults(
  defineProps<{
    page?: Page | null;
    navigationPosition?: "top" | "bottom";
    isLoading?: boolean;
  }>(),
  { page: undefined, navigationPosition: "top", isLoading: false },
);

const emits = defineEmits<(e: "updatePage", page: number) => void>();
const route = useRoute();

const previousPageNumber = computed(() => {
  if (!props.page?.view.previous) return undefined;
  return parsePageNumber(props.page.view.previous).page;
});

const nextPageNumber = computed(() => {
  if (!props.page?.view.next) return undefined;
  return parsePageNumber(props.page.view.next).page;
});

const previousPageUrl = computed(() => {
  if (previousPageNumber.value === undefined) return undefined;
  const query = { ...route.query };
  if (previousPageNumber.value === 0) {
    delete query.pageNumber;
  } else {
    query.pageNumber = previousPageNumber.value.toString();
  }
  return `${route.path}?${new URLSearchParams(query as Record<string, string>).toString()}`;
});

const nextPageUrl = computed(() => {
  if (nextPageNumber.value === undefined) return undefined;
  const query = { ...route.query };
  if (nextPageNumber.value === 0) {
    delete query.pageNumber;
  } else {
    query.pageNumber = nextPageNumber.value.toString();
  }
  return `${route.path}?${new URLSearchParams(query as Record<string, string>).toString()}`;
});

async function nextPage() {
  if (nextPageNumber.value !== undefined) {
    emits("updatePage", nextPageNumber.value);
  }
}

async function previousPage() {
  if (previousPageNumber.value !== undefined) {
    emits("updatePage", previousPageNumber.value);
  }
}

const currentPageIndex = computed(() => {
  if (!props.page?.["@id"]) return undefined;
  return parsePageNumber(props.page["@id"]).page;
});
const isOnlyPage = computed(
  () => !(props.page?.view.previous || props.page?.view.next),
);

const itemsOnPage = computed(() => buildItemsOnPageString(props.page));

const paginationLinkBase =
  "ris-body2-bold relative inline-flex h-48 max-w-full items-center justify-center gap-8 border-2 bg-white px-16 py-4 text-center";

const paginationLinkEnabled = `${paginationLinkBase} border-blue-800 text-blue-800 no-underline outline-blue-800 outline-0 outline-offset-4 hover:bg-gray-200 focus:bg-gray-200 focus-visible:outline-4 active:border-white active:bg-white cursor-pointer`;

const paginationLinkDisabled = `${paginationLinkBase} border-blue-500 text-blue-500 cursor-not-allowed`;
</script>

<template>
  <slot v-if="props.navigationPosition == 'bottom'" />
  <div
    v-if="page?.member && page?.member.length && !isLoading"
    class="my-32 mt-20 mb-10 flex flex-col items-center"
  >
    <div class="flex w-full items-center">
      <div
        class="relative flex grow flex-wrap items-center justify-between gap-8"
      >
        <a
          v-if="!isOnlyPage && page?.view.previous"
          :href="previousPageUrl"
          aria-label="vorherige Ergebnisse"
          :class="paginationLinkEnabled"
          @click.prevent="previousPage"
        >
          <IconArrowBack aria-hidden="true" />
          Zurück
        </a>
        <span
          v-else-if="!isOnlyPage"
          aria-disabled="true"
          aria-label="vorherige Ergebnisse"
          :class="paginationLinkDisabled"
        >
          <IconArrowBack aria-hidden="true" />
          Zurück
        </span>
        <span class="only:m-auto">
          <b v-if="!isOnlyPage && currentPageIndex !== undefined"
            >Seite {{ currentPageIndex + 1 }}:
          </b>
          <span> {{ itemsOnPage }}</span>
        </span>
        <a
          v-if="!isOnlyPage && page?.view.next"
          :href="nextPageUrl"
          aria-label="nächste Ergebnisse"
          :class="paginationLinkEnabled"
          @click.prevent="nextPage"
        >
          Weiter
          <IconArrowForward aria-hidden="true" />
        </a>
        <span
          v-else-if="!isOnlyPage"
          aria-disabled="true"
          aria-label="nächste Ergebnisse"
          :class="paginationLinkDisabled"
        >
          Weiter
          <IconArrowForward aria-hidden="true" />
        </span>
      </div>
    </div>
  </div>
  <slot v-if="props.navigationPosition == 'top'" />
</template>
