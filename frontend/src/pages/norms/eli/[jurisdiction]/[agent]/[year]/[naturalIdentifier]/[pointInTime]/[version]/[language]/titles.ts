import type { LegislationWork } from "~/types";

export function getNormBreadcrumbTitle(norm: LegislationWork): string {
  return norm.abbreviation || norm.alternateName || norm.name || "";
}

export function getNormTitle(norm: LegislationWork): string {
  return norm.name || norm.alternateName || norm.abbreviation || "";
}
