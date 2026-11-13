import type { RouteLocationRaw } from "#vue-router";
import type {
  Rechtsprechung,
  ZitierungLiteraturSelbststaendig,
  ZitierungLiteraturUnselbststaendig,
  ZitierungRechtsprechung,
} from "~/types/api";
import { dateFormattedLongGerman } from "~/utils/dateFormatting";
import { isStringEmpty } from "~/utils/textFormatting";

export type ZitierungLink = {
  text: string;
  to?: RouteLocationRaw;
};

export type Zitierungen = {
  aktivRechtsprechung: ZitierungLink[];
  passivRechtsprechung: ZitierungLink[];
  passivLiteratur: ZitierungLink[];
};

type Zitierung = { richtung?: "aktiv" | "passiv"; dokumentnummer?: string };

function isNotBlank(value: string | null | undefined): value is string {
  return !isStringEmpty(value);
}

function joinNonBlank(parts: (string | undefined)[], separator: string) {
  return parts.filter(isNotBlank).join(separator);
}

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

function toZitierungLink(
  zitierung: Zitierung,
  text: string | undefined,
  basePath: string,
  from?: string,
): ZitierungLink | undefined {
  if (!text) return undefined;
  if (!zitierung.dokumentnummer) return { text };

  return {
    text,
    to: { path: `${basePath}/${zitierung.dokumentnummer}`, query: { from } },
  };
}

function toRechtsprechungLinks(
  zitierungen: ZitierungRechtsprechung[],
  from?: string,
): ZitierungLink[] {
  return zitierungen.flatMap((zitierung) => {
    const text = formatZitierungRechtsprechung(zitierung);
    return (
      toZitierungLink(zitierung, text, "/gerichtsentscheidungen", from) ?? []
    );
  });
}

function toLiteraturSelbststaendigLinks(
  zitierungen: ZitierungLiteraturSelbststaendig[],
  from?: string,
): ZitierungLink[] {
  return zitierungen.flatMap((zitierung) => {
    const text = formatZitierungLiteraturSelbststaendig(zitierung);
    return toZitierungLink(zitierung, text, "/literaturnachweise", from) ?? [];
  });
}

function toLiteraturUnselbststaendigLinks(
  zitierungen: ZitierungLiteraturUnselbststaendig[],
  from?: string,
): ZitierungLink[] {
  return zitierungen.flatMap((zitierung) => {
    const text = formatZitierungLiteraturUnselbststaendig(zitierung);
    return toZitierungLink(zitierung, text, "/literaturnachweise", from) ?? [];
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
    aktivRechtsprechung: toRechtsprechungLinks(
      rechtsprechungen.filter(isAktiv),
      from,
    ),
    passivRechtsprechung: toRechtsprechungLinks(
      rechtsprechungen.filter(isPassiv),
      from,
    ),
    passivLiteratur: [
      ...toLiteraturSelbststaendigLinks(selbststaendig.filter(isPassiv), from),
      ...toLiteraturUnselbststaendigLinks(
        unselbststaendig.filter(isPassiv),
        from,
      ),
    ],
  };

  const hasZitierungen = Object.values(zitierungen).some(
    (links) => links.length > 0,
  );
  return hasZitierungen ? zitierungen : undefined;
}
