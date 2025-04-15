<script lang="ts" setup>
import IconSearch from "~icons/ic/search?width=20&height=20";
import LoginActions from "~/components/LoginActions.vue";

const config = useRuntimeConfig();

defineProps<{ listClass: string }>();
</script>

<template>
  <nav class="flex flex-col items-end gap-y-8" aria-label="Hauptnavigation">
    <ul
      class="flex flex-col justify-end gap-x-28 md:flex-row md:items-center"
      :class="listClass"
    >
      <li>
        <NuxtLink
          class="ris-link1-regular link-hover flex items-center gap-4"
          to="/search"
          ><IconSearch />Suche</NuxtLink
        >
      </li>
      <li>
        <NuxtLink class="ris-link1-regular link-hover" to="/feedback"
          >Feedback geben</NuxtLink
        >
      </li>
      <li>
        <NuxtLink class="ris-link1-regular link-hover" to="/ueber"
          >Über den Service</NuxtLink
        >
      </li>
      <LoginActions v-if="config.public.authEnabled" />
    </ul>
  </nav>
</template>
