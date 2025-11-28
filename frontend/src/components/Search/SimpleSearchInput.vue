<script setup lang="ts">
import Button from "primevue/button";
import InputText from "primevue/inputtext";
import { addDefaults } from "~/composables/useSimpleSearchParams/getInitialState";
import { usePostHogStore } from "~/stores/usePostHogStore";
import IconSearch from "~icons/ic/search";

const {
  inputLabel = "Suchbegriff",
  inputPlaceholder = "Suchbegriff eingeben",
  submitLabel = "Suchen",
} = defineProps<{
  fullWidth?: boolean;
  inputLabel?: string;
  inputPlaceholder?: string;
  submitLabel?: string;
}>();

const model = defineModel<string>();

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
        autofocus
        fluid
        name="query"
        type="search"
        @keyup="onKeyup"
      />
      <InputGroupAddon>
        <Button :aria-label="submitLabel" type="submit">
          <template #icon><IconSearch /></template>
        </Button>
      </InputGroupAddon>
    </InputGroup>
  </form>
</template>
