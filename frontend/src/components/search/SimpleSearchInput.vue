<script setup lang="ts">
import { Button, InputGroup, InputGroupAddon, InputText } from "primevue";
import IconSearch from "~icons/ic/search";

const {
  inputLabel = "Suchfeld",
  inputPlaceholder = "Suchbegriff eingeben",
  submitLabel = "Suchen",
} = defineProps<{
  fullWidth?: boolean;
  inputLabel?: string;
  inputPlaceholder?: string;
  submitLabel?: string;
}>();

const model = defineModel<string>();

// currentText is decoupled from the model, we want to update
// the model only when the user performs a search
const currentText = ref<string | undefined>(model.value);

// propagate model updates back to the input
watch(model, (newValue) => {
  currentText.value = newValue;
});

const emit = defineEmits(["emptySearch"]);

const performSearch = () => {
  // if the user hasn't entered any text, updating the model will have no effect
  // since they might still want to trigger an empty search, use "emit"
  if (!currentText.value) emit("emptySearch");

  model.value = currentText.value;
};

const searchInputId = useId();
</script>

<template>
  <form
    role="search"
    :class="{ 'max-w-md': !fullWidth }"
    action="/search"
    @submit.prevent="performSearch"
  >
    <InputGroup>
      <label class="sr-only" :for="searchInputId">{{ inputLabel }}</label>
      <InputText
        :id="searchInputId"
        v-model="currentText"
        :placeholder="inputPlaceholder"
        fluid
        name="query"
        type="search"
      />
      <InputGroupAddon>
        <Button :aria-label="submitLabel" type="submit">
          <template #icon><IconSearch /></template>
        </Button>
      </InputGroupAddon>
    </InputGroup>
  </form>
</template>
