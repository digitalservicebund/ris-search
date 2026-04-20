<script setup lang="ts">
import { ref, useId, useTemplateRef, watchEffect } from "vue";

const { length = 3, tolerance = 3 } = defineProps<{
  /**
   * Specifies the maximum number of visible lines.
   * @default 3
   */
  length?: number;
  /**
   * Specifies a pixels threshold under which overflowing content would be
   * ignored.
   * @default 3
   */
  tolerance?: number;
}>();

const expanded = defineModel<boolean>("expanded", { default: false });

const canExpand = ref(false);

// Note that the variable name must be different than the ref name, otherwise
// the compiled version of this component will trigger a warning in development
// mode of the applications using RIS UI. While this doesn't break anything, it
// adds a lot of noise to the console and test output.
//
// See https://github.com/vuejs/core/issues/12852
const textContentRef = useTemplateRef("textContent");

const textId = useId();

watchEffect(() => {
  if (textContentRef.value instanceof HTMLDivElement) {
    canExpand.value =
      textContentRef.value.scrollHeight - tolerance >
      textContentRef.value.clientHeight;
  }
});
</script>

<template>
  <div>
    <div
      :id="textId"
      ref="textContent"
      :class="{ [$style.truncate]: !expanded }"
    >
      <slot />
    </div>

    <button
      v-if="canExpand"
      class="ris-link1-regular cursor-pointer focus-visible:outline-4 focus-visible:outline-offset-4 focus-visible:outline-blue-800 active:outline-hidden"
      :aria-controls="textId"
      :aria-expanded="expanded"
      @click="expanded = !expanded"
    >
      <template v-if="expanded">Weniger anzeigen</template>
      <template v-if="!expanded">Mehr anzeigen</template>
    </button>
  </div>
</template>

<style module>
.truncate {
  overflow: hidden;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: v-bind(length);
}
</style>
