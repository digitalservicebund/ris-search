<script setup lang="ts">
import { RadioButton } from "primevue";
import { itemsPerPageOptions } from "~/utils/search/itemsPerPageOptions";

const model = defineModel<string>();

const name = useId();
const legendId = useId();
</script>

<template>
  <fieldset :aria-labelledby="legendId" class="flex flex-col gap-16">
    <legend :id="legendId" class="typo-label1-bold mb-8">
      Einträge pro Seite
    </legend>
    <div
      v-for="option in itemsPerPageOptions"
      :key="option"
      class="flex items-center gap-8"
    >
      <RadioButton
        v-model="model"
        :input-id="`${name}-${option}`"
        :name="name"
        :value="option"
      />
      <label :for="`${name}-${option}`">{{ option }}</label>
    </div>
  </fieldset>
</template>
