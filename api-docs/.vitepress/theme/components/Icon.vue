<script setup lang="ts">
import { useData } from "../composables/data";
import { prependBase } from "../utils";

const { site } = useData();
const props = defineProps<{ id: string; size?: string; class?: string }>();
const size = props.size || "1em";
</script>

<template>
  <svg
    :width="size"
    :height="size"
    :class="'inline-block ' + (props.class || '')"
  >
    <use :href="prependBase(`/icons.svg#${props.id}`, site.base)" />
  </svg>
</template>
