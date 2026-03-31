<script setup lang="ts">
import Button from "primevue/button";
import type { TreeItem } from "~/components/TreeView.vue";
import IcBaselineClose from "~icons/ic/baseline-close";
import IcBaselineFormatListBulleted from "~icons/ic/baseline-format-list-bulleted";

interface Props {
  tableOfContents: TreeItem[];
  selectedKey?: string;
  subheading?: string;
}

const props = defineProps<Props>();

const isTocVisible = ref(false);

const responsiveStyles =
  "z-10 max-lg:fixed max-lg:left-0 max-lg:top-0 max-lg:h-full max-lg:w-full max-lg:bg-gray-100 max-lg:px-32 max-lg:py-16";

function toggleTableOfContents() {
  isTocVisible.value = !isTocVisible.value;
}

function hideTableOfContents() {
  isTocVisible.value = false;
}
</script>

<template>
  <Button
    v-if="!isTocVisible"
    class="visible mt-16 w-full lg:hidden"
    data-testid="mobile-toc-button"
    severity="secondary"
    @click="toggleTableOfContents"
  >
    <IcBaselineFormatListBulleted />
    Inhaltsverzeichnis
  </Button>
  <div
    :data-selected="isTocVisible"
    :class="responsiveStyles"
    class="flex h-full flex-col max-lg:data-[selected=false]:hidden max-lg:data-[selected=true]:flex"
    data-testid="table-of-contents"
  >
    <div class="flex flex-row items-center justify-between">
      <Button
        class="visible bg-transparent hover:bg-transparent lg:hidden"
        aria-label="Inhaltsverzeichnis schließen"
        @click="toggleTableOfContents"
      >
        <IcBaselineClose class="text-gray-900 hover:text-black" />
      </Button>
    </div>

    <TreeView
      :items="tableOfContents"
      :selected="selectedKey"
      :expand-to-key="selectedKey"
      :selection-enabled="!!selectedKey"
      :subheading="subheading"
      heading="Inhalte"
      class="h-full lg:pt-16"
      @click="hideTableOfContents"
    />
  </div>
</template>
