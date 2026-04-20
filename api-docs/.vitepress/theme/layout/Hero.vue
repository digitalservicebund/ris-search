<script lang="ts" setup>
import { computed } from "vue";

const props = defineProps<{
  title: string;
  text: string;
  variant?: "primary" | "secondary";
}>();

const headerStyles = computed(() => {
  return `${props.variant === "primary" ? "ris-heading2-bold" : "ris-heading2-regular"} text-black dark:text-white mb-16`
});
</script>

<template>
  <div class="bg-gray-100 dark:bg-gray-800 text-black dark:text-white">
    <div class="container">
      <div
        class="flex flex-col gap-16 lg:flex-row lg:justify-between py-48"
      >
        <div class="max-w-prose">
          <h1
            v-html="props.title"
            :class="headerStyles"
          />
          <p v-html="props.text" class="text-lg prose ris-subhead-regular" />
        </div>
        <slot />
      </div>
    </div>
  </div>
</template>
