<script setup lang="ts">
import { RisSingleAccordion } from "@digitalservicebund/ris-ui/components";
import { Button, InputGroup, InputGroupAddon, InputText } from "primevue";
import DataFieldList from "~/components/AdvancedSearch/DataFieldList.vue";
import type { DataField } from "~/pages/advanced-search/dataFields";
import type { DocumentKind } from "~/types";
import IcBaselineSearch from "~icons/ic/baseline-search";

const {
  dataFields = {},
  count,
  documentKind,
} = defineProps<{
  /** All data fields for all supported document kinds. */
  dataFields?: Record<DocumentKind, DataField[]>;
  /** Number of data sets that can be searched. */
  count: number;
  /** Kind of document that will be searched. */
  documentKind: DocumentKind;
}>();

/** The current search query. */
const query = defineModel<string>({ default: "" });

const queryInputEl = useTemplateRef("queryInputEl");
const queryInputId = useId();

const formattedCount = computed(() => formatNumberWithSeparators(count));
const formattedDocumentKind = computed(() => formatDocumentKind(documentKind));

const dataFieldsForDokumentKind = computed(
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
</script>

<template>
  <div class="flex flex-col gap-8">
    <span class="ris-label2-bold lg:ris-label1-regular">
      In {{ formattedCount }} {{ formattedDocumentKind }} suchen
    </span>

    <InputGroup>
      <label class="sr-only" :for="queryInputId">Suchanfrage</label>
      <InputText
        :id="queryInputId"
        ref="queryInputEl"
        v-model="query"
        size="large"
        class="grow"
      />
      <InputGroupAddon>
        <Button aria-label="Suchen" size="large">
          <template #icon>
            <IcBaselineSearch />
          </template>
        </Button>
      </InputGroupAddon>
    </InputGroup>

    <span class="ris-label2-bold">
      Diese Datenfelder können gezielt durchsucht werden:
    </span>

    <div class="hidden lg:block">
      <DataFieldList
        :data-fields="dataFieldsForDokumentKind"
        @click-data-field="insertInQuery"
      />
    </div>

    <div class="lg:hidden">
      <RisSingleAccordion
        header-collapsed="Auswahl für gezielte Suche"
        header-expanded="Auswahl für gezielte Suche"
      >
        <DataFieldList
          :data-fields="dataFieldsForDokumentKind"
          @click-data-field="insertInQuery"
        />
      </RisSingleAccordion>
    </div>
  </div>
</template>
