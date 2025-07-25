<script lang="ts" setup>
import Select from "primevue/select";
import {
  type FieldType,
  type Field,
  availablePublicFields,
  availableInternalFields,
  availablePublicFeatures,
  availableInternalFeatures,
  type Feature,
} from "./data";
import type { DropdownItem } from "~/components/types";
import { isInternalProfile, isPublicProfile } from "~/utils/config";

definePageMeta({ alias: "/erweiterte-suche/hilfe" });

const filter = ref<FieldType>("all");
const filters: DropdownItem[] = [
  { label: "Alle", value: "all" },
  { label: "Rechtsprechung", value: "case_law" },
  { label: "Normen", value: "norms" },
  { label: "Verwaltungsvorschriften", value: "administrative_regulations" },
  { label: "Literaturnachweise", value: "literature" },
];

const fields = computed(() => {
  return filter.value === "all"
    ? availableFields
    : availableFields.filter((f) => f.types.includes(filter.value));
});
const isInternal = isInternalProfile();
const isPublic = isPublicProfile();

const availableFields: Field[] = isPublic
  ? availablePublicFields
  : availableInternalFields;
const availableFeatures: Feature[] = isPublic
  ? availablePublicFeatures
  : availableInternalFeatures;
</script>

<template>
  <div v-if="isInternal" class="container mx-auto py-16">
    <div class="flex flex-col gap-48">
      <div>
        <h1 class="ris-heading2-regular mt-24 mb-8">Hilfe zur Suche</h1>
        <p>
          Hier finden Sie eine Übersicht über die aktuell verfügbaren Funktionen
          und Rubriken der Suche. Wir arbeiten daran weitere Daten hinzuzufügen
          und die Funktionalität nutzerfreundlicher zu machen.
        </p>
      </div>

      <div>
        <h2 class="ris-heading3-regular mb-20">Verfügbare Funktionen</h2>
        <dl class="flex flex-col gap-16 divide-y divide-gray-200">
          <div
            v-for="feature in availableFeatures"
            :id="feature.id"
            :key="feature.label"
            class="gap-8 md:flex md:flex-row"
          >
            <dt class="w-2/6">
              <h3 class="ds-subhead">
                {{ feature.label }}
              </h3>
            </dt>
            <dd class="w-4/6">
              <p class="whitespace-pre-wrap">{{ feature.description }}</p>
              <div
                v-if="feature.examples.length > 0"
                class="ris-body2-regular mt-4"
              >
                <template v-for="example in feature.examples" :key="example">
                  <pre
                    class="inline-block w-auto border border-gray-200 px-4"
                    >{{ example }}</pre
                  >
                </template>
              </div>
            </dd>
          </div>
        </dl>
      </div>

      <div>
        <div class="flex flex-row justify-between">
          <h2 class="ris-heading3-regular mb-20">Verfügbare Rubriken</h2>
          <Select
            v-model="filter"
            class="ds-select-small w-auto"
            :options="filters"
            option-label="label"
            option-value="value"
            placeholder="Bitte auswählen"
          />
        </div>
        <table class="w-full">
          <caption class="sr-only">
            Auflistung der verfügbaren Rubriken mit Kennung und Beispielen für
            die Suche.
          </caption>
          <thead class="sticky top-0">
            <tr>
              <th
                class="border-b-2 border-blue-300 bg-white px-16 py-12 text-left"
              >
                Rubrik
              </th>
              <th
                class="border-b-2 border-blue-300 bg-white px-16 py-12 text-left"
              >
                Kennung
              </th>
              <th
                class="border-b-2 border-blue-300 bg-white px-16 py-12 text-left"
              >
                Beispiele
              </th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="field in fields"
              :id="field.label.toLocaleLowerCase()"
              :key="field.label"
              class="hover:bg-gray-100"
            >
              <td class="border-b-1 border-blue-300 px-16 py-12 align-middle">
                {{ field.label }}
              </td>
              <td
                class="ris-body2-regular border-b-1 border-blue-300 px-16 py-12 align-middle"
              >
                <template v-for="alias in field.aliases" :key="alias">
                  <code>{{ alias }}</code>
                  <br />
                </template>
              </td>
              <td
                class="ris-body2-regular border-b-1 border-blue-300 px-16 py-12 align-middle"
              >
                <template v-for="example in field.examples" :key="example">
                  <pre
                    class="inline-block w-auto border border-gray-200 px-4"
                    >{{ example }}</pre
                  >
                  <br />
                </template>
              </td>
            </tr>
            <tr v-if="fields.length === 0">
              <td class="p-12" colspan="3">
                Für diese Dokumentart sind aktuell keine Rubriken verfügbar.
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
  <div v-if="isPublic">
    <PageHeader title="Hilfe zur Suche">
      <p>
        Hier finden Sie eine Übersicht über die aktuell verfügbaren Funktionen
        und Rubriken der Suche. Wir arbeiten daran weitere Daten hinzuzufügen
        und die Funktionalität nutzerfreundlicher zu machen.
      </p>
    </PageHeader>
    <div class="flex flex-col gap-48">
      <div>
        <h2 class="ris-heading3-regular mb-20">Verfügbare Funktionen</h2>
        <dl class="flex flex-col gap-16 divide-y divide-gray-200">
          <div
            v-for="feature in availableFeatures"
            :id="feature.id"
            :key="feature.label"
            class="gap-8 md:flex md:flex-row"
          >
            <dt class="w-2/6">
              <h3 class="ds-subhead">
                {{ feature.label }}
              </h3>
            </dt>
            <dd class="w-4/6">
              <p class="whitespace-pre-wrap">{{ feature.description }}</p>
              <div
                v-if="feature.examples.length > 0"
                class="ris-body2-regular mt-4"
              >
                <template v-for="example in feature.examples" :key="example">
                  <pre
                    class="inline-block w-auto border border-gray-200 px-4"
                    >{{ example }}</pre
                  >
                </template>
              </div>
            </dd>
          </div>
        </dl>
      </div>

      <div>
        <div class="flex flex-row justify-between">
          <h2 class="ris-heading3-regular mb-20">Verfügbare Rubriken</h2>
          <Select
            v-model="filter"
            class="ds-select-small w-auto"
            :options="filters"
            option-label="label"
            option-value="value"
            placeholder="Bitte auswählen"
          />
        </div>
        <table class="w-full">
          <caption class="sr-only">
            Auflistung der verfügbaren Rubriken mit Kennung und Beispielen für
            die Suche.
          </caption>
          <thead class="sticky top-0">
            <tr>
              <th
                class="border-b-2 border-blue-300 bg-white px-16 py-12 text-left"
              >
                Rubrik
              </th>
              <th
                class="border-b-2 border-blue-300 bg-white px-16 py-12 text-left"
              >
                Kennung
              </th>
              <th
                class="border-b-2 border-blue-300 bg-white px-16 py-12 text-left"
              >
                Beispiele
              </th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="field in fields"
              :id="field.label.toLocaleLowerCase()"
              :key="field.label"
              class="hover:bg-gray-100"
            >
              <td class="border-b-1 border-blue-300 px-16 py-12 align-middle">
                {{ field.label }}
              </td>
              <td
                class="ris-body2-regular border-b-1 border-blue-300 px-16 py-12 align-middle"
              >
                <template v-for="alias in field.aliases" :key="alias">
                  <code>{{ alias }}</code>
                  <br />
                </template>
              </td>
              <td
                class="ris-body2-regular border-b-1 border-blue-300 px-16 py-12 align-middle"
              >
                <template v-for="example in field.examples" :key="example">
                  <pre
                    class="inline-block w-auto border border-gray-200 px-4"
                    >{{ example }}</pre
                  >
                  <br />
                </template>
              </td>
            </tr>
            <tr v-if="fields.length === 0">
              <td class="p-12" colspan="3">
                Für diese Dokumentart sind aktuell keine Rubriken verfügbar.
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>
