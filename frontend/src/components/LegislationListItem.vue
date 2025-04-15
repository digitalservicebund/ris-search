<script setup lang="ts">
import type { LegislationWork } from "@/types";

const props = defineProps<{ item: LegislationWork }>();
const item = props.item;
</script>

<template>
  <div>
    <NuxtLink :to="'norms/' + item.workExample.legislationIdentifier"
      ><h2 class="ris-link3-bold max-w-title">
        {{ item.name }}
        <span v-if="item.abbreviation">({{ item.abbreviation }})</span>
      </h2>
    </NuxtLink>
  </div>
</template>
