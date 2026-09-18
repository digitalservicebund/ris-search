<script setup lang="ts">
import { Drawer } from "primevue";
import IcBaselineArrowForward from "~icons/ic/baseline-arrow-Forward";
import IcBaselineList from "~icons/ic/baseline-list";
import type { RouteLocationRaw } from "#vue-router";
import type { TreeItem } from "~/components/TreeView.vue";

interface Props {
  tableOfContents: TreeItem[];
  selectedKey?: string;
  subheading?: string;
  subheadingTo?: RouteLocationRaw;
  subheadingAddition?: string;
}

const props = defineProps<Props>();

const desktopSubheading = computed(() => {
  if (props.subheading && props.subheadingAddition) {
    return props.subheading + " " + props.subheadingAddition;
  } else if (props.subheading) {
    return props.subheading;
  } else {
    return undefined;
  }
});

const {
  visible: mobileTocVisible,
  // @ts-expect-error -- usage in template not detected
  triggerRef: openButtonRef,
  closeButtonProps,
} = useDrawer();

const drawerId = useId();
</script>

<template>
  <!-- mobile closed toc -->
  <Transition
    enter-active-class="transition-transform duration-300 ease-in-out delay-150"
    enter-from-class="translate-y-full"
    leave-active-class="transition-transform duration-150 ease-in-out"
    leave-to-class="translate-y-full"
  >
    <button
      v-if="!mobileTocVisible"
      type="button"
      ref="openButtonRef"
      class="shadow-gray-1000/15 fixed inset-x-0 bottom-0 z-10 flex cursor-pointer items-center justify-between gap-8 bg-white p-16 text-left shadow-[0_0_0.5rem] -outline-offset-4 outline-blue-800 focus-visible:outline-4 md:hidden"
      data-back-to-top-adjust="drawer"
      :aria-expanded="mobileTocVisible"
      :aria-controls="drawerId"
      @click="mobileTocVisible = true"
    >
      <div class="flex flex-col gap-4">
        <div class="line-clamp-1">
          <span class="typo-headline3-bold">Inhalte</span>{{ " " }}
          <span class="typo-headline3-regular">{{ subheading }}</span>
        </div>
        <span
          v-if="subheading && subheadingAddition"
          class="typo-label2-regular line-clamp-1"
          >{{ subheadingAddition }}</span
        >
      </div>

      <IcBaselineList class="typo-body-regular flex-none text-blue-800" />
    </button>
  </Transition>

  <!-- mobile open toc -->
  <Drawer
    v-model:visible="mobileTocVisible"
    aria-label="Inhalte"
    block-scroll
    position="bottom"
    :id="drawerId"
    :close-button-props="closeButtonProps"
  >
    <template #header>
      <div class="flex flex-col gap-4">
        <div class="line-clamp-1">
          <span class="typo-headline3-bold">Inhalte</span>{{ " " }}
          <span class="typo-headline3-regular">{{ subheading }}</span>
        </div>
        <span
          v-if="subheading && subheadingAddition"
          class="typo-label2-regular line-clamp-1"
          >{{ subheadingAddition }}</span
        >
      </div>
    </template>
    <div>
      <NuxtLink
        v-if="subheadingTo"
        :to="subheadingTo"
        class="typo-label1-compact-regular text-blue-800"
      >
        <div
          class="subheading-to-border -mx-16 -mt-8 flex items-center justify-between border-b border-b-gray-400 p-16"
        >
          <span>Zur Gesamtausgabe</span>
          <IcBaselineArrowForward class="size-24" />
        </div>
      </NuxtLink>
      <TreeView
        :items="tableOfContents"
        :selected="selectedKey"
        :expand-to-key="selectedKey"
        :selection-enabled="!!selectedKey"
        label="Inhalte"
        class="-mx-16 h-full"
        @click="mobileTocVisible = false"
      />
    </div>
  </Drawer>

  <!-- desktop -->
  <TreeView
    :items="tableOfContents"
    :selected="selectedKey"
    :expand-to-key="selectedKey"
    :selection-enabled="!!selectedKey"
    :subheading="desktopSubheading"
    :subheading-to="subheadingTo"
    heading="Inhalte"
    class="hidden h-full md:block md:pt-16"
  />
</template>

<style scoped>
@reference "~/assets/main.css";

/*
 * In browsers that don't support scroll-state queries, a border is already drawn above the
 * subheading link as a fallback. Don't add the border in those browsers to avoid a duplicate
 * border.
 */
@supports (container-type: scroll-state) {
  .subheading-to-border {
    @apply border-t border-t-gray-400;
  }
}
</style>
