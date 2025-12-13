<script setup lang="ts">
import { Button } from "primevue";
import { NuxtLink } from "#components";
import type { RouteLocationRaw } from "#vue-router";
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

const emit = defineEmits<(e: "updatePage", page: number) => void>();

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
    emit("updatePage", nextPageNumber.value);
  }
}

async function previousPage() {
  if (previousPageNumber.value !== undefined) {
    emit("updatePage", previousPageNumber.value);
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
</script>

<template>
  <slot v-if="props.navigationPosition == 'bottom'" />

  <nav
    v-if="page?.member && page?.member.length && !isLoading"
    aria-label="Paginierung"
    class="flex flex-col items-center"
    :class="{
      'mt-20': props.navigationPosition === 'bottom',
      'mb-20': props.navigationPosition === 'top',
    }"
  >
    <div class="flex w-full items-center">
      <div
        class="relative flex grow flex-wrap items-center justify-between gap-8"
      >
        <Button
          v-if="!isOnlyPage"
          :to="previousPageRoute"
          :as="previousPageRoute ? NuxtLink : undefined"
          aria-label="vorherige Ergebnisse"
          severity="secondary"
          label="Zurück"
          :disabled="!previousPageRoute"
          @click.prevent="previousPage()"
        >
          <template #icon><IconArrowBack /></template>
        </Button>

        <span class="ris-label1-regular only:m-auto">
          <span
            v-if="!isOnlyPage && currentPageIndex !== undefined"
            class="ris-label1-bold"
          >
            Seite {{ currentPageIndex + 1 }}:
          </span>
          <span>{{ itemsOnPage }}</span>
        </span>

        <Button
          v-if="!isOnlyPage"
          :to="nextPageRoute"
          :as="nextPageRoute ? NuxtLink : undefined"
          aria-label="nächste Ergebnisse"
          severity="secondary"
          icon-pos="right"
          label="Weiter"
          :disabled="!nextPageRoute"
          @click.prevent="nextPage()"
        >
          <template #icon="slot">
            <IconArrowForward :class="slot.class" />
          </template>
        </Button>
      </div>
    </div>
  </nav>

  <slot v-if="props.navigationPosition == 'top'" />
</template>
