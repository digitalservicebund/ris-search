import { DocumentKind } from "~/types";

export enum FIELD_TYPE {
  ENTITY = 1, // the chips are entities, no text input
  TEXT = 2,
  DATE = 3,
  ERROR = 4,
}

type FIELD = {
  label: string;
  value: string;
  documentKind: string;
  type: FIELD_TYPE;
  enabled: boolean;
};

export function fieldTypeToLogicOptions(fieldType: FIELD_TYPE) {
  switch (fieldType) {
    case FIELD_TYPE.ENTITY:
      return logicOptionsEntity;
    case FIELD_TYPE.TEXT:
      return logicOptionsText;
    case FIELD_TYPE.DATE:
      return logicOptionsDate;
    case FIELD_TYPE.ERROR:
      return logicOptionsError;
    default:
      throw new Error("Unknown field type");
  }
}

export enum INPUT_ELEMENT {
  NONE = 1, // the dropdown selection is enough, e.g. "is empty"
  CHIPS = 2,
  TEXT = 3,
  DATE = 4,
  DATE_RANGE = 5,
  MONTH = 6,
  MONTH_RANGE = 7,
  YEAR = 8,
  YEAR_RANGE = 9,
}

export type RangeTypeInputElement =
  | INPUT_ELEMENT.DATE_RANGE
  | INPUT_ELEMENT.MONTH_RANGE
  | INPUT_ELEMENT.YEAR_RANGE;

export interface RangeString {
  start: string;
  end: string;
}

export type TermBuilder<T = string> = (from: T) => {
  negated?: boolean;
  term: string;
};

export interface SelectedLogic {
  label: string;
  value: string;
  inputElement: Exclude<INPUT_ELEMENT, RangeTypeInputElement>;
  buildTerm?: TermBuilder;
}
export interface SelectedLogicDateRange {
  label: string;
  value: string;
  inputElement: RangeTypeInputElement;
  buildTerm?: TermBuilder<RangeString>;
}

export interface QueryBuilderRow {
  rowLogicOp: RowLogicOperator;
  selectedField: { type: FIELD_TYPE; value: string } | "";
  selectedLogic: SelectedLogic | "";
  searchValue: string;
}

export interface RowLogicOperator {
  label: string;
  value: string;
}

export const andLogicOperator: RowLogicOperator = {
  label: "und",
  value: "AND",
};

export const orLogicOperator: RowLogicOperator = { label: "oder", value: "OR" };

export const rowLogicOperators: RowLogicOperator[] = [
  andLogicOperator,
  orLogicOperator,
];

const logicOptionsDate: (SelectedLogic | SelectedLogicDateRange)[] = [
  {
    label: "ist am",
    value: "isOn",
    inputElement: INPUT_ELEMENT.DATE,
    buildTerm: (value) => ({ term: value }),
  },
  {
    label: "ist nicht am",
    value: "isNotOn",
    inputElement: INPUT_ELEMENT.DATE,
    buildTerm: (value) => ({ negated: true, term: value }),
  },
  {
    label: "ist vor dem",
    value: "isBefore",
    inputElement: INPUT_ELEMENT.DATE,
    buildTerm: (value) => ({ term: `[* TO ${value}]` }),
  },
  {
    label: "ist nach dem",
    value: "isAfter",
    inputElement: INPUT_ELEMENT.DATE,
    buildTerm: (value) => ({ term: `[${value} TO *]` }),
  },
  {
    label: "liegt im Zeitraum",
    value: "isInRange",
    inputElement: INPUT_ELEMENT.DATE_RANGE,
    buildTerm: (value) => ({
      term: `[${value.start} TO ${value.end}]`,
    }),
  },
  {
    label: "liegt nicht im Zeitraum",
    value: "isNotInRange",
    inputElement: INPUT_ELEMENT.DATE_RANGE,
    buildTerm: (value) => ({
      negated: true,
      term: `[${value.start} TO ${value.end}]`,
    }),
  },
  {
    label: "liegt in den Monaten",
    value: "isInMonthRange",
    inputElement: INPUT_ELEMENT.MONTH_RANGE,
    buildTerm: (value) => ({
      term: `[${value.start}-01 TO ${value.end}-31]`,
    }),
  },
  {
    label: "liegt in den Jahren",
    value: "isInYearRange",
    inputElement: INPUT_ELEMENT.YEAR_RANGE,
    buildTerm: (value) => ({
      term: `[${value.start}-01-01 TO ${value.end}-12-31]`,
    }),
  },
  {
    label: "liegt im Monat",
    value: "isInMonth",
    inputElement: INPUT_ELEMENT.MONTH,
    buildTerm: (value) => ({
      term: `[${value}-01 TO ${value}-31]`,
    }),
  },
  {
    label: "liegt im Jahr",
    value: "isInYear",
    inputElement: INPUT_ELEMENT.YEAR,
    buildTerm: (value) => ({
      term: `[${value}-01-01 TO ${value}-12-31]`,
    }),
  },
  {
    label: "ist leer",
    value: "isEmpty",
    inputElement: INPUT_ELEMENT.NONE,
    buildTerm: () => ({ negated: true, term: `_exists_:` }),
  },
  {
    label: "ist nicht leer",
    value: "isNotEmpty",
    inputElement: INPUT_ELEMENT.NONE,
    buildTerm: () => ({ term: `_exists_:` }),
  },
];

export const logicOptionsEntity = [
  {
    label: "treffen alle zu",
    value: "allApply",
    inputElement: INPUT_ELEMENT.CHIPS,
  },
  {
    label: "trifft min. eins zu",
    value: "appliesToAtLeastOne",
    inputElement: INPUT_ELEMENT.CHIPS,
  },
  {
    label: "trifft keins zu",
    value: "noneApplies",
    inputElement: INPUT_ELEMENT.CHIPS,
  },
  { label: "ist leer", value: "isEmpty", inputElement: INPUT_ELEMENT.NONE },
  {
    label: "ist nicht leer",
    value: "isNotEmpty",
    inputElement: INPUT_ELEMENT.NONE,
  },
];

export const logicOptionsText = [
  {
    label: "enthält alle Wörter",
    value: "containsAllWords",
    inputElement: INPUT_ELEMENT.CHIPS,
  },
  {
    label: "enthält eins der Wörter",
    value: "containsAnyOfWords",
    inputElement: INPUT_ELEMENT.CHIPS,
  },
  {
    label: "enthält genau diese Phrase",
    value: "containsExactPhrase",
    inputElement: INPUT_ELEMENT.TEXT,
  },
  {
    label: "enthält keins der Wörter",
    value: "containsNoneOfWords",
    inputElement: INPUT_ELEMENT.CHIPS,
  },
  {
    label: "enthält nicht genau diese Phrase",
    value: "doesNotContainExactPhrase",
    inputElement: INPUT_ELEMENT.TEXT,
  },
  { label: "ist", value: "equals", inputElement: INPUT_ELEMENT.CHIPS },
  {
    label: "ist nicht",
    value: "doesNotEqual",
    inputElement: INPUT_ELEMENT.CHIPS,
  },
  { label: "ist leer", value: "isEmpty", inputElement: INPUT_ELEMENT.NONE },
  {
    label: "ist nicht leer",
    value: "isNotEmpty",
    inputElement: INPUT_ELEMENT.NONE,
  },
];

export const logicOptionsError = [
  { label: "ist vorhanden", value: "exists", inputElement: INPUT_ELEMENT.NONE },
  {
    label: "ist nicht vorhanden",
    value: "doesntExist",
    inputElement: INPUT_ELEMENT.NONE,
  },
  { label: "ist leer", value: "isEmpty", inputElement: INPUT_ELEMENT.NONE },
  {
    label: "ist nicht leer",
    value: "isNotEmpty",
    inputElement: INPUT_ELEMENT.NONE,
  },
];

// Based on https://digitalservicebund.atlassian.net/wiki/spaces/VER/database/1018265651 from 2024-04-30
// Fetch this from a source of truth that is shared with the backend?
type FieldContent = { label: string; value: string; fieldType?: FIELD_TYPE };
type DocumentKindFields = { type: DocumentKind; fields: FieldContent[] };

const fieldsByDocumentKind: DocumentKindFields[] = [
  {
    type: DocumentKind.CaseLaw,
    fields: [
      {
        label: "Aktenzeichen",
        value: "AKTENZEICHEN",
      },
      {
        label: "Gerichtstyp",
        value: "GERICHTSTYP",
      },
      {
        label: "Gerichtsort",
        value: "GERICHTSORT",
      },
      {
        label: "Abweichende Dokumentnummer",
        value: "DATUM",
      },
      {
        label: "Abweichende Meinung",
        value: "ABWMEIN",
      },
      {
        label: "Dokstelle",
        value: "DOKSTELLE",
      },
      {
        label: "Dokumentnummer",
        value: "DOKUMENTNUMMER",
      },
      {
        label: "Dokumenttyp",
        value: "DOKUMENTTYP",
      },
      {
        label: "ECLI",
        value: "ECLI",
      },
      {
        label: "Entscheidungsdatum",
        value: "DATUM",
      },
      {
        label: "Entscheidungsgründe",
        value: "ENTSCHEIDUNGSGRUENDE",
      },
      {
        label: "Entscheidungsname",
        value: "ENTSCHEIDUNGSNAME",
      },
      {
        label: "Fehler",
        value: "FEHLER",
      },
      {
        label: "Gliederung",
        value: "GLIEDERUNG",
      },
      {
        label: "Gründe",
        value: "GRUENDE",
      },
      {
        label: "Leitsatz",
        value: "LEITSATZ",
      },
      {
        label: "Orientierungssatz",
        value: "ORIENTIERUNGSSATZ",
      },
      {
        label: "Schlagwörter",
        value: "SCHLAGWOERTER",
      },
      {
        label: "Spruchkörper",
        value: "SPRUCHKOERPER",
      },
      {
        label: "Status",
        value: "STATUS",
      },
      {
        label: "Tatbestand",
        value: "TATBESTAND",
      },
      {
        label: "Tenor",
        value: "TENOR",
      },
      {
        label: "Titelzeile",
        value: "TITELZEILE",
      },
      {
        label: "Vorgang",
        value: "VORGANG",
      },
    ],
  },
  {
    type: DocumentKind.Norm,
    fields: [
      {
        label: "Amtliche Buchstabenabkürzung",
        value: "AB",
      },
      {
        label: "Amtliche Kurzüberschrift",
        value: "AK",
      },
      {
        label: "Amtliche Langüberschrift",
        value: "AL",
      },
      {
        label: "ELI",
        value: "work_eli",
      },
      {
        label: "Überschrift Einzelnorm",
        value: "UEE",
      },
      {
        label: "Text Einzelnorm",
        value: "TE",
      },
    ],
  },
  {
    type: DocumentKind.All,
    fields: [
      {
        label: "Datum",
        value: "DATUM",
        fieldType: FIELD_TYPE.DATE,
      },
      {
        label: "Titel",
        value: "TITEL",
      },
    ],
  },
];

export const fields = fieldsByDocumentKind.flatMap((documentKindFields) => {
  return documentKindFields.fields.map((record) =>
    getFieldForDocumentType(
      record.label,
      record.value,
      documentKindFields.type,
      record.fieldType ?? FIELD_TYPE.TEXT,
    ),
  );
});

function getFieldForDocumentType(
  label: string,
  value: string,
  documentType: DocumentKind,
  fieldType: FIELD_TYPE,
): FIELD {
  return {
    label: label,
    value: value,
    documentKind: documentType.valueOf(),
    type: fieldType,
    enabled: true,
  };
}
