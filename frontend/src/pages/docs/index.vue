<script setup lang="ts">
import { ApiReference } from "@scalar/api-reference";
import "@scalar/api-reference/style.css";
import openApiSpec from "~/public/openapi.json";

useHead({ title: "Documentation" });
definePageMeta({ alias: ["/docs"], layout: "docs" });
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
