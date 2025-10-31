import { mountSuspended } from "@nuxt/test-utils/runtime";
import { createTestingPinia } from "@pinia/testing";
import { describe, expect, it } from "vitest";
import { nextTick } from "vue";
import YearRangeFilter from "./YearRangeFilter.vue";
import { DateSearchMode } from "~/stores/searchParams";
import { setStoreValues } from "~/tests/piniaUtils";

describe("year/year range filter", () => {
  const scenarios = [
    {
      mode: DateSearchMode.None,
      fields: [],
      expectations: {},
    },
    {
      mode: DateSearchMode.Equal,
      fields: ["yearEqual"],
      expectations: { dateAfter: "2000-01-01", dateBefore: "2000-12-31" },
    },
    {
      mode: DateSearchMode.After,
      fields: ["yearAfter"],
      expectations: { dateAfter: "2000-01-01" },
    },
    {
      mode: DateSearchMode.Before,
      fields: ["yearBefore"],
      expectations: { dateBefore: "2000-12-31" },
    },
    {
      mode: DateSearchMode.Range,
      fields: ["yearAfter", "yearBefore"],
      expectations: { dateAfter: "2000-01-01", dateBefore: "2000-12-31" },
    },
  ];

  scenarios.forEach(({ mode, fields, expectations }) => {
    it(`renders inputs ${fields.length ? fields.join(", ") : "none"} and stores correct values for mode "${mode}"`, async () => {
      const wrapper = await mountSuspended(YearRangeFilter, {
        global: { plugins: [createTestingPinia({ stubActions: false })] },
      });

      const store = await setStoreValues({ dateSearchMode: mode });

      // check rendered inputs
      const inputIds = wrapper.findAll("input").map((e) => e.element.id);
      expect(inputIds).toEqual(fields);

      // set values if any
      for (const id of fields) {
        await wrapper.find(`input[id="${id}"]`).setValue("2000");
      }

      await nextTick();

      // verify store state
      expect(store.dateAfter).toBe(
        "dateAfter" in expectations ? expectations.dateAfter : undefined,
      );
      expect(store.dateBefore).toBe(
        "dateBefore" in expectations ? expectations.dateBefore : undefined,
      );
    });
  });

  it("renders no input fields if non data search mode selected", async () => {
    const wrapper = await mountSuspended(YearRangeFilter, {
      global: { plugins: [createTestingPinia({ stubActions: false })] },
    });

    await setStoreValues({ dateSearchMode: DateSearchMode.None });

    expect(wrapper.findComponent({ name: "Select" }).vm.modelValue).toBe("");
    expect(wrapper.findAll("input")).toHaveLength(0);
  });

  it("resets date values when mode changes", async () => {
    const wrapper = await mountSuspended(YearRangeFilter, {
      global: { plugins: [createTestingPinia({ stubActions: false })] },
    });

    const store = await setStoreValues({
      dateSearchMode: DateSearchMode.Range,
    });
    await nextTick();

    expect((wrapper.get("#yearBefore").element as HTMLInputElement).value).toBe(
      "",
    );
    expect((wrapper.get("#yearAfter").element as HTMLInputElement).value).toBe(
      "",
    );

    expect(store.dateAfter).toBeUndefined();
    expect(store.dateBefore).toBeUndefined();
  });

  it('keeps "Equal" mode start and end synchronized', async () => {
    const wrapper = await mountSuspended(YearRangeFilter, {
      global: { plugins: [createTestingPinia({ stubActions: false })] },
    });

    const store = await setStoreValues({
      dateSearchMode: DateSearchMode.Equal,
    });

    const input = wrapper.get("#yearEqual");
    await input.setValue("2022");
    await nextTick();

    expect(store.dateAfter).toBe("2022-01-01");
    expect(store.dateBefore).toBe("2022-12-31");
  });

  it("clears store values for invalid year input", async () => {
    const wrapper = await mountSuspended(YearRangeFilter, {
      global: { plugins: [createTestingPinia({ stubActions: false })] },
    });

    const store = await setStoreValues({
      dateSearchMode: DateSearchMode.After,
    });
    await nextTick();

    const input = wrapper.get("#yearAfter");
    await input.setValue("abcd");
    await nextTick();

    expect(store.dateAfter).toBeUndefined();
  });
});
