<script setup lang="ts">
import { computed } from "vue";
import { tw } from "../../utils/tags";

const props = withDefaults(
  defineProps<{
    fluid?: boolean;
  }>(),
  {
    fluid: false,
  },
);

const model = defineModel<string>();

// Classes ------------------------------------------------

const base = tw`typo-body-regular [&+small]:typo-label2-regular min-h-56 border-2 border-blue-800 bg-white px-14 pt-8 pb-12 placeholder:text-gray-900 read-only:cursor-not-allowed read-only:border-blue-300 read-only:bg-blue-300 hover:outline hover:outline-4 hover:-outline-offset-4 hover:outline-blue-800 focus-visible:outline focus-visible:outline-4 focus-visible:-outline-offset-4 focus-visible:outline-blue-800 disabled:border-blue-500 disabled:bg-white disabled:text-blue-500 disabled:outline-hidden aria-[invalid]:border-red-800 aria-[invalid]:bg-red-200 aria-[invalid]:outline-red-800 aria-[invalid]:disabled:outline-hidden [&+small]:mt-2 [&+small]:flex [&+small]:items-center [&+small]:gap-4 [&+small]:text-gray-900 [&[aria-invalid="true"]+small]:text-red-900`;

const rootClass = computed(() => ({
  [base]: true,
  "w-full": props.fluid,
}));
</script>

<template>
  <textarea v-model="model" :class="rootClass" />
</template>
