<script setup lang="ts">
const { isGranted, label } = defineProps<{
  /** Whether submitting the form grants or denies consent */
  isGranted: boolean;
  label: string;
}>();

defineOptions({ inheritAttrs: false });

const { setConsent } = useConsent();
const route = useRoute();
</script>

<template>
  <!-- Posts to the server when JavaScript is disabled -->
  <form
    action="/api/consent"
    method="POST"
    class="contents"
    @submit.prevent="setConsent(isGranted)"
  >
    <input
      type="hidden"
      name="consent"
      :value="isGranted ? 'granted' : 'denied'"
    />
    <input type="hidden" name="redirect" :value="route.fullPath" />
    <UiButton v-bind="$attrs" :label="label" type="submit" />
  </form>
</template>
