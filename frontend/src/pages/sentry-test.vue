<script setup lang="ts">
import { captureException } from "@sentry/nuxt";
import { Button } from "primevue";

function throwError() {
  captureException(new Error("testing Sentry integration 2"));
}
</script>

<template>
  <Button label="Throw error" @click="throwError()" />
</template>
