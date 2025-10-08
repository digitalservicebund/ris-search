<script setup lang="ts">
import ContentWrapper from "~/components/CustomLayouts/ContentWrapper.vue";
import SimpleSearch from "~/components/Search/SimpleSearch/SimpleSearch.vue";
import { useStaticPageSeo } from "~/composables/useStaticPageSeo";

useStaticPageSeo("suche");
</script>

<template>
  <ContentWrapper> <SimpleSearch /></ContentWrapper>
</template>
