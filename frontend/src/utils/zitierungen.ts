import type {
  Rechtsprechung,
  ZitierungLiteraturSelbststaendig,
  ZitierungLiteraturUnselbststaendig,
  ZitierungRechtsprechung,
} from "~/types/api";
import { dateFormattedLongGerman } from "~/utils/dateFormatting";
import { type DocumentLink, toDocumentLink } from "~/utils/documentLink";
import { isNotBlank, joinNonBlank } from "~/utils/textFormatting";

export type Zitierungen = {
  aktivRechtsprechung: DocumentLink[];
  passivRechtsprechung: DocumentLink[];
  passivLiteratur: DocumentLink[];
};

type Zitierung = Pick<ZitierungRechtsprechung, "richtung" | "dokumentnummer">;

/**
 * Formats a cited court decision, e.g. "Parallelentscheidung BVerwG Leipzig, 27.
 * August 2026 — 6 B 13.26"
 */
export function formatZitierungRechtsprechung(
  zitierung: ZitierungRechtsprechung,
): string | undefined {
  const { artDerZitierung, gericht, entscheidungsdatum, aktenzeichen } =
    zitierung;

  const artUndGericht = joinNonBlank(
    [artDerZitierung, gericht?.gerichtstyp, gericht?.gerichtsort],
    " ",
  );
  const datum = dateFormattedLongGerman(entscheidungsdatum);
  const herkunft = joinNonBlank([artUndGericht, datum], ", ");

  return joinNonBlank([herkunft, aktenzeichen], " — ") || undefined;
}

/**
 * Formats a cited dependent publication (ULI), e.g. "Kugelmüller-Pugh, Anette;
 * DStR (Deutsches Steuerrecht) 2026, 1636"
 */
export function formatZitierungLiteraturUnselbststaendig(
  zitierung: ZitierungLiteraturUnselbststaendig,
): string | undefined {
  const { autor, fundstelle } = zitierung;

  const titel = fundstelle?.periodikum?.titel;
  const titelInKlammern = isNotBlank(titel) ? `(${titel})` : undefined;
  const fundstelleFormatted = joinNonBlank(
    [
      fundstelle?.periodikum?.abkuerzung,
      titelInKlammern,
      fundstelle?.zitatstelle,
    ],
    " ",
  );

  return joinNonBlank([autor, fundstelleFormatted], "; ") || undefined;
}

/**
 * Formats a cited independent publication (SLI), e.g. "Böhringer, Ayse-Martina;
 * Pandemie im offenen Grundrechtsstaat 2021"
 */
export function formatZitierungLiteraturSelbststaendig(
  zitierung: ZitierungLiteraturSelbststaendig,
): string | undefined {
  const { autor, titel, veroeffentlichungsjahr } = zitierung;

  const werk = joinNonBlank([titel, veroeffentlichungsjahr], " ");

  return joinNonBlank([autor, werk], "; ") || undefined;
}

function toLinks<T extends Zitierung>(
  zitierungen: T[],
  format: (zitierung: T) => string | undefined,
  basePath: string,
  from?: string,
): DocumentLink[] {
  return zitierungen.flatMap((zitierung) => {
    const text = format(zitierung);
    const link = toDocumentLink(text, basePath, zitierung.dokumentnummer, from);
    return link ?? [];
  });
}

function isAktiv(zitierung: Zitierung): boolean {
  return zitierung.richtung === "aktiv";
}

function isPassiv(zitierung: Zitierung): boolean {
  return zitierung.richtung === "passiv";
}

/**
 * Collects the citations of a court decision that are shown in the Zitierungen
 * tab. Selbstständige and unselbstständige Literatur are merged into one list
 * (SLI first), because the API delivers them separately.
 *
 * @returns The citations, or undefined if there is nothing to show
 */
export function getZitierungen(
  rechtsprechung: Rechtsprechung,
  from?: string,
): Zitierungen | undefined {
  const rechtsprechungen = rechtsprechung.zitierungenRechtsprechung ?? [];
  const selbststaendig =
    rechtsprechung.zitierungenLiteraturSelbststaendig ?? [];
  const unselbststaendig =
    rechtsprechung.zitierungenLiteraturUnselbststaendig ?? [];

  const zitierungen: Zitierungen = {
    aktivRechtsprechung: toLinks(
      rechtsprechungen.filter(isAktiv),
      formatZitierungRechtsprechung,
      "/gerichtsentscheidungen",
      from,
    ),
    passivRechtsprechung: toLinks(
      rechtsprechungen.filter(isPassiv),
      formatZitierungRechtsprechung,
      "/gerichtsentscheidungen",
      from,
    ),
    passivLiteratur: [
      ...toLinks(
        selbststaendig.filter(isPassiv),
        formatZitierungLiteraturSelbststaendig,
        "/literaturnachweise",
        from,
      ),
      ...toLinks(
        unselbststaendig.filter(isPassiv),
        formatZitierungLiteraturUnselbststaendig,
        "/literaturnachweise",
        from,
      ),
    ],
  };

  const hasZitierungen = Object.values(zitierungen).some(
    (links) => links.length > 0,
  );
  return hasZitierungen ? zitierungen : undefined;
}
