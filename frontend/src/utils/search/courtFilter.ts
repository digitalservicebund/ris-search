// Court id -> label, shared across all CourtFilter instances (module scope,
// not component state) so a label already seen survives a component like
// the mobile filter drawer unmounting and remounting.
export const knownCourtLabels = new Map<string, string>();

export const courtFilterDefaultSuggestions = [
  { id: "BVerfG", label: "Bundesverfassungsgericht" },
  { id: "BGH", label: "Bundesgerichtshof" },
  { id: "BVerwG", label: "Bundesverwaltungsgericht" },
  { id: "BFH", label: "Bundesfinanzhof" },
  { id: "BAG", label: "Bundesarbeitsgericht" },
  { id: "BSG", label: "Bundessozialgericht" },
  { id: "BPatG", label: "Bundespatentgericht" },
  {
    id: "GmSOGB",
    label: "Gemeinsamer Senat der obersten Gerichtshöfe des Bundes",
  },
];
