<script setup lang="ts">
import { Button } from "primevue";

function throwError() {
  throw new Error("testing Sentry integration");
}
</script>

<template>
  <Button label="Throw error" @click="throwError()" />
</template>
