<script setup lang="ts">
import { InputText } from "primevue"
import IcOutlineClose from "~icons/ic/outline-close"
import IcSearch from "~icons/ic/search"

const model = defineModel<string>({ default: "" })

const props = defineProps<{
  matchCount?: number
}>()

const inputId = useId()

function onClear() {
  model.value = ""
}
</script>

<template>
  <div class="flex items-center gap-8">
    <label :for="inputId" class="sr-only">Im Gesetzestext suchen</label>
    <div class="relative flex items-center">
      <IcSearch
        class="absolute left-8 text-gray-600"
        aria-hidden="true"
      />
      <InputText
        :id="inputId"
        v-model="model"
        placeholder="Im Text suchen …"
        type="search"
        class="pl-32 pr-32"
        aria-label="Im Gesetzestext suchen"
      />
      <button
        v-if="model"
        type="button"
        class="absolute right-8 text-gray-600 hover:text-gray-900"
        aria-label="Suche leeren"
        @click="onClear"
      >
        <IcOutlineClose aria-hidden="true" />
      </button>
    </div>
    <span
      v-if="model"
      class="ris-body2-regular whitespace-nowrap text-gray-700"
      aria-live="polite"
    >
      {{ matchCount }} Treffer
    </span>
  </div>
</template>
