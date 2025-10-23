<script setup lang="ts">
import type { Dayjs } from "dayjs";
import Toast from "primevue/toast";
import type { TreeNode } from "primevue/treenode";
import { computed, ref, onMounted } from "vue";
import type { ComputedRef } from "vue";
import { useRoute } from "#app";
import Accordion from "~/components/Accordion.vue";
import NormActionsMenu from "~/components/ActionMenu/NormActionsMenu.vue";
import ContentWrapper from "~/components/CustomLayouts/ContentWrapper.vue";
import TableOfContentsLayout from "~/components/CustomLayouts/SidebarLayout.vue";
import IncompleteDataMessage from "~/components/IncompleteDataMessage.vue";
import {
  linkTabBase,
  linkTabActive,
  linkTabInactive,
  linkTabNav,
  linkTabNavContainer,
  linkTabPanel,
} from "~/components/LinkTabs.styles";
import NormMetadataFields from "~/components/Norm/Metadatafields/NormMetadataFields.vue";
import NormVersionList from "~/components/Norm/NormVersionList.vue";
import NormVersionWarning from "~/components/Norm/NormVersionWarning.vue";
import VersionsTeaser from "~/components/Norm/VersionsTeaser.vue";
import NormHeadingGroup from "~/components/NormHeadingGroup.vue";
import Properties from "~/components/Properties.vue";
import PropertiesItem from "~/components/PropertiesItem.vue";
import NormTableOfContents from "~/components/Ris/NormTableOfContents.vue";
import RisBreadcrumb from "~/components/Ris/RisBreadcrumb.vue";
import type { BreadcrumbItem } from "~/components/Ris/RisBreadcrumb.vue";
import { useBackendURL } from "~/composables/useBackendURL";
import { useDynamicSeo } from "~/composables/useDynamicSeo";
import { useIntersectionObserver } from "~/composables/useIntersectionObserver";
import { useFetchNormContent } from "~/composables/useNormData";
import { useNormVersions } from "~/composables/useNormVersions";
import { DocumentKind, type LegislationWork } from "~/types";
import { dateFormattedDDMMYYYY } from "~/utils/dateFormatting";
import { formatDocumentKind } from "~/utils/displayValues";
import {
  getNormBreadcrumbTitle,
  getValidityStatus,
  getManifestationUrl,
  temporalCoverageToValidityInterval,
  getValidityStatusLabel,
} from "~/utils/norm";
import type { ValidityStatus } from "~/utils/norm";
import { isPrototypeProfile } from "~/utils/profile";
import { tocItemsToTreeNodes } from "~/utils/tableOfContents";
import { truncateAtWord } from "~/utils/textFormatting";
import IcBaselineSubject from "~icons/ic/baseline-subject";
import IcOutlineInfo from "~icons/ic/outline-info";
import IcOutlineRestore from "~icons/ic/outline-settings-backup-restore";
import MaterialSymbolsDownload from "~icons/material-symbols/download";

definePageMeta({
  // note: this is an expression ELI
  alias:
    "/eli/:jurisdiction/:agent/:year/:naturalIdentifier/:pointInTime/:version/:language",
  layout: "base", // use "base" layout to allow for full-width tab backgrounds
});

const route = useRoute();
const expressionEli = Object.values(route.params).join("/");

const { data, error, status } = await useFetchNormContent(expressionEli);

const metadata: Ref<LegislationWork | undefined> = computed(() => {
  return data.value?.legislationWork;
});

const html = computed(() => data.value?.html);
const htmlParts = computed(() => data.value?.htmlParts);

const backendURL = useBackendURL();

const zipUrl = computed(() =>
  getManifestationUrl(metadata.value, backendURL, "application/zip"),
);

if (error.value) {
  showError(error.value);
}

const tableOfContents: Ref<TreeNode[]> = computed(() => {
  if (!metadata.value?.workExample.tableOfContents) return [];
  const normPath = route.path;
  return tocItemsToTreeNodes(
    metadata.value.workExample.tableOfContents,
    normPath.concat("#"),
    normPath.concat("#"),
  );
});

const validityInterval = computed(() =>
  isPrototypeProfile()
    ? undefined
    : temporalCoverageToValidityInterval(
        metadata.value?.workExample.temporalCoverage,
      ),
);

const validityStatus = computed(() => {
  if (metadata.value?.workExample && validityInterval)
    return getValidityStatus(validityInterval.value);
  return undefined;
});

const { selectedEntry, vObserveElements } = useIntersectionObserver();

const normBreadcrumbTitle = computed(() =>
  metadata.value ? getNormBreadcrumbTitle(metadata.value) : "",
);

const { status: normVersionsStatus, sortedVersions: normVersions } =
  useNormVersions(metadata.value?.legislationIdentifier);

const isClient = ref(false);
onMounted(() => (isClient.value = true));

const activeSection = ref<"text" | "details" | "versions">("text");

const breadcrumbItems: ComputedRef<BreadcrumbItem[]> = computed(() => {
  const list = [
    {
      label: formatDocumentKind(DocumentKind.Norm),
      route: `/search?category=${DocumentKind.Norm}`,
    },
    {
      route: `/norms/${metadata.value?.legislationIdentifier}`,
      label: normBreadcrumbTitle.value,
    },
  ];

  const isInForce =
    metadata.value?.workExample.legislationLegalForce === "InForce";
  if (!isInForce) {
    const validityIntervalLabel = `${dateFormattedDDMMYYYY(validityInterval.value?.from) ?? ""}-${dateFormattedDDMMYYYY(validityInterval.value?.to) ?? ""}`;
    list.push({ route: route.fullPath, label: validityIntervalLabel });
  }

  return list;
});

const prototypeMode = isPrototypeProfile();
const buildOgTitle = (
  norm: LegislationWork,
  validFrom?: Dayjs,
  status?: ValidityStatus,
) => {
  const abbreviation = norm.abbreviation?.trim();
  const shortTitle = norm.alternateName?.trim();
  const baseTitle = abbreviation || shortTitle || "";

  if (!baseTitle) return undefined;

  if (prototypeMode) {
    const parts: string[] = [baseTitle];

    if (validFrom) {
      parts.push("Fassung vom [Inkrafttreten]");
    }

    if (status) {
      parts.push("[Status]");
    }
    const placeholder = parts.join(", ");
    return truncateAtWord(placeholder, 55) || undefined;
  } else {
    const parts: string[] = [baseTitle];

    const formattedValidFrom = dateFormattedDDMMYYYY(validFrom);
    if (formattedValidFrom) {
      parts.push(`Fassung vom ${formattedValidFrom}`);
    }

    const statusLabel = getValidityStatusLabel(status);
    if (statusLabel) {
      parts.push(statusLabel);
    }

    return truncateAtWord(parts.join(", "), 55) || undefined;
  }
};

const title = computed<string | undefined>(() =>
  metadata.value
    ? buildOgTitle(
        metadata.value,
        validityInterval.value?.from,
        validityStatus.value,
      )
    : undefined,
);

const description = computed<string | undefined>(() => {
  const shortTitle = metadata.value?.alternateName?.trim();
  const longTitle = metadata.value?.name?.trim();
  const chosen = shortTitle || longTitle || "";
  return chosen ? truncateAtWord(chosen, 150) : undefined;
});

useDynamicSeo({ title, description });
</script>

<template>
  <ContentWrapper border>
    <div v-if="status == 'pending'">Lade ...</div>
    <div v-if="!!metadata">
      <div class="container">
        <div class="flex items-center gap-8 print:hidden">
          <RisBreadcrumb :items="breadcrumbItems" class="grow" />
          <client-only> <NormActionsMenu :metadata="metadata" /></client-only>
        </div>
        <NormHeadingGroup :metadata="metadata" :html-parts="htmlParts" />
        <NormVersionWarning
          v-if="normVersionsStatus === 'success'"
          :versions="normVersions"
          :current-version="metadata"
        />
        <NormMetadataFields
          :abbreviation="metadata.abbreviation"
          :status="validityStatus"
          :valid-from="validityInterval?.from"
          :valid-to="validityInterval?.to"
        />
      </div>
      <nav :class="linkTabNav" aria-label="Ansichten des Gesetzes">
        <div :class="linkTabNavContainer">
          <a
            href="#text"
            :aria-current="activeSection === 'text' ? 'page' : undefined"
            aria-label="Text des Gesetzes"
            :class="[
              linkTabBase,
              activeSection === 'text' ? linkTabActive : linkTabInactive,
            ]"
            @click.prevent="activeSection = 'text'"
          >
            <IcBaselineSubject aria-hidden="true" />
            Text
          </a>

          <a
            href="#details"
            data-attr="norm-metadata-tab"
            :aria-current="activeSection === 'details' ? 'page' : undefined"
            aria-label="Details des Gesetzes"
            :class="[
              linkTabBase,
              activeSection === 'details' ? linkTabActive : linkTabInactive,
            ]"
            @click.prevent="activeSection = 'details'"
          >
            <IcOutlineInfo aria-hidden="true" />
            Details
          </a>

          <a
            href="#versions"
            data-attr="norm-versions-tab"
            :aria-current="activeSection === 'versions' ? 'page' : undefined"
            aria-label="Fassungen des Gesetzes"
            :class="[
              linkTabBase,
              activeSection === 'versions' ? linkTabActive : linkTabInactive,
            ]"
            @click.prevent="activeSection = 'versions'"
          >
            <IcOutlineRestore aria-hidden="true" />
            Fassungen
          </a>
        </div>
      </nav>

      <section
        id="text"
        :class="[
          linkTabPanel,
          isClient && activeSection !== 'text' ? 'hidden' : '',
        ]"
      >
        <TableOfContentsLayout class="container">
          <template #content>
            <h2 class="sr-only">Text</h2>
            <IncompleteDataMessage />
            <Accordion
              v-if="htmlParts.officialToc"
              header-expanded="Amtliches Inhaltsverzeichnis ausblenden"
              header-collapsed="Amtliches Inhaltsverzeichnis einblenden"
            >
              <div v-html="htmlParts.officialToc" />
            </Accordion>
            <div v-observe-elements class="norm-view" v-html="html" />
          </template>
          <template #sidebar>
            <client-only>
              <NormTableOfContents
                v-if="metadata.workExample.tableOfContents.length > 0"
                :table-of-contents="tableOfContents"
                :selected-key="selectedEntry"
              />
            </client-only>
          </template>
        </TableOfContentsLayout>
      </section>

      <section
        id="details"
        :class="[
          linkTabPanel,
          'pt-24 pb-80',
          isClient && activeSection !== 'details' ? 'hidden' : '',
        ]"
        aria-labelledby="detailsTabPanelTitle"
      >
        <div class="container">
          <h2 id="detailsTabPanelTitle" class="ris-heading3-bold my-24">
            Details
          </h2>
          <IncompleteDataMessage class="my-24" />
          <Properties>
            <PropertiesItem
              label="Ausfertigungsdatum:"
              :value="dateFormattedDDMMYYYY(metadata.legislationDate)"
            />
            <PropertiesItem label="Vollzitat:" :value="htmlParts.vollzitat" />
            <PropertiesItem
              label="Stand:"
              :value-list="htmlParts.standangaben"
            />
            <PropertiesItem
              label="Hinweis zum Stand:"
              :value-list="htmlParts.standangabenHinweis"
            />
            <PropertiesItem
              v-if="htmlParts.prefaceContainer"
              label="Besonderer Hinweis:"
            >
              <div v-html="htmlParts.prefaceContainer" />
            </PropertiesItem>
            <PropertiesItem label="Fußnoten:">
              <template v-if="htmlParts.headingNotes" #default>
                <div v-html="htmlParts.headingNotes" />
              </template>
            </PropertiesItem>
            <PropertiesItem label="Download:">
              <NuxtLink
                data-attr="xml-zip-view"
                class="ris-link1-regular"
                external
                :href="zipUrl"
              >
                <MaterialSymbolsDownload class="mr-2 inline" />
                {{ metadata.abbreviation ?? "Inhalte" }} als ZIP herunterladen
              </NuxtLink>
            </PropertiesItem>
          </Properties>
        </div>
      </section>

      <section
        id="versions"
        :class="[
          linkTabPanel,
          'pt-24 pb-80',
          isClient && activeSection !== 'versions' ? 'hidden' : '',
        ]"
      >
        <div class="container">
          <NormVersionList
            v-if="!isPrototypeProfile()"
            :status="normVersionsStatus"
            :current-legislation-identifier="
              metadata.workExample.legislationIdentifier
            "
            :versions="normVersions"
          />
          <VersionsTeaser v-else />
        </div>
      </section>
    </div>
  </ContentWrapper>
  <Toast />
</template>
