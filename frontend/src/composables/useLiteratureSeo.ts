export type UseLiteratureSeoInput = {
  documentTypes: string[];
  yearsOfPublication: string[];
  headline?: string;
  alternativeHeadline?: string;
};

export function useLiteratureSeo({
  documentTypes,
  yearsOfPublication,
  headline,
  alternativeHeadline,
}: UseLiteratureSeoInput) {
  useSeo({
    title: buildTitle({
      documentTypes,
      yearsOfPublication,
      headline,
      alternativeHeadline,
    }),
  });
}

function buildTitle({
  documentTypes,
  yearsOfPublication,
  headline,
  alternativeHeadline,
}: {
  documentTypes: string[];
  yearsOfPublication: string[];
  headline?: string;
  alternativeHeadline?: string;
}) {
  const parts = [
    documentTypes[0] ?? "",
    yearsOfPublication[0] ?? "",
    (headline || alternativeHeadline) ?? "",
  ].filter((it) => !isStringEmpty(it));

  return parts.join(", ");
}
