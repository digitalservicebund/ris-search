<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref, useSlots, watch } from "vue";
import IcBaselineClose from "~icons/ic/baseline-close";

const { header } = defineProps<{
  /** Title shown in the header. Overridden by the #header slot. */
  header?: string;
}>();

const visible = defineModel<boolean>("visible", { default: false });

const slots = useSlots();
const dialogRef = ref<HTMLDialogElement | null>(null);

// A fixed-position, always-present child of the dialog that slotted content
// can teleport interactive overlays into (e.g. a dropdown panel) instead of
// document.body. A native modal dialog makes everything outside its own DOM
// subtree inert: not just visually behind it, but unclickable, even for
// content that's separately promoted into the top layer.
const appendTargetRef = ref<HTMLElement | null>(null);

// Mounted only while open (or animating closed), so closed drawers don't
// duplicate content (headings, links, ...) in the DOM, and each open gets a
// fresh mount, so components inside (e.g. a draft form field) can't carry
// stale state across a close/reopen cycle.
const showContent = ref(visible.value);

const isOpen = ref(visible.value);

const EXIT_DURATION_MS = 150; // match .drawer-root's transition-duration

let previousBodyOverflow: string | null = null;
let cleanupPendingClose: (() => void) | undefined;

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
  const dialog = dialogRef.value;
  if (!dialog) return;

  cleanupPendingClose?.();
  cleanupPendingClose = undefined;

  if (dialog.open) {
    // Already in the top layer, possibly mid-exit-animation. Just make sure
    // it ends up visually open again.
    isOpen.value = true;
    return;
  }

  showContent.value = true;
  dialog.showModal();
  lockScroll();

  // Let the closed state paint first, so switching to drawer-open is a real
  // transition instead of the dialog's very first style.
  requestAnimationFrame(() => {
    requestAnimationFrame(() => {
      if (dialog.open) isOpen.value = true;
    });
  });
}

function closeDialog() {
  const dialog = dialogRef.value;
  if (!dialog?.open) return;

  isOpen.value = false;

  const onTransitionEnd = (event: TransitionEvent) => {
    if (event.target === dialog) finish();
  };

  const finish = () => {
    clearTimeout(timeoutId);
    dialog.removeEventListener("transitionend", onTransitionEnd);
    cleanupPendingClose = undefined;
    dialog.close();
  };

  // Fallback in case the transition never fires (e.g. reduced motion).
  const timeoutId = setTimeout(finish, EXIT_DURATION_MS + 50);
  dialog.addEventListener("transitionend", onTransitionEnd);

  cleanupPendingClose = () => {
    clearTimeout(timeoutId);
    dialog.removeEventListener("transitionend", onTransitionEnd);
  };
}

function close() {
  visible.value = false;
}

function handleCancel(event: Event) {
  // Run our animated close instead of the browser's default instant one.
  event.preventDefault();
  close();
}

function handleKeydown(event: KeyboardEvent) {
  if (event.key === "Escape") close();
}

function handleClose() {
  visible.value = false;
  isOpen.value = false;
  showContent.value = false;
  unlockScroll();
}

function handleBackdropClick(event: MouseEvent) {
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
  cleanupPendingClose?.();
  if (dialogRef.value?.open) unlockScroll();
});
</script>

<template>
  <dialog
    ref="dialogRef"
    :class="[
      'drawer-root shadow-gray-1000/15 fixed inset-x-0 top-auto m-0 max-h-[85dvh] w-full max-w-none overflow-auto border-0 bg-white p-0 shadow-[0_0_0.5rem] print:hidden',
      { 'drawer-open': isOpen },
    ]"
    @cancel="handleCancel"
    @click="handleBackdropClick"
    @close="handleClose"
    @keydown="handleKeydown"
  >
    <div
      ref="appendTargetRef"
      class="drawer-append-target pointer-events-none fixed inset-0 z-20"
    />

    <template v-if="showContent">
      <div
        class="drawer-header sticky top-0 z-10 flex min-h-64 items-center justify-between gap-8 bg-white px-16 py-8"
      >
        <span class="typo-headline3-bold">
          <slot name="header">{{ header }}</slot>
        </span>

        <button
          type="button"
          class="typo-label2-regular flex cursor-pointer items-center gap-6 py-12 text-blue-800 outline-offset-4 outline-blue-800 focus-visible:outline-4"
          @click="close"
        >
          Schließen
          <IcBaselineClose class="size-20" />
        </button>
      </div>

      <div class="px-16 py-8">
        <slot :append-target="appendTargetRef" />
      </div>

      <div
        v-if="slots.footer"
        class="drawer-footer sticky bottom-0 bg-white px-16 pt-16 pb-24"
      >
        <slot name="footer" />
      </div>
    </template>
  </dialog>
</template>

<style scoped>
.drawer-root {
  container-type: scroll-state;
  bottom: -100dvh;
  opacity: 0;
  pointer-events: none;
  transition:
    bottom 150ms ease-in-out,
    opacity 150ms ease-in-out,
    pointer-events 150ms allow-discrete;
}

.drawer-root.drawer-open {
  bottom: 0;
  opacity: 1;
  pointer-events: auto;
  transition-duration: 300ms;
}

.drawer-root::backdrop {
  background-color: color-mix(in srgb, var(--color-gray-900) 0%, transparent);
  transition: background-color 150ms ease-in-out;
}

.drawer-root.drawer-open::backdrop {
  background-color: color-mix(in srgb, var(--color-gray-900) 30%, transparent);
  transition-duration: 300ms;
}

@supports (color: color-mix(in lab, red, red)) {
  .drawer-root::backdrop {
    background-color: color-mix(
      in oklab,
      var(--color-gray-900) 0%,
      transparent
    );
  }

  .drawer-root.drawer-open::backdrop {
    background-color: color-mix(
      in oklab,
      var(--color-gray-900) 30%,
      transparent
    );
  }
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
