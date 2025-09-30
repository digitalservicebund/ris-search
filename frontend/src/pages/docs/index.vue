<script setup lang="ts">
import { ApiReference } from "@scalar/api-reference";
import { useStaticPageSeo } from "~/composables/useStaticPageSeo";
import "@scalar/api-reference/style.css";
import openApiSpec from "~/public/openapi.json";

definePageMeta({ alias: ["/docs"], layout: "docs" });

useStaticPageSeo("api");
</script>

<template>
  <div>
    <ApiReference
      :configuration="{
        content: openApiSpec,
        showSidebar: false,
      }"
    />
  </div>
</template>
