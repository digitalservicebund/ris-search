import { truncateAtWord } from "~/utils/textFormatting";

export type UseTranslationSeoInput = {
  name?: string;
  translationOfWork?: string;
};

export function useTranslationSeo({
  name,
  translationOfWork,
}: UseTranslationSeoInput) {
  const titlePrefix = name || undefined ? `${name}, ` : "";
  const title = `${titlePrefix}English translation`;

  const base = name?.trim() || translationOfWork?.trim();

  const description = base
    ? truncateAtWord(
        `This is the English translation of the ${base}, provided by the German Federal Legal Information Portal. This translation is for informational purposes only. The German version is the only legally binding text.`,
        150,
      )
    : "";

  const ogTitle = base
    ? truncateAtWord(`${base} – English Translation`, 55)
    : "";

  useSeo({
    title,
    description,
    ogTitle,
  });
}
