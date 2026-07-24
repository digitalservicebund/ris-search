import { DocumentKind } from "~/types/api";
import { sortMode } from "~/utils/search/sortMode";

export type SortOption = { label: string; value: string };

const reversedSortMode = (name: string) => "-" + name;

const sharedSortOptions: SortOption[] = [
  { label: "Relevanz", value: "default" },
  { label: "Datum: Älteste zuerst", value: sortMode.date },
  { label: "Datum: Neueste zuerst", value: reversedSortMode(sortMode.date) },
];

const caselawSortOptions: SortOption[] = [
  { label: "Relevanz", value: "default" },
  { label: "Gericht: Von A nach Z", value: sortMode.courtName },
  {
    label: "Gericht: Von Z nach A",
    value: reversedSortMode(sortMode.courtName),
  },
  { label: "Entscheidungsdatum: Älteste zuerst", value: sortMode.date },
  {
    label: "Entscheidungsdatum: Neueste zuerst",
    value: reversedSortMode(sortMode.date),
  },
];

const legislationSortOptions: SortOption[] = [
  { label: "Relevanz", value: "default" },
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
