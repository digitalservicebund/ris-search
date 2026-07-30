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

interface ArticleVersionInfo {
  temporalCoverage: string;
  exists: boolean;
}

// Maps a version's legislationIdentifier to whether the current eId exists
// in that version and, if so, the matching article's own temporalCoverage.
// Falls back to an optimistic "exists" with the whole expression's
// temporalCoverage until the lookup resolves, which means consecutive
// versions only collapse into one group once we've confirmed the article
// itself is unchanged between them.
const articleVersionInfo = ref<Record<string, ArticleVersionInfo>>({});

async function loadArticleVersionInfo(versions: LegislationExpression[]) {
  await Promise.all(
    versions.map(async (version) => {
      if (articleVersionInfo.value[version.legislationIdentifier]) return;
      const expressionEli = version.legislationIdentifier.replace(/^eli\//, "");
      try {
        const metadata = await $risBackend<LegislationExpression>(
          `/v1/legislation/eli/${expressionEli}`,
        );
        const matchedPart = findPartByEId(metadata.hasPart, props.eId);
        articleVersionInfo.value[version.legislationIdentifier] = matchedPart
          ? { temporalCoverage: matchedPart.temporalCoverage, exists: true }
          : { temporalCoverage: version.temporalCoverage, exists: false };
      } catch {
        // Transient fetch error: assume the article still exists rather than
        // wrongly reporting it as removed.
        articleVersionInfo.value[version.legislationIdentifier] = {
          temporalCoverage: version.temporalCoverage,
          exists: true,
        };
      }
    }),
  );
}

// Prototype-only: fetches full metadata for every version to determine
// whether the article's text (rather than just the whole expression) changed
// between them, and whether the article exists in that version at all.
// Client-only since it's a UX enhancement layered on top of the
// already-rendered list, not needed for SSR/first paint.
if (import.meta.client) {
  watch(() => props.versions, loadArticleVersionInfo, { immediate: true });
}

interface VersionGroup {
  exists: boolean;
  // Meaningful only when exists === true; the article-level temporalCoverage
  // shared by every member of the group.
  temporalCoverage: string;
  // The earliest version in the group is the one that introduced this
  // wording, matching the group's "Gültig ab" date, and serves as the link
  // target for the whole group.
  earliestVersion: LegislationExpression;
  // The latest (newest) version in the group, used to derive a display range
  // for "not found" groups, which have no single meaningful temporalCoverage.
  latestVersion: LegislationExpression;
  memberCount: number;
  containsCurrent: boolean;
}

// Consecutive versions (in descending order) sharing the same article-level
// temporalCoverage represent an unchanged article text and are collapsed
// into a single group. Consecutive versions where the eId doesn't exist at
// all are collapsed into their own group, regardless of their individual
// expression dates, since "not present" is the only claim being made there.
const groupedVersions = computed<VersionGroup[]>(() => {
  const groups: VersionGroup[] = [];
  for (const version of sortedVersions.value) {
    const info = articleVersionInfo.value[version.legislationIdentifier];
    const exists = info?.exists ?? true;
    const coverageKey = exists
      ? (info?.temporalCoverage ?? version.temporalCoverage)
      : "not-found";
    const isCurrent =
      version.legislationIdentifier === props.currentLegislationIdentifier;

    const lastGroup = groups.at(-1);
    const sameGroup =
      lastGroup &&
      lastGroup.exists === exists &&
      (!exists || lastGroup.temporalCoverage === coverageKey);

    if (sameGroup) {
      lastGroup.earliestVersion = version;
      lastGroup.memberCount += 1;
      lastGroup.containsCurrent ||= isCurrent;
    } else {
      groups.push({
        exists,
        temporalCoverage: coverageKey,
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
      eId: props.eId,
    },
  };
}

function formatValidity(temporalCoverage: string): string {
  const interval = temporalCoverageToValidityInterval(temporalCoverage);
  const from = dateFormattedDDMMYYYY(interval?.from) ?? "unbekannt";
  const to = dateFormattedDDMMYYYY(interval?.to) ?? "unbekannt";
  return `Gültig ab ${from} – Gültig bis ${to}`;
}

// "Not found" groups have no single meaningful temporalCoverage (each member
// expression has its own), so this spans the group's newest-to-oldest
// expression dates instead as an approximate range.
function formatGroupRange(group: VersionGroup): string {
  if (group.exists) return formatValidity(group.temporalCoverage);
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
          :to="getRouteForVersion(group.earliestVersion)"
          class="typo-link-regular link-hover"
        >
          Zur Fassung
        </NuxtLink>
      </li>
    </ul>
  </section>
</template>
