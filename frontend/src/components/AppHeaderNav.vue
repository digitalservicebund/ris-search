<script lang="ts" setup>
import LoginActions from "~/components/LoginActions.vue";
import IconSearch from "~icons/ic/search?width=20&height=20";

const config = useRuntimeConfig();

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
          to="/search"
          ><IconSearch />Suche</NuxtLink
        >
      </li>
      <li @click="$emit('selectItem')">
        <NuxtLink
          class="ris-link1-regular link-hover decoration-3 aria-[current=page]:underline"
          to="/feedback"
          >Feedback geben</NuxtLink
        >
      </li>
      <li @click="$emit('selectItem')">
        <NuxtLink
          class="ris-link1-regular link-hover decoration-3 aria-[current=page]:underline"
          to="/ueber"
          >Über den Service</NuxtLink
        >
      </li>
      <LoginActions v-if="config.public.authEnabled" />
    </ul>
  </nav>
</template>
