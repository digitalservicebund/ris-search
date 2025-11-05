<script setup lang="ts">
import { Button } from "primevue";
import type { RouteLocationRaw } from "vue-router";
import { NuxtLink } from "#components";
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

const previousPageRoute = computed<RouteLocationRaw | undefined>(() => {
  if (previousPageNumber.value === undefined) return undefined;
  const query = { ...route.query };
  if (previousPageNumber.value === 0) {
    delete query.pageNumber;
  } else {
    query.pageNumber = previousPageNumber.value.toString();
  }
  return {
    path: route.path,
    query,
  };
});

const nextPageRoute = computed<RouteLocationRaw | undefined>(() => {
  if (nextPageNumber.value === undefined) return undefined;
  const query = { ...route.query };
  if (nextPageNumber.value === 0) {
    delete query.pageNumber;
  } else {
    query.pageNumber = nextPageNumber.value.toString();
  }
  return {
    path: route.path,
    query,
  };
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

const paginationLinkDisabled =
  "ris-body2-bold relative inline-flex h-48 max-w-full items-center justify-center gap-8 border-2 border-blue-500 text-blue-500 cursor-not-allowed bg-white px-16 py-4 text-center";
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
        <Button
          v-if="!isOnlyPage && page?.view.previous"
          :to="previousPageRoute"
          :as="NuxtLink"
          aria-label="vorherige Ergebnisse"
          severity="secondary"
          icon-position="left"
          label="Zurück"
          @click="previousPage"
        >
          <template #icon><IconArrowBack /></template>
        </Button>
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
        <Button
          v-if="!isOnlyPage && page?.view.next"
          :to="nextPageRoute"
          :as="NuxtLink"
          aria-label="nächste Ergebnisse"
          severity="secondary"
          icon-position="left"
          label="Weiter"
          @click="nextPage"
        >
          <template #icon><IconArrowForward /></template>
        </Button>
        <span
          v-else-if="!isOnlyPage"
          aria-disabled="true"
          aria-label="nächste Ergebnisse"
          :class="paginationLinkDisabled"
        >
          <IconArrowForward aria-hidden="true" />
          Weiter
        </span>
      </div>
    </div>
  </div>
  <slot v-if="props.navigationPosition == 'top'" />
</template>
