import { describe, expect } from "vitest";
import type { QueryBuilderRow } from "~/components/Search/Query/data";
import { FIELD_TYPE, INPUT_ELEMENT } from "~/components/Search/Query/data";
import { buildTerm } from "~/components/Search/Query/logic";

describe("logic", () => {
  it("returns the correct value for a simple term", () => {
    const row: QueryBuilderRow = {
      searchValue: "Frühstück",
      rowLogicOp: { label: "und", value: "AND" },
      selectedField: { type: FIELD_TYPE.TEXT, value: "Titel" },
      selectedLogic: {
        value: "containsExactPhrase",
        label: "containsExactPhrase",
        inputElement: INPUT_ELEMENT.TEXT,
      },
    };

    expect(buildTerm(row)).toEqual({
      field: "Titel",
      term: '"Frühstück"',
    });
  });

  it("negates a value correctly", () => {
    const row: QueryBuilderRow = {
      searchValue: "Frühstück",
      rowLogicOp: { label: "und", value: "AND" },
      selectedField: { type: FIELD_TYPE.TEXT, value: "Titel" },
      selectedLogic: {
        value: "doesNotContainExactPhrase",
        label: "doesNotContainExactPhrase",
        inputElement: INPUT_ELEMENT.TEXT,
      },
    };

    expect(buildTerm(row)).toEqual({
      term: '(-Titel:"Frühstück")',
    });
  });
});
