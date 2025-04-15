import { toString } from "@hyperdx/lucene";
import type { QueryBuilderRow } from "~/components/Search/Query/data";
import { INPUT_ELEMENT } from "~/components/Search/Query/data";

export interface AstNode {
  field?: string;
  term?: string;
  left?: AstNode;
  operator?: string;
  right?: AstNode;
}

function negate(node: AstNode): AstNode {
  return { term: `(-${toString(node)})` };
}

export function buildTerm(row: QueryBuilderRow): AstNode | undefined {
  if (!row.selectedField || !row.selectedLogic || !row.searchValue) return;

  const field = row.selectedField.value;

  // most logical options are not implemented yet

  if (
    row.selectedLogic.buildTerm &&
    row.selectedLogic.inputElement === INPUT_ELEMENT.DATE
  ) {
    const formattedDate = row.searchValue
      .split(".")
      .reverse()
      .map((part) => part.padStart(2, "0"))
      .join("-"); // use dayjs instead?
    const { term, negated } = row.selectedLogic.buildTerm(formattedDate);
    if (negated) return negate({ field, term });
    else return { field, term };
  }

  let term: string;

  if (
    row.selectedLogic.value === "containsExactPhrase" ||
    row.selectedLogic.value === "doesNotContainExactPhrase"
  ) {
    term = `"${row.searchValue}"`;
  } else {
    term = row.searchValue;
  }

  const node = {
    field,
    term,
  };

  if (row.selectedLogic.value === "doesNotContainExactPhrase") {
    return negate(node);
  }

  return node;
}
