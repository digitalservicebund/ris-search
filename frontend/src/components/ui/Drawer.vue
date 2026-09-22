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

// The dialog's content is only mounted once the drawer has been opened for
// the first time, so unopened drawers don't leave inert duplicate content
// (headings, links, ...) sitting in the DOM.
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

// The dialog's native "close" event fires both for our own close() calls
// (button/backdrop click -> visible=false -> watcher -> dialog.close()) and
// for the browser's own default action (Escape) - syncing visible here
// covers both without duplicating close logic.
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

// translate isn't animated here via Tailwind's translate-y-* utilities:
// Safari's @starting-style fails to pick up the starting value when it's
// held in a custom property (Tailwind's translate utilities route through
// --tw-translate-y), so the slide-in silently no-ops there while opacity
// (a plain value, no custom property) still animates. See
// https://github.com/tailwindlabs/tailwindcss/discussions/18304. Fixed
// below in a scoped <style> block using direct `translate` values instead.
const root = tw`drawer-root shadow-gray-1000/15 fixed inset-x-0 top-auto bottom-0 m-0 max-h-[85dvh] w-full max-w-none overflow-auto border-0 bg-white p-0 shadow-[0_0_0.5rem] backdrop:bg-gray-900/30 backdrop:transition-all backdrop:transition-discrete backdrop:duration-300 backdrop:ease-in-out not-open:backdrop:bg-gray-900/0 starting:open:backdrop:bg-gray-900/0 print:hidden`;

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
  transition:
    translate 200ms ease-in-out,
    opacity 200ms ease-in-out,
    overlay 200ms ease-in-out allow-discrete,
    display 200ms ease-in-out allow-discrete;
}

.drawer-root:not([open]) {
  translate: 0 100%;
  opacity: 0;
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
