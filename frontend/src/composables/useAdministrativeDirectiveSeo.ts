export type UseAdministrativeDirectiveSeoInput = {
  documentType?: string;
  entryIntoForceDate?: string;
  headline?: string;
};

export function useAdministrativeDirectiveSeo({
  documentType,
  entryIntoForceDate,
  headline,
}: UseAdministrativeDirectiveSeoInput) {
  useSeo({
    title: buildTitle({
      documentType,
      entryIntoForceDate,
      headline,
    }),
  });
}

function buildTitle({
  documentType,
  entryIntoForceDate,
  headline,
}: {
  documentType?: string;
  entryIntoForceDate?: string;
  headline?: string;
}) {
  const docType = documentType || "Verwaltungsvorschrift";
  const formattedDate = entryIntoForceDate
    ? ` vom ${dateFormattedDDMMYYYY(entryIntoForceDate)}`
    : "";
  const title = headline ? `, ${headline}` : "";

  return `${docType}${formattedDate}${title}`;
}
