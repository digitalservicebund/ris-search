<script setup lang="ts">
import { useRoute } from "#app";
import type { RouteQueryAndHash } from "#vue-router";
import IcBaselineFormatListBulleted from "~icons/ic/baseline-format-list-bulleted";
import IcBaselineGavel from "~icons/ic/baseline-gavel";
import IcBaselineNotes from "~icons/ic/baseline-notes";
import IcBaselineShortText from "~icons/ic/baseline-short-text";
import IcBaselineSubject from "~icons/ic/baseline-subject";
import IcOutlineFactCheck from "~icons/ic/outline-fact-check";

export type TableOfContentsEntry = {
  id: string;
  title: string;
};

const props = defineProps<{
  tableOfContentEntries: TableOfContentsEntry[];
}>();

const selectedEntry = ref<string | null>(null);
let lastScrollY = 0;
const visibleIds = ref<string[]>([]);

const route = useRoute();
const initialLoad = ref(true);

function selectEntry(id: string) {
  selectedEntry.value = id;
}

function getIcon(title: string) {
  switch (true) {
    case title.includes("Orientierungssatz"):
      return IcBaselineShortText;
    case title.includes("Leitsatz"):
      return IcBaselineSubject;
    case title.includes("Tenor"):
      return IcBaselineGavel;
    case title.includes("Tatbestand"):
      return IcOutlineFactCheck;
    case title.includes("Entscheidungsgründe"):
      return IcBaselineFormatListBulleted;
    default:
      return IcBaselineNotes;
  }
}

function handleIntersection(entries: IntersectionObserverEntry[]) {
  const currentScrollY = window.scrollY;
  const scrollingDown = currentScrollY > lastScrollY;
  lastScrollY = currentScrollY;
  entries.forEach((entry) => {
    if (entry.isIntersecting) {
      if (scrollingDown) {
        visibleIds.value = [...visibleIds.value, entry.target.id];
      } else {
        visibleIds.value = [entry.target.id, ...visibleIds.value];
      }
    } else {
      visibleIds.value =
        visibleIds.value?.filter((id) => id !== entry.target.id) ?? [];
    }
  });
  const orderedVisibleSections = props.tableOfContentEntries.filter((entry) =>
    visibleIds.value.includes(entry.id),
  );
  // prevent overriding the entry selected based on the hash, only set it if none is selected
  if (!initialLoad.value || !selectedEntry.value) {
    selectedEntry.value = orderedVisibleSections[0]?.id ?? null;
  }
  initialLoad.value = false;
}

onMounted(() => {
  lastScrollY = window.scrollY;
  const observer = new IntersectionObserver(handleIntersection, {
    threshold: 0,
  });

  props.tableOfContentEntries.forEach((entry) => {
    const element = document.getElementById(entry.id);
    if (element) {
      observer.observe(element);
    }
  });

  const { hash } = route as RouteQueryAndHash;
  if (hash) {
    selectedEntry.value = hash.substring(1); // drop leading #
  }
});
</script>

<template>
  <nav class="w-full" aria-labelledby="toc-header">
    <div
      id="toc-header"
      class="ris-heading3-bold lg:ris-subhead-regular my-16 inline-block"
    >
      Seiteninhalte
    </div>
    <div>
      <div v-for="entry in tableOfContentEntries" :key="entry.id">
        <NuxtLink
          :aria-current="selectedEntry === entry.id ? 'section' : null"
          replace
          :to="`#${entry.id}`"
          class="flex flex-row items-center space-x-12 py-8 text-blue-800 underline hover:text-black hover:underline aria-current-section:bg-blue-300 aria-current-section:text-black aria-current-section:hover:bg-blue-300 lg:px-24 lg:py-20 lg:no-underline lg:hover:bg-blue-200 lg:aria-current-section:border-l-4 lg:aria-current-section:border-blue-900 lg:aria-current-section:bg-blue-200 lg:aria-current-section:pl-20"
          @click="selectEntry(entry.id)"
        >
          <component :is="getIcon(entry.title)"></component>
          <p
            :data-selected="selectedEntry === entry.id"
            class="ris-subhead-regular lg:data-[selected=true]:font-bold"
          >
            {{ entry.title }}
          </p>
        </NuxtLink>
      </div>
    </div>
  </nav>
</template>
