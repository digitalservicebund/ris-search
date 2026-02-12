<script setup lang="ts">
import {
  captureException,
  diagnoseSdkConnectivity,
  isEnabled,
  isInitialized,
} from "@sentry/nuxt";
import { Button } from "primevue";

const status = computed(() => ({
  isEnabled,
  isInitialized,
  dsn: useRuntimeConfig().public.sentryDSN,
  diagnose: connect.value,
}));

const connect = ref("open");
onMounted(async () => {
  try {
    connect.value = (await diagnoseSdkConnectivity()) ?? "no issue detected";
  } catch (err) {
    connect.value = "failed to connect " + err;
  }
});

function throwError() {
  captureException(new Error("testing Sentry integration"));
}
</script>

<template>
  <output class="block">
    {{ JSON.stringify(status) }}
  </output>

  <Button label="Throw error" @click="throwError()" />
</template>
