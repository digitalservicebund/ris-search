import type { VueWrapper } from "@vue/test-utils";
import { mount } from "@vue/test-utils";

import { beforeEach, describe, expect, it } from "vitest";
import { createTestingPinia } from "@pinia/testing";
import DateRangeFilter from "./DateRangeFilter.vue";
import { DateSearchMode } from "@/stores/searchParams";
import { setStoreValues } from "@/utils/testing/piniaUtils";
import PrimeVue from "primevue/config";

const testDates = {
  date: "1999-12-31",
  dateAfter: "2000-01-01",
  dateBefore: "2025-12-31",
};

describe("date/date range filter", () => {
  let wrapper: VueWrapper;

  beforeEach(() => {
    wrapper = mount(DateRangeFilter, {
      global: {
        plugins: [createTestingPinia({ stubActions: false }), PrimeVue],
      },
    });
  });

  const scenarios = [
    { mode: DateSearchMode.None, fields: [] },
    { mode: DateSearchMode.Equal, fields: ["date"] },
    { mode: DateSearchMode.After, fields: ["dateAfter"] },
    { mode: DateSearchMode.Before, fields: ["dateBefore"] },
    { mode: DateSearchMode.Range, fields: ["dateAfter", "dateBefore"] },
  ];

  for (const { fields, mode } of scenarios) {
    it(`renders ${fields.length ? fields : "nothing"} for mode "${mode}"`, async () => {
      await setStoreValues({ dateSearchMode: mode });
      expect(wrapper.findAll("input").map((e) => e.element.id)).toEqual(fields);
    });

    it(`correctly stores the input date(s) for mode "${mode}"`, async () => {
      const store = await setStoreValues({ dateSearchMode: mode });

      wrapper.findAll("input").forEach((i) => i.setValue("02.01.2000"));
      await nextTick();
      for (const field of fields) {
        expect((store as unknown as Record<string, string>)[field]).toEqual(
          "2000-01-02",
        );
      }
    });
  }

  it("renders just the dropdown if no mode is set", async () => {
    await setStoreValues({ dateSearchMode: DateSearchMode.None });
    expect(wrapper.findComponent({ name: "Select" }).vm.modelValue).toBe("");

    expect(wrapper.findAll("input")).toHaveLength(0);
  });

  it("ignores other parameters in the store", async () => {
    await setStoreValues({
      dateSearchMode: DateSearchMode.Equal,
      ...testDates,
    });

    expect(wrapper.findAll("input").map((e) => e.element.id)).toEqual(["date"]);
    expect((wrapper.find("#date").element as HTMLInputElement).value).toBe(
      "31.12.1999",
    );
  });

  it("renders a correct date range", async () => {
    await setStoreValues({
      dateSearchMode: DateSearchMode.Range,
      ...testDates,
    });

    expect(wrapper.findComponent({ name: "Select" }).vm.modelValue).toBe(
      DateSearchMode.Range,
    );

    expect(wrapper.findAll("input").map((e) => e.element.id)).toEqual([
      "dateAfter",
      "dateBefore",
    ]);
    const inputAfter = wrapper.find("#dateAfter").element as HTMLInputElement;
    expect(inputAfter.value).toBe("01.01.2000");
    expect(inputAfter?.labels?.[0]?.textContent?.trim()).toBe("Ab dem Datum");
    const inputBefore = wrapper.find("#dateBefore").element as HTMLInputElement;
    expect(inputBefore.value).toBe("31.12.2025");
    expect(inputBefore?.labels?.[0]?.textContent?.trim()).toBe("Bis zum Datum");
  });

  it("correctly switches from a single date input to range", async () => {
    const store = await setStoreValues({
      dateSearchMode: DateSearchMode.After,
      dateAfter: "1949-05-23",
    });

    await wrapper
      .findComponent({ name: "Select" })
      .setValue(DateSearchMode.Range);
    await nextTick();

    expect((wrapper.get("#dateAfter").element as HTMLInputElement).value).toBe(
      "23.05.1949",
    );
    expect((wrapper.get("#dateBefore").element as HTMLInputElement).value).toBe(
      "",
    );

    expect(store.dateSearchMode).toBe(DateSearchMode.Range);
    expect(store.dateAfter).toBe("1949-05-23");

    await wrapper.get("#dateBefore").setValue("03.10.1990");

    await nextTick();
    expect(store.dateBefore).toBe("1990-10-03");
  });
});
