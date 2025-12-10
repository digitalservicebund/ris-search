<script lang="ts" setup>
import IconSearch from "~icons/ic/search";

defineProps<{ listClass: string }>();

defineEmits(["selectItem"]);
</script>

<template>
  <nav class="flex flex-col items-end gap-y-8" aria-label="Hauptnavigation">
    <ul
      class="flex flex-col justify-end gap-x-28 md:flex-row md:items-center"
      :class="listClass"
    >
      <li @click="$emit('selectItem')">
        <NuxtLink
          class="ris-link1-regular link-hover flex items-center gap-4 decoration-3 aria-[current=page]:underline"
          :to="{ name: 'search' }"
          ><IconSearch />Suche</NuxtLink
        >
      </li>
      <li @click="$emit('selectItem')">
        <NuxtLink
          class="ris-link1-regular link-hover decoration-3 aria-[current=page]:underline"
          :to="{ name: 'feedback' }"
          >Feedback geben</NuxtLink
        >
      </li>
      <li @click="$emit('selectItem')">
        <NuxtLink
          class="ris-link1-regular link-hover decoration-3 aria-[current=page]:underline"
          :to="{ name: 'about' }"
          >Über den Service</NuxtLink
        >
      </li>
    </ul>
  </nav>
</template>
