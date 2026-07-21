<script setup lang="ts">
import { orderBy } from "lodash-es";
import type { RouteLocationRaw } from "#vue-router";
import type { LegislationExpression } from "~/types/api";

const props = defineProps<{
  status: string;
  currentLegislationIdentifier: string;
  versions: LegislationExpression[];
  eId: string;
}>();

const route = useRoute();
const { $risBackend } = useNuxtApp();

const sortedVersions = computed(() =>
  orderBy(props.versions, [(v) => v.temporalCoverage], ["desc"]),
);

// Maps a version's legislationIdentifier to its eId -> doknr lookup table,
// parsed from that version's raw LegalDocML XML, once fetched.
const doknrMaps = ref<Record<string, Map<string, string>>>({});

async function loadDoknrMap(version: LegislationExpression) {
  if (doknrMaps.value[version.legislationIdentifier]) return;
  const xmlUrl = getManifestationUrl(version, "application/xml");
  if (!xmlUrl) return;
  try {
    const xml = await $risBackend<string>(xmlUrl, {
      headers: { Accept: "application/xml" },
    });
    doknrMaps.value[version.legislationIdentifier] = parseDoknrMap(xml);
  } catch {
    doknrMaps.value[version.legislationIdentifier] = new Map();
  }
}

// Prototype-only: fetches the raw XML for every version to read its doknr
// table. Client-only since it's a UX enhancement layered on top of the
// already-rendered list, not needed for SSR/first paint.
if (import.meta.client) {
  watch(
    () => props.versions,
    (versions) => {
      void Promise.all(versions.map(loadDoknrMap));
    },
    { immediate: true },
  );
}

// The doknr for the current article, looked up in the current version's own
// map (fetched via the same loop, since the current expression is itself one
// of `props.versions`). Its stable-id prefix is the reference every other
// version is matched against.
const currentDoknr = computed(() =>
  doknrMaps.value[props.currentLegislationIdentifier]?.get(props.eId),
);
const stableId = computed(() =>
  currentDoknr.value ? getDoknrStableId(currentDoknr.value) : undefined,
);

// Finds the entry in a version's doknr map matching the reference stable id,
// regardless of which eId it's attached to in that version — this is what
// lets a renumbered provision (different eId, same doknr stable id) still be
// found and linked correctly.
function findMatch(
  version: LegislationExpression,
): { eId: string; doknr: string } | undefined {
  const map = doknrMaps.value[version.legislationIdentifier];
  if (!map || !stableId.value) return undefined;
  for (const [eId, doknr] of map) {
    if (doknr.startsWith(stableId.value)) return { eId, doknr };
  }
  return undefined;
}

interface VersionGroup {
  exists: boolean;
  groupKey: string; // the matched doknr, or "not-found"
  matchedEId: string | undefined; // target eId for this group's link; undefined when !exists
  earliestVersion: LegislationExpression; // oldest member — link target, "Gültig ab" anchor
  latestVersion: LegislationExpression; // newest member — "Gültig bis" anchor
  memberCount: number;
  containsCurrent: boolean;
}

// Consecutive versions (in descending order) sharing the same doknr
// represent an unchanged article text (the version counter in the doknr only
// steps when that provision's content actually changed) and are collapsed
// into a single group. Versions whose XML hasn't loaded yet are skipped for
// now and will appear once resolved.
const groupedVersions = computed<VersionGroup[]>(() => {
  if (!stableId.value) return [];
  const groups: VersionGroup[] = [];
  for (const version of sortedVersions.value) {
    if (!doknrMaps.value[version.legislationIdentifier]) continue;

    const match = findMatch(version);
    const exists = !!match;
    const groupKey = match?.doknr ?? "not-found";
    const isCurrent =
      version.legislationIdentifier === props.currentLegislationIdentifier;

    const lastGroup = groups.at(-1);
    if (lastGroup && lastGroup.groupKey === groupKey) {
      lastGroup.earliestVersion = version;
      lastGroup.memberCount += 1;
      lastGroup.containsCurrent ||= isCurrent;
    } else {
      groups.push({
        exists,
        groupKey,
        matchedEId: match?.eId,
        earliestVersion: version,
        latestVersion: version,
        memberCount: 1,
        containsCurrent: isCurrent,
      });
    }
  }
  return groups;
});

function getRouteForVersion(
  version: LegislationExpression,
  matchedEId: string,
): RouteLocationRaw | undefined {
  // legislationIdentifier e.g. "eli/bund/bgbl-1/1975/s1760/1998-01-29/10/deu"
  // -> last 3 segments are [pointInTime, version, language]
  const [pointInTime, versionSegment, language] = version.legislationIdentifier
    .split("/")
    .slice(-3);
  if (!pointInTime || !versionSegment || !language) return undefined;

  return {
    name: route.name ?? undefined,
    query: route.query,
    params: {
      ...route.params,
      pointInTime,
      version: versionSegment,
      language,
      eId: matchedEId,
    },
  };
}

// No hasPart-derived temporalCoverage is available in this approach, so every
// group's displayed range is derived from its own constituent expressions'
// dates instead.
function formatGroupRange(group: VersionGroup): string {
  const from = temporalCoverageToValidityInterval(
    group.earliestVersion.temporalCoverage,
  )?.from;
  const to = temporalCoverageToValidityInterval(
    group.latestVersion.temporalCoverage,
  )?.to;
  return `Gültig ab ${dateFormattedDDMMYYYY(from) ?? "unbekannt"} – Gültig bis ${dateFormattedDDMMYYYY(to) ?? "unbekannt"}`;
}
</script>

<template>
  <section
    v-if="status === 'success' && groupedVersions.length"
    aria-label="Andere Fassungen dieses Paragrafen"
    class="mt-24"
  >
    <h2 class="typo-label1-bold mb-8">
      Andere Fassungen dieses Paragrafen (Prototyp)
    </h2>
    <ul class="space-y-4">
      <li
        v-for="group in groupedVersions"
        :key="group.earliestVersion.legislationIdentifier"
        class="flex flex-wrap items-center justify-between gap-8"
      >
        <span class="typo-body-regular">
          {{ formatGroupRange(group) }}
          <template v-if="group.exists && group.memberCount > 1">
            ({{ group.memberCount }} Fassungen, Paragraf unverändert)
          </template>
        </span>

        <span v-if="!group.exists" class="typo-body-regular text-gray-900">
          Paragraf existiert in dieser Fassung nicht
        </span>
        <span
          v-else-if="group.containsCurrent"
          class="typo-body-regular text-gray-900"
        >
          Aktuelle Fassung
        </span>
        <NuxtLink
          v-else
          :to="getRouteForVersion(group.earliestVersion, group.matchedEId!)"
          class="typo-link-regular link-hover"
        >
          Zur Fassung
        </NuxtLink>
      </li>
    </ul>
  </section>
</template>
