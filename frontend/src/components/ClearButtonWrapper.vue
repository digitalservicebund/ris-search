<script setup lang="ts">
import IcBaselineClose from "~icons/ic/baseline-close";

withDefaults(
  defineProps<{
    /**
     * Whether the clear button is shown. Set to true when the wrapped input has
     * a value.
     */
    clearButtonVisible?: boolean;
  }>(),
  {
    clearButtonVisible: false,
  },
);

const emit = defineEmits<{
  /** Emitted when the user clicks the clear button. */
  clear: [];
}>();
</script>

<template>
  <div class="relative">
    <slot />
    <button
      v-if="clearButtonVisible"
      type="button"
      class="absolute inset-y-6 right-6 flex aspect-square cursor-pointer items-center justify-center text-blue-800 hover:bg-blue-100 hover:text-blue-800 focus-visible:bg-blue-800 focus-visible:text-white focus-visible:outline-none"
      aria-label="Entfernen"
      @click="emit('clear')"
    >
      <IcBaselineClose class="h-[1em] w-[1em]" />
    </button>
  </div>
</template>
