import { type Literature } from "~/types/api";

export type UseLiteratureSeoInput = {
  literature?: Literature;
};

export function useLiteratureSeo({ literature }: UseLiteratureSeoInput) {
  useSeo({
    title: buildTitle(literature),
    description: "", // todo after main is merged an description is optional
  });
}

function buildTitle(literature?: Literature) {
  if (!literature) return "";
  const parts = [
    literature.documentTypes[0] ?? "",
    literature.yearsOfPublication[0] ?? "",
    (literature.headline || literature.alternativeHeadline) ?? "",
  ].filter((it) => !isStringEmpty(it));

  return parts.join(", ");
}
