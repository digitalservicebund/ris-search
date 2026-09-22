<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref, useSlots, watch } from "vue";
import IcBaselineClose from "~icons/ic/baseline-close";
import { tw } from "../../utils/tags";

const { header } = defineProps<{
  /** Title shown in the header. Overridden by the #header slot. */
  header?: string;
}>();

const visible = defineModel<boolean>("visible", { default: false });

const slots = useSlots();
const dialogRef = ref<HTMLDialogElement | null>(null);

// Mount content lazily on first open, so closed drawers don't duplicate
// content (headings, links, ...) in the DOM.
const hasOpened = ref(visible.value);

let previousBodyOverflow: string | null = null;

function lockScroll() {
  previousBodyOverflow = document.body.style.overflow;
  document.body.style.overflow = "hidden";
}

function unlockScroll() {
  if (previousBodyOverflow !== null) {
    document.body.style.overflow = previousBodyOverflow;
    previousBodyOverflow = null;
  }
}

function openDialog() {
  if (dialogRef.value?.open) return;
  hasOpened.value = true;
  dialogRef.value?.showModal();
  lockScroll();
}

function closeDialog() {
  if (dialogRef.value?.open) dialogRef.value.close();
}

function close() {
  visible.value = false;
}

// Covers both close paths in one place: our close() (visible=false, then
// the watcher calls dialog.close()) and the browser's native Escape handling.
function handleClose() {
  visible.value = false;
  unlockScroll();
}

function handleBackdropClick(event: MouseEvent) {
  // Clicks on the ::backdrop pseudo-element target the dialog itself; clicks
  // on the header/content/footer target those elements instead.
  if (event.target === dialogRef.value) close();
}

watch(visible, (isVisible) => {
  if (isVisible) openDialog();
  else closeDialog();
});

onMounted(() => {
  if (visible.value) openDialog();
});

onBeforeUnmount(() => {
  if (dialogRef.value?.open) unlockScroll();
});

// Classes ------------------------------------------------

// Not using Tailwind's translate-y-* utilities here: Safari's
// @starting-style ignores values held in a custom property, so the entrance
// slide silently breaks there (opacity, a plain value, still animates). See
// https://github.com/tailwindlabs/tailwindcss/discussions/18304.
// The scoped <style> below uses direct `translate` values instead.
const root = tw`drawer-root shadow-gray-1000/15 fixed inset-x-0 top-auto bottom-0 m-0 max-h-[85dvh] w-full max-w-none overflow-auto border-0 bg-white p-0 shadow-[0_0_0.5rem] backdrop:bg-gray-900/30 backdrop:transition-all backdrop:transition-discrete backdrop:duration-300 backdrop:ease-in-out not-open:backdrop:bg-gray-900/0 not-open:backdrop:duration-150 starting:open:backdrop:bg-gray-900/0 print:hidden`;

const headerClass = tw`drawer-header sticky top-0 z-10 flex min-h-64 items-center justify-between gap-8 bg-white px-16 py-8`;

const titleClass = tw`typo-headline3-bold`;

const closeButtonClass = tw`typo-label2-regular flex cursor-pointer items-center gap-6 py-12 text-blue-800 outline-offset-4 outline-blue-800 focus-visible:outline-4`;

const contentClass = tw`px-16 py-8`;

const footerClass = tw`drawer-footer sticky bottom-0 bg-white px-16 pt-16 pb-24`;
</script>

<template>
  <dialog
    ref="dialogRef"
    :class="root"
    @click="handleBackdropClick"
    @close="handleClose"
  >
    <template v-if="hasOpened">
      <div :class="headerClass">
        <span :class="titleClass">
          <slot name="header">{{ header }}</slot>
        </span>

        <button type="button" :class="closeButtonClass" @click="close">
          Schließen
          <IcBaselineClose class="size-20" />
        </button>
      </div>

      <div :class="contentClass">
        <slot />
      </div>

      <div v-if="slots.footer" :class="footerClass">
        <slot name="footer" />
      </div>
    </template>
  </dialog>
</template>

<style scoped>
.drawer-root {
  translate: 0 0;
  opacity: 1;
  transition-property: translate, opacity, overlay, display;
  transition-duration: 300ms;
  transition-timing-function: ease-in-out;
  transition-behavior: allow-discrete;
}

.drawer-root:not([open]) {
  translate: 0 100%;
  opacity: 0;
  /* CSS transitions take their duration from the state being transitioned
     into, so this is what makes the drawer close faster than it opens. */
  transition-duration: 150ms;
}

@starting-style {
  .drawer-root[open] {
    translate: 0 100%;
    opacity: 0;
  }
}

.drawer-root {
  container-type: scroll-state;
}

.drawer-header,
.drawer-footer {
  box-shadow: 0 0 0.5rem 0 transparent;
  transition: box-shadow 100ms ease-in-out;
}

@container scroll-state(scrollable: top) {
  .drawer-header {
    border-bottom: 1px solid var(--color-gray-400);
    box-shadow: 0 0 0.5rem 0 rgb(from var(--color-gray-1000) r g b / 0.15);
  }
}

@container scroll-state(scrollable: bottom) {
  .drawer-footer {
    border-top: 1px solid var(--color-gray-400);
    box-shadow: 0 0 0.5rem 0 rgb(from var(--color-gray-1000) r g b / 0.15);
  }
}

@supports not (container-type: scroll-state) {
  .drawer-header {
    border-bottom: 1px solid var(--color-gray-400);
  }

  .drawer-footer {
    border-top: 1px solid var(--color-gray-400);
  }
}
</style>
