<script setup lang="ts">
import Button from "primevue/button";
import InputText from "primevue/inputtext";
import { addDefaults } from "~/stores/searchParams/getInitialState";
import IconSearch from "~icons/ic/search";

const userInputDisabled = ref(true);
onNuxtReady(() => {
  userInputDisabled.value = false;
});

const model = defineModel<string>();
const props = defineProps<{ fullWidth?: boolean }>();
const postHogStore = usePostHogStore();
const router = useRouter();

// currentText is decoupled from the model, we want to update
// the model only when the user performs a search
const currentText = ref<string | undefined>(model.value);

// propagate model updates back to the input
watch(model, (newValue) => {
  currentText.value = newValue;
});
const emit = defineEmits(["emptySearch"]);
const performSearch = () => {
  // If the user is coming from another page, we want to track the search
  if (router.currentRoute.value.name !== "search") {
    postHogStore.searchPerformed(
      "simple",
      addDefaults({ query: currentText.value ?? "" }),
    );
  }
  if (!currentText.value) {
    // if the user hasn't entered any text, updating the model will have no effect
    // since they might still want to trigger an empty search, use "emit"
    emit("emptySearch");
  }
  model.value = currentText.value;
};
const onKeyup = (event: KeyboardEvent) => {
  if (event.key === "Enter") {
    performSearch();
  }
};
</script>

<template>
  <form
    role="search"
    class="flex max-w-md flex-row gap-8 data-[full-width='true']:max-w-full"
    :data-full-width="props.fullWidth"
    @submit.prevent="performSearch"
  >
    <InputField
      id="searchInput"
      label="Suche nach Rechtsinformationen"
      visually-hide-label
    >
      <InputText
        id="searchInput"
        v-model="currentText"
        aria-label="Suchbegriff"
        fluid
        placeholder="Suchbegriff eingeben"
        :disabled="userInputDisabled"
        autofocus
        type="search"
        @keyup="onKeyup"
      />
    </InputField>
    <Button
      aria-label="Suchen"
      class="h-[3rem] w-[3rem] shrink-0 justify-center"
      :disabled="userInputDisabled"
      @click="performSearch"
    >
      <template #icon><IconSearch /></template>
    </Button>
  </form>
</template>
