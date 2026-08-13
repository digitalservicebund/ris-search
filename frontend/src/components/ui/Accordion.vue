<script setup lang="ts">
import { useId } from "vue";
import IcOutlineExpandCircleDown from "~icons/ic/outline-expand-circle-down";
import { tw } from "../../utils/tags";

defineProps<{
  /** Header label shown while the content is hidden. */
  headerCollapsed: string;
  /** Header label shown while the content is visible. */
  headerExpanded: string;
}>();

const open = defineModel<boolean>({ default: false });

const headerId = useId();

// Classes ------------------------------------------------

// `list-none` and the marker rules remove the native disclosure triangle
const summary = tw`typo-label2-bold mb-6 flex cursor-pointer list-none flex-row items-center gap-8 text-blue-800 outline-offset-4 outline-blue-800 focus-visible:outline-4 [&::-webkit-details-marker]:hidden [&::marker]:hidden`;
</script>

<template>
  <details :open="open">
    <summary
      :aria-expanded="open"
      :class="summary"
      role="button"
      @click.prevent="open = !open"
    >
      <IcOutlineExpandCircleDown :class="{ 'rotate-180': open }" />
      <div :id="headerId">
        {{ open ? headerExpanded : headerCollapsed }}
      </div>
    </summary>

    <section v-show="open" :aria-labelledby="headerId">
      <slot />
    </section>
  </details>
</template>
