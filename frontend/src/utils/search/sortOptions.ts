import { DocumentKind } from "~/types/api";
import { sortMode } from "~/utils/search/sortMode";

export type SortOption = { label: string; value: string };

const reversedSortMode = (name: string) => "-" + name;

const relevanceSortOption: SortOption = { label: "Relevanz", value: "default" };

const dateSortOptions: SortOption[] = [
  { label: "Datum: Älteste zuerst", value: sortMode.date },
  { label: "Datum: Neueste zuerst", value: reversedSortMode(sortMode.date) },
];

const sharedSortOptions: SortOption[] = [
  relevanceSortOption,
  ...dateSortOptions,
];

const caselawSortOptions: SortOption[] = [
  relevanceSortOption,
  { label: "Gericht: Von A nach Z", value: sortMode.courtName },
  {
    label: "Gericht: Von Z nach A",
    value: reversedSortMode(sortMode.courtName),
  },
  ...dateSortOptions,
];

const legislationSortOptions: SortOption[] = [
  relevanceSortOption,
  { label: "Ausfertigungsdatum: Älteste zuerst", value: sortMode.date },
  {
    label: "Ausfertigungsdatum: Neueste zuerst",
    value: reversedSortMode(sortMode.date),
  },
];

export function validSortOptions(documentKind: DocumentKind): SortOption[] {
  switch (documentKind) {
    case DocumentKind.Norm:
      return legislationSortOptions;
    case DocumentKind.CaseLaw:
      return caselawSortOptions;
    default:
      return sharedSortOptions;
  }
}
