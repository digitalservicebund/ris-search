import { createTestingPinia } from "@pinia/testing";
import { mount } from "@vue/test-utils";
import type { VueWrapper } from "@vue/test-utils";
import { beforeEach, describe, expect, it } from "vitest";
import YearRangeFilter from "./YearRangeFilter.vue";
import { DateSearchMode } from "~/stores/searchParams";
import { setStoreValues } from "~/tests/piniaUtils";

const testYears = {
  dateAfter: "2000-01-01",
  dateBefore: "2025-12-31",
};

describe("year/year range filter", () => {
  let wrapper: VueWrapper;

  beforeEach(() => {
    wrapper = mount(YearRangeFilter, {
      global: {
        plugins: [createTestingPinia({ stubActions: false })],
      },
    });
  });

  const scenarios = [
    { mode: DateSearchMode.None, fields: [] },
    { mode: DateSearchMode.Equal, fields: ["yearEqual"] },
    { mode: DateSearchMode.After, fields: ["yearAfter"] },
    { mode: DateSearchMode.Before, fields: ["yearBefore"] },
    { mode: DateSearchMode.Range, fields: ["yearAfter", "yearBefore"] },
  ];

  for (const { fields, mode } of scenarios) {
    it(`renders ${fields.length ? fields : "nothing"} for mode "${mode}"`, async () => {
      await setStoreValues({ dateSearchMode: mode });
      expect(wrapper.findAll("input").map((e) => e.element.id)).toEqual(fields);
    });

    it(`correctly stores the input year(s) for mode "${mode}"`, async () => {
      const store = await setStoreValues({ dateSearchMode: mode });

      for (const input of wrapper.findAll("input")) {
        input.setValue("2000");
      }

      await nextTick();

      for (const field of fields) {
        if (field === "yearBefore") {
          expect(store.dateBefore).toBe("2000-12-31");
        } else {
          expect(store.dateAfter).toBe("2000-01-01");
        }
      }
    });
  }

  it("renders just the dropdown if no mode is set", async () => {
    await setStoreValues({ dateSearchMode: DateSearchMode.None });
    expect(wrapper.findComponent({ name: "Select" }).vm.modelValue).toBe("");

    expect(wrapper.findAll("input")).toHaveLength(0);
  });

  it.skip("ignores other parameters in the store", async () => {
    await setStoreValues({
      dateSearchMode: DateSearchMode.Equal,
      ...testYears,
    });

    expect(wrapper.findAll("input").map((e) => e.element.id)).toEqual([
      "yearEqual",
    ]);
    expect((wrapper.find("#yearEqual").element as HTMLInputElement).value).toBe(
      "2000",
    );
  });

  it.skip("renders a correct year range", async () => {
    await setStoreValues({
      dateSearchMode: DateSearchMode.Range,
      ...testYears,
    });

    expect(wrapper.findComponent({ name: "Select" }).vm.modelValue).toBe(
      DateSearchMode.Range,
    );

    expect(wrapper.findAll("input").map((e) => e.element.id)).toEqual([
      "yearAfter",
      "yearBefore",
    ]);
    const inputAfter = wrapper.find("#yearAfter").element as HTMLInputElement;
    expect(inputAfter.value).toBe("2000");
    expect(inputAfter?.labels?.[0]?.textContent?.trim()).toBe("Ab dem Jahr");

    const inputBefore = wrapper.find("#yearBefore").element as HTMLInputElement;
    expect(inputBefore.value).toBe("2025");
    expect(inputBefore?.labels?.[0]?.textContent?.trim()).toBe("Bis zum Jahr");
  });

  it("correctly switches from a single year input to range", async () => {
    await setStoreValues({
      dateSearchMode: DateSearchMode.After,
      dateAfter: "1949-01-01",
    });

    expect((wrapper.get("#yearAfter").element as HTMLInputElement).value).toBe(
      "",
    );
  });
});
