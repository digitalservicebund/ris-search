<script setup lang="ts">
/**
 * Wrapper component for legislation content that provides scoped styling
 * for server-rendered AKN (Akoma Ntoso) HTML content.
 */
defineProps<{
  /** When true, applies single-article view styling (hides section headings) */
  singleArticle?: boolean;
}>();
</script>

<template>
  <div :class="['legislation', { 'single-article': singleArticle }]">
    <slot />
  </div>
</template>

<style scoped>
@reference "~/assets/main.css";

.legislation {
  @apply max-w-prose print:max-w-none;
}

/* AKN */
:deep(.akn-act) {
  @apply flex flex-col gap-32 pt-24;
}

:deep(.akn-content) {
  @apply block;
}

/* Display the following structure all inline:
 | .akn-paragraph
    | .akn-num
    | .akn-content
       | .akn-p
 */
:deep(.akn-num + .akn-content),
:deep(.akn-content > .akn-p:first-child) {
  @apply inline;
}

:deep(.akn-paragraph) {
  @apply mb-16;
}

/* These values will be displayed separately */
:deep(.akn-docTitle) {
  @apply hidden;
}

:deep(.akn-shortTitle) {
  @apply hidden;
}

:deep(.akn-proprietary) {
  @apply hidden;
}

:deep(.akn-act a) {
  @apply text-blue-800 outline-offset-4 outline-blue-800 focus-visible:outline-4;

  &:hover * {
    @apply underline decoration-3;
  }
}

:deep(.akn-act table > thead > tr > th) {
  @apply border-b-2 border-solid border-gray-800 align-bottom;
}

:deep(.akn-act td),
:deep(.akn-act th) {
  @apply p-4 align-top; /* Used to be 1mm at gesetze-im-internet.de */
}

:deep(.akn-act table) {
  @apply mb-8 max-w-full border-collapse overflow-x-auto border-gray-800;

  .rowsep {
    @apply border-b;
  }
  .colsep {
    @apply border-r;
  }
}

/* see https://stackoverflow.com/questions/13352080/match-all-elements-having-class-name-starting-with-a-specific-string */
:deep(.akn-act *[class^="frame-"]),
:deep(.akn-act *[class*=" frame-"]) {
  @apply border-solid border-gray-800;
}

:deep(.akn-act .frame-all) {
  @apply border;
}
:deep(.akn-act .frame-top) {
  @apply border-t;
}
:deep(.akn-act .frame-right) {
  @apply border-r;
}
:deep(.akn-act .frame-bottom) {
  @apply border-b;
}
:deep(.akn-act .frame-left) {
  @apply border-l;
}

:deep(.akn-act pre) {
  @apply ris-body3-regular overflow-x-auto font-[monospace];
}

:deep(.akn-act .akn-paragraph),
:deep(.akn-mainBody) {
  @apply overflow-x-auto;
}

/*
Attributes from the Juris CALS format without a corresponding HTML attribute migrated as classes
*/
:deep(.akn-act .valign-top) {
  @apply align-top;
}
:deep(.akn-act .valign-middle) {
  @apply align-middle;
}
:deep(.akn-act .valign-bottom) {
  @apply align-bottom;
}
:deep(.akn-act .align-center) {
  @apply text-center;
  img {
    @apply inline-block;
  }
}

/* Hide headings for single article view */
.single-article :deep(h2.einzelvorschrift) {
  @apply hidden;
}

/* Single article inline heading overrides */
.single-article :deep(.akn-num.inline),
.single-article :deep(.akn-heading.inline) {
  @apply hidden;
}

/* highlight unimplemented structures */
:deep(.unimplementiert) {
  @apply bg-yellow-100;
}

:deep(.akn-act > .dokumentenkopf) {
  @apply hidden;
}

/* hide default table of contents, since it will be displayed behind an accordion section */
:deep(.eingangsformel .inhaltsuebersicht) {
  @apply hidden;
}

/*   Regelungstext-Hauptteil
     ======================= */

:deep(.einzelvorschrift) {
  @apply mb-16;
}

/* Einzelvorschrift :: Überschrift  */
:deep(h2.einzelvorschrift) {
  @apply ris-heading3-bold my-24 inline-block break-after-avoid;
}

:deep(.akn-heading a) {
  @apply text-blue-800;
}

:deep(.akn-p) {
  @apply mb-8;
}

:deep(.akn-act),
:deep(.akn-doc) {
  .akn-book,
  .akn-part,
  .akn-chapter,
  .akn-subchapter,
  .akn-section,
  .akn-subsection,
  .akn-title,
  .akn-subtitle,
  .akn-num,
  .akn-heading,
  .akn-paragraph,
  ol,
  h2,
  p,
  table {
    @apply max-w-prose;
  }

  table.pgwide {
    @apply w-full max-w-full;
  }
}

/*
  Disable default list style. Instead, use the <li> attribute `data-aufzählungsliteral`
  as the :before element's content.
*/
:deep(.juristischer-absatz-untergliederung) {
  @apply list-none;
}
:deep(.juristischer-absatz-untergliederung li::before) {
  content: attr(data-aufzählungsliteral) /* Leerzeichen einfügen */ " ";
  @apply mr-4;
}
:deep(.juristischer-absatz-untergliederung li div) {
  @apply inline;
}

:deep(hr.trennlinie) {
  @apply mx-[30%] my-28 border-t border-black;
}

:deep(.signatur) {
  @apply my-32 text-center;
}

/*
 Section separators
 ======
 The following is the LegalDocML.de nesting hierarchy.
 Apply different border widths depending on level.
 */
:deep(.akn-book > .akn-num),
:deep(.akn-part > .akn-num),
:deep(.akn-chapter > .akn-num),
:deep(.akn-subchapter > .akn-num),
:deep(.akn-section > .akn-num),
:deep(.akn-subsection > .akn-num),
:deep(.akn-title > .akn-num),
:deep(.akn-subtitle > .akn-num) {
  @apply ris-heading3-bold float-none mb-4 block text-center;
}

:deep(.akn-book > .akn-heading),
:deep(.akn-part > .akn-heading),
:deep(.akn-chapter > .akn-heading),
:deep(.akn-subchapter > .akn-heading),
:deep(.akn-section > .akn-heading),
:deep(.akn-subsection > .akn-heading),
:deep(.akn-title > .akn-heading),
:deep(.akn-subtitle > .akn-heading) {
  @apply ris-heading3-regular float-none mb-24 block border-b-4 border-gray-600 pb-16 text-center text-gray-900;
}
:deep(.akn-book > .akn-heading),
:deep(.akn-part > .akn-heading) {
  @apply border-b-10;
}
:deep(.akn-chapter > .akn-heading) {
  @apply border-b-8;
}
:deep(.akn-subchapter > .akn-heading),
:deep(.akn-section > .akn-heading) {
  @apply border-b-[6px];
}

:deep(.akn-book + .akn-book),
:deep(.akn-part + .akn-part),
:deep(.akn-chapter + .akn-chapter),
:deep(.akn-subchapter + .akn-subchapter),
:deep(.akn-section + .akn-section),
:deep(.akn-subsection + .akn-subsection),
:deep(.akn-title + .akn-title),
:deep(.akn-subtitle + .akn-subtitle) {
  @apply mt-80 block;
}

/*   Darstellung von Fußnoten
     ====== */

:deep(.fussnoten),
:deep(.nichtamtliche-fussnoten) {
  @apply ris-body2-regular my-16 list-none pl-0 text-gray-900;
}

/* Show a 10 character wide separator above the collected notes  */
:deep(.fussnoten:before),
:deep(.nichtamtliche-fussnoten:before) {
  content: "";
  display: block;
  width: 10ch;
  height: 1px;
  @apply mb-10 bg-gray-900;
}

/* Do not show separator line if the non-authorial notes are preceded by authorial notes */
:deep(.fussnoten + .nichtamtliche-fussnoten:before) {
  @apply content-[initial];
}

/* Zeige Aufzählungszeichen und dazugehörigen Text nebeneinander an */
:deep(.fussnote) {
  @apply flex;
}

/* Mindestbreite für Aufzählungszeichen */
:deep(.fussnote .marker) {
  @apply min-w-32 align-super text-sm;
}

/* Stelle sicher, dass Aufzählungszeichen und p auf der gleichen Zeile stehen */
:deep(.fussnote .marker + p) {
  @apply mt-0;
}

/* Hebe Fußnoten hervor, wenn direkt zu ihnen navigiert wurde */
:deep(.fussnote:target) {
  @apply bg-yellow-200;
}

/* Blende Rückverweis-Links in Print-Version aus */
:deep(.fussnoten .rueckverweis) {
  @apply print:hidden;
}

/* Display blockLists with indent */
:deep(.akn-blockList) {
  @apply mb-8;

  .akn-item {
    @apply flex items-start;
  }

  /* Ensure the num has a min-width of 60px (with margin), taking more space if needed */
  .akn-item > .akn-num {
    @apply float-none mr-4 shrink grow-0 basis-56;
  }
  .akn-item > .content {
    @apply shrink grow basis-0;
  }
}

:deep(.norm-pdf-link) {
  @apply inline-flex items-center;
}

:deep(.norm-pdf-link::before) {
  @apply mr-8 inline-block h-24 w-24 bg-current mask-contain content-['_'];
  mask: url("~/assets/img/file.svg") no-repeat center;
}

/* Print styles for legislation content */
:deep(.akn-section),
:deep(.akn-subsection) {
  @apply break-inside-avoid;

  .akn-num,
  .akn-heading {
    @apply break-after-avoid-page;
  }
}

:deep(.extra_letter_spacing) {
  @apply tracking-[0.2em]; /* following styling in gesetze-im-internet.de */
}
</style>
