<script setup lang="ts">
import { Button, InputGroup, InputGroupAddon, InputText } from "primevue";
import type { SkipLink } from "~";
import IcBaselineSearch from "~icons/ic/baseline-search";
import DataFieldList from "~/components/search/DataFieldList.vue";
import type { DocumentKind } from "~/types/api";
import type { DataField } from "~/utils/search/dataFields";

const {
  dataFields = {},
  count,
  documentKind,
  loading = false,
  skipLinkTarget = "#search-results",
} = defineProps<{
  /** All data fields for all supported document kinds. */
  dataFields?: Record<DocumentKind, DataField[]>;
  /** Number of data sets that can be searched. */
  count?: Partial<Record<DocumentKind, number>>;
  /** Kind of document that will be searched. */
  documentKind: DocumentKind;
  /** True if the component should be displayed in a loading state */
  loading?: boolean;
  /**
   * ID of the underlying form element to connect it with other items on the
   * screen
   */
  formId?: string;
  /** Element to scroll to when the skip link is used */
  skipLinkTarget?: SkipLink["to"];
}>();

/** The current search query. */
const query = defineModel<string>({ default: "" });

const emit = defineEmits<{
  /**
   * Emitted when the search button is clicked or the query is submitted by
   * pressing Enter
   */
  submit: [];
}>();

const queryInputEl = useTemplateRef("queryInputEl");
const queryInputId = useId();

const dataFieldListId = useId();

const formattedCount = computed(() =>
  typeof count?.[documentKind] === "number"
    ? formatNumberWithSeparators(count[documentKind])
    : "",
);

const formattedDocumentKind = computed(() => formatDocumentKind(documentKind));

const dataFieldsForDocumentKind = computed(
  // Need to cast explicitly because the Vue types don't seem to be working here
  () => (dataFields as Record<DocumentKind, DataField[]>)?.[documentKind] ?? [],
);

function insertInQuery({ pattern }: DataField) {
  let newQuery = query.value.trimEnd();
  if (newQuery.length) newQuery += " ";

  let nextCursorPosition = newQuery.length;
  const cursorMarker = pattern.indexOf("$");
  if (cursorMarker >= 0) {
    pattern = pattern.replace("$", "");
    nextCursorPosition += cursorMarker;
  } else {
    nextCursorPosition += pattern.length;
  }

  newQuery += pattern;
  query.value = newQuery;

  nextTick().then(() => {
    // @ts-expect-error -- Type is wrong here, $el does exist
    const focusableInput = queryInputEl.value?.$el;
    if (!(focusableInput instanceof HTMLInputElement)) return;

    focusableInput.focus();
    focusableInput.setSelectionRange(nextCursorPosition, nextCursorPosition);
  });
}

function submitUnlessLoading() {
  if (!loading) emit("submit");
}
</script>

<template>
  <search class="flex flex-col">
    <p class="typo-label1-bold mb-8" aria-hidden="true">
      In {{ formattedCount }} {{ formattedDocumentKind }} suchen
    </p>

    <form
      :id="formId"
      data-testid="form"
      @submit.prevent="submitUnlessLoading()"
      class="mb-16"
    >
      <InputGroup>
        <InputText
          :id="queryInputId"
          ref="queryInputEl"
          v-model="query"
          aria-label="Suchbegriff eingeben"
          size="large"
          class="grow"
          placeholder="Suchbegriff eingeben"
          type="search"
        />
        <InputGroupAddon>
          <Button aria-label="Suchen" size="large" type="submit" :loading>
            <template #icon>
              <IcBaselineSearch />
            </template>
          </Button>
        </InputGroupAddon>
      </InputGroup>

      <SkipLink class="mt-8" :to="skipLinkTarget">Zu den Ergebnissen</SkipLink>
    </form>

    <div class="hidden lg:block">
      <p class="typo-label2-regular mb-8">
        Diese Datenfelder können gezielt durchsucht werden:
      </p>

      <DataFieldList
        :data-fields="dataFieldsForDocumentKind"
        :label-id="dataFieldListId"
        @click-data-field="insertInQuery"
      />
    </div>

    <div class="mt-8 mb-16 lg:hidden">
      <SingleAccordion
        header-collapsed="Auswahl für gezielte Suche"
        header-expanded="Auswahl für gezielte Suche"
      >
        <DataFieldList
          :data-fields="dataFieldsForDocumentKind"
          :label-id="dataFieldListId"
          @click-data-field="insertInQuery"
        />
      </SingleAccordion>
    </div>
  </search>

  <SearchOperatorsHelp class="lg:mt-32" />
</template>
