<script setup lang="ts">
import { RisSingleAccordion } from "@digitalservicebund/ris-ui/components";
import { Button, InputGroup, InputGroupAddon, InputText } from "primevue";
import OperatorsHelp from "./OperatorsHelp.vue";
import DataFieldList from "~/components/Search/DataFieldList.vue";
import type { DocumentKind } from "~/types";
import { formatDocumentKind } from "~/utils/displayValues";
import { formatNumberWithSeparators } from "~/utils/numberFormatting";
import type { DataField } from "~/utils/search/dataFields";
import IcBaselineSearch from "~icons/ic/baseline-search";

const {
  dataFields = {},
  count,
  documentKind,
  loading = false,
} = defineProps<{
  /** All data fields for all supported document kinds. */
  dataFields?: Record<DocumentKind, DataField[]>;
  /** Number of data sets that can be searched. */
  count?: Partial<Record<DocumentKind, number>>;
  /** Kind of document that will be searched. */
  documentKind: DocumentKind;
  /** True if the component should be displayed in a loading state */
  loading?: boolean;
  /** ID of the underlying form element to connect it with other items on the screen */
  formId?: string;
}>();

/** The current search query. */
const query = defineModel<string>({ default: "" });

const emit = defineEmits<{
  /**
   * Emitted when the search button is clicked or the query is submitted
   * by pressing Enter
   */
  submit: [];
}>();

const queryInputEl = useTemplateRef("queryInputEl");
const queryInputId = useId();
const queryDescriptionId = useId();

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
  <search class="flex flex-col gap-8" :aria-labelledby="queryDescriptionId">
    <p
      :id="queryDescriptionId"
      class="ris-label2-bold lg:ris-label1-regular"
      aria-hidden="true"
    >
      In {{ formattedCount }} {{ formattedDocumentKind }} suchen
    </p>

    <form
      :id="formId"
      data-testid="form"
      @submit.prevent="submitUnlessLoading()"
    >
      <InputGroup>
        <label class="sr-only" :for="queryInputId">Suchfeld</label>
        <InputText
          :id="queryInputId"
          ref="queryInputEl"
          v-model="query"
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
    </form>

    <div class="hidden lg:block">
      <p class="ris-label2-bold mb-8">
        Diese Datenfelder können gezielt durchsucht werden:
      </p>

      <DataFieldList
        :data-fields="dataFieldsForDocumentKind"
        :label-id="dataFieldListId"
        @click-data-field="insertInQuery"
      />
    </div>

    <div class="mt-8 mb-16 lg:hidden">
      <RisSingleAccordion
        header-collapsed="Auswahl für gezielte Suche"
        header-expanded="Auswahl für gezielte Suche"
      >
        <DataFieldList
          :data-fields="dataFieldsForDocumentKind"
          :label-id="dataFieldListId"
          @click-data-field="insertInQuery"
        />
      </RisSingleAccordion>
    </div>
  </search>

  <OperatorsHelp class="lg:mt-32" />
</template>
