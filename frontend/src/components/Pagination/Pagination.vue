<script setup lang="ts">
import IconArrowBack from "~icons/ic/baseline-arrow-back";
import IconArrowForward from "~icons/ic/baseline-arrow-forward";
import Button from "primevue/button";
import {
  buildItemsOnPageString,
  parsePageNumber,
} from "~/utils/paginationUtils";
import type { Page } from "~/components/Pagination/Pagination";

const props = withDefaults(
  defineProps<{
    page?: Page | null;
    navigationPosition?: "top" | "bottom";
    isLoading?: boolean;
  }>(),
  { page: undefined, navigationPosition: "top", isLoading: false },
);

const emits = defineEmits<(e: "updatePage", page: number) => void>();

async function nextPage() {
  if (props.page?.view.next) {
    const { page } = parsePageNumber(props.page.view.next);
    if (page !== undefined) emits("updatePage", page);
  }
}

async function previousPage() {
  if (props.page?.view.previous) {
    const { page } = parsePageNumber(props.page.view.previous);
    if (page !== undefined) emits("updatePage", page);
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

const userInputDisabled = ref(true);
onNuxtReady(() => {
  userInputDisabled.value = false;
});
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
          v-if="!isOnlyPage"
          aria-label="vorherige Ergebnisse"
          severity="secondary"
          :disabled="!page?.view.previous || userInputDisabled"
          icon-position="left"
          label="Zurück"
          @click="previousPage"
          @keydown.enter="previousPage"
          ><template #icon><IconArrowBack /></template
        ></Button>
        <span class="only:m-auto">
          <b v-if="!isOnlyPage && currentPageIndex !== undefined"
            >Seite {{ currentPageIndex + 1 }}:
          </b>
          <span> {{ itemsOnPage }}</span>
        </span>
        <Button
          v-if="!isOnlyPage"
          aria-label="nächste Ergebnisse"
          severity="secondary"
          :disabled="!page?.view.next || userInputDisabled"
          icon-pos="right"
          label="Weiter"
          @click="nextPage"
          @keydown.enter="nextPage"
          ><template #icon><IconArrowForward /></template
        ></Button>
      </div>
    </div>
  </div>
  <slot v-if="props.navigationPosition == 'top'" />
</template>
