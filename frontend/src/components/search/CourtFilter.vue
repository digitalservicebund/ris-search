<script setup lang="ts">
import { debounce } from "lodash-es";
import type {
  AutoCompleteCompleteEvent,
  AutoCompleteDropdownClickEvent,
} from "primevue/autocomplete";
import type { AutoCompleteSuggestion } from "~/components/AutoComplete.vue";
import type { CourtSearchResult, CourtsSearchParams } from "~/types/api";
import { courtFilterDefaultSuggestions } from "~/utils/search/courtFilter";

const { appendTo = "self" } = defineProps<{
  /**
   * Where to render the suggestions overlay. "self" (the default) keeps it
   * width-matched to the field, but gets clipped by any scrollable ancestor
   * (e.g. a Drawer). Use "body" in such contexts instead.
   */
  appendTo?: "self" | "body";
}>();

const model = defineModel<string | undefined>();

const searchResults = ref<CourtSearchResult[]>([]);

const { $risBackend } = useNuxtApp();

const search = async (prefix?: string) => {
  const query: CourtsSearchParams = prefix ? { prefix } : {};
  searchResults.value = await $risBackend<CourtSearchResult[]>(
    "/v1/case-law/courts",
    { query },
  );
};

const searchDebounced = debounce(search, 250);

/*
Workaround for loading prop being ignored in PrimeVue AutoComplete:
It is important that the suggestions.value be updated each time. Otherwise, the loading indicator will not disappear
the second time that the default suggestions are invoked using the dropdown.

Both onComplete and onDropdownClick are called when the dropdown is opened,
but only onDropdownClick is called on close.

See https://github.com/primefaces/primevue/issues/5601 for further information.
 */
const onComplete = (
  event:
    | AutoCompleteCompleteEvent
    | AutoCompleteDropdownClickEvent
    | { query: undefined },
) => {
  if (event.query) {
    // normal search for entered prefix
    searchDebounced(event.query);
  } else if (model.value) {
    // user has already made a selection, use that as the prefix
    searchDebounced(model.value);
  } else {
    // dropdown was opened without any text entered or value pre-selected
    // a copy of the default suggestions is required since the loading
    searchResults.value = [...courtFilterDefaultSuggestions];
  }
};

const onDropdownClick = (
  event: AutoCompleteDropdownClickEvent | { query: undefined },
) => {
  if (event.query === undefined) {
    // dropdown has been closed
    searchResults.value = [];
  } else {
    // onComplete will also fire, but with an empty query
    // therefore, call it again
    onComplete(event);
  }
};

const onItemSelect = () => {
  searchResults.value = [];
};

const suggestions = computed<AutoCompleteSuggestion[]>(() =>
  searchResults.value
    .filter(
      (i): i is typeof i & { id: string; label: string } => !!i.id && !!i.label,
    )
    .map((i) => ({
      id: i.id,
      label: i.label,
      secondaryLabel: i.id,
    })),
);

const id = useId();
const hintId = useId();

/*
 * When appendTo="body", PrimeVue's own overlay positioning (alignOverlay in
 * primevue/autocomplete) anchors off the bare <input> element rather than the
 * whole field (which also includes the dropdown button and the field's
 * padding/border), and only sets a min-width rather than a width. Without an
 * explicit width, the overlay's suggestion list (rendered with white-space:
 * nowrap) can grow well beyond the field, and PrimeVue then mis-detects a
 * right-edge overflow and clamps the overlay flush to the viewport's left edge
 * instead of aligning it with the field.
 *
 * We correct both by measuring the field ourselves and applying the result
 * directly to the overlay's `style`, ignoring whatever PrimeVue computed.
 * PrimeVue re-runs its own (wrong) calculation - and overwrites both properties
 * - on every re-render while the overlay is open (see its `updated()` hook),
 * e.g. whenever the suggestion list changes, so a one-off correction isn't
 * enough.
 *
 * Correcting the overlay only once it's fully open (e.g. on a "show" event,
 * which fires after PrimeVue's enter transition finishes) would mean the wrong
 * position is visible - and painted - for the whole transition, causing a jump
 * right at the end. Instead, a MutationObserver watches document.body for the
 * overlay being inserted, and immediately attaches a second observer to its
 * `style` attribute. Since MutationObserver callbacks run as microtasks (before
 * the browser's next paint, even for changes made by Vue/PrimeVue earlier in
 * the same task), the very first (wrong) style PrimeVue sets is corrected
 * before it can ever be painted, and every later re-alignment is caught the
 * same way.
 */
const OVERLAY_MARKER_CLASS = "court-filter-overlay";
const fieldRef = useTemplateRef<HTMLElement>("fieldRef");
let bodyObserver: MutationObserver | undefined;
let styleObserver: MutationObserver | undefined;

const correctOverlayPosition = (overlayEl: HTMLElement) => {
  const rect = fieldRef.value?.getBoundingClientRect();
  if (!rect) return;
  const left = `${rect.left + window.scrollX}px`;
  const width = `${rect.width}px`;
  if (overlayEl.style.insetInlineStart !== left) {
    overlayEl.style.insetInlineStart = left;
  }
  if (overlayEl.style.width !== width) {
    overlayEl.style.width = width;
  }
};

const watchOverlayPosition = (overlayEl: HTMLElement) => {
  correctOverlayPosition(overlayEl);
  styleObserver?.disconnect();
  styleObserver = new MutationObserver(() => correctOverlayPosition(overlayEl));
  styleObserver.observe(overlayEl, {
    attributes: true,
    attributeFilter: ["style"],
  });
};

const watchForBodyOverlay = () => {
  if (appendTo !== "body") return;
  bodyObserver?.disconnect();
  bodyObserver = new MutationObserver((mutations) => {
    for (const mutation of mutations) {
      for (const node of mutation.addedNodes) {
        if (
          node instanceof HTMLElement &&
          node.classList.contains(OVERLAY_MARKER_CLASS)
        ) {
          bodyObserver?.disconnect();
          watchOverlayPosition(node);
          return;
        }
      }
    }
  });
  bodyObserver.observe(document.body, { childList: true });
};

const stopAligningBodyOverlay = () => {
  bodyObserver?.disconnect();
  bodyObserver = undefined;
  styleObserver?.disconnect();
  styleObserver = undefined;
};

onBeforeUnmount(stopAligningBodyOverlay);
</script>

<template>
  <div class="flex flex-col gap-4">
    <label :id="id" class="typo-label1-bold">Gericht</label>
    <small :id="hintId" class="ris-label3-regular md:ris-label2-regular">
      Bundesgericht auswählen oder weiteres Gericht suchen
    </small>
    <div ref="fieldRef" data-testid="court-filter-field">
      <AutoComplete
        v-model="model"
        :aria-labelledby="id"
        :append-to="appendTo"
        :suggestions
        dropdown
        dropdown-mode="blank"
        placeholder="Auswählen oder suchen"
        :pt="{
          // AutoComplete has no ariaDescribedby prop, so the hint text above
          // the field is tied to the input as its accessible description via
          // passthrough instead. In non-multiple mode (our case), AutoComplete
          // renders the input as a nested InputText, whose own root section
          // *is* the input element - hence the extra nesting.
          pcInputText: { root: { 'aria-describedby': hintId } },
          ...(appendTo === 'body'
            ? {
                // AutoComplete isn't customized by ris-ui, so this reproduces
                // its default overlay classes, minus the theme's own
                // `min-width: 100%` rule (which would otherwise force the panel
                // to the viewport's width when appended to body). Providing
                // pt.overlay.class here replaces rather than merges with the
                // default, so all of the other classes need to be repeated too.
                // The marker class lets watchForBodyOverlay find this element
                // again; width/position are then corrected in JS, see the
                // comment above.
                overlay: {
                  class: `mt-12 overflow-auto bg-white px-8 py-12 shadow-md ${OVERLAY_MARKER_CLASS}`,
                },
              }
            : {}),
        }"
        typeahead
        @before-show="watchForBodyOverlay"
        @complete="onComplete"
        @dropdown-click="onDropdownClick"
        @hide="stopAligningBodyOverlay"
        @item-select="onItemSelect"
      />
    </div>
  </div>
</template>
