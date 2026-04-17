<script setup lang="ts">
import {useLangs} from "../composables/langs";

const currentLang = useLangs().currentLang;
</script>

<template>
  <a :href="currentLang.link" aria-label="Home" class="no-underline">
    <div class="ris-label1-bold text-black dark:text-white">Rechtsinformationen</div>
    <div class="ris-label1-regular text-gray-800 dark:text-gray-200 text-left">des Bundes</div>
  </a>
</template>
