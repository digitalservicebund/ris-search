<script setup lang="ts">
import { useData } from "vitepress";
import ButtonLink from "./ButtonLink.vue";
const { theme } = useData();
</script>

<template>
  <ButtonLink
    :href="theme.swaggerUiUrl"
    target="_blank"
    class="not-prose mt-16 bg-blue-800 hover:bg-blue-600 p-16 ris-label2-bold text-white no-underline focus:text-white focus-visible:text-white"
  >
    Test the API in Swagger
  </ButtonLink>
</template>
