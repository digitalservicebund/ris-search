import { createTestingPinia } from "@pinia/testing";
import { mount } from "@vue/test-utils";
import type { VueWrapper } from "@vue/test-utils";
import { beforeEach, describe, expect, it } from "vitest";
import YearRangeFilter from "./YearRangeFilter.vue";
import { DateSearchMode } from "~/stores/searchParams";
import { setStoreValues } from "~/tests/piniaUtils";

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

  for (const { mode, fields, expectations } of scenarios) {
    it(`renders inputs ${fields.length ? fields.join(", ") : "none"} and stores correct values for mode "${mode}"`, async () => {
      const store = await setStoreValues({ dateSearchMode: mode });

      // check rendered inputs
      const inputIds = wrapper.findAll("input").map((e) => e.element.id);
      expect(inputIds).toEqual(fields);

      // set values if any
      for (const id of fields) {
        wrapper.find(`input[id="${id}"]`).setValue("2000");
      }

      await nextTick();

      // verify store state
      if ("dateAfter" in expectations) {
        expect(store.dateAfter).toBe(expectations.dateAfter);
      } else {
        expect(store.dateAfter).toBeUndefined();
      }

      if ("dateBefore" in expectations) {
        expect(store.dateBefore).toBe(expectations.dateBefore);
      } else {
        expect(store.dateBefore).toBeUndefined();
      }
    });
  }

  it("it renders no input fields if non data search mode selected", async () => {
    await setStoreValues({ dateSearchMode: DateSearchMode.None });
    expect(wrapper.findComponent({ name: "Select" }).vm.modelValue).toBe("");

    expect(wrapper.findAll("input")).toHaveLength(0);
  });

  it("resets date values when mode changes", async () => {
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
    const store = await setStoreValues({
      dateSearchMode: DateSearchMode.After,
    });
    await nextTick();

    const input = wrapper.get("#yearAfter");
    await input.setValue("abcd");
    await nextTick();

    expect(store.dateAfter).toBeUndefined();
  });

  it("updates inputs when store values change", async () => {
    await setStoreValues({
      dateSearchMode: DateSearchMode.Range,
      dateAfter: "2010-01-01",
      dateBefore: "2020-12-31",
    });

    // expect(store.dateAfter).toBe("2010-01-01");
    // expect(store.dateBefore).toBe("2020-12-31");

    const inputs = wrapper.findAll("input");
    const values = inputs.map((el) => (el.element as HTMLInputElement).value);
    expect(values).toEqual(["2010", "2020"]);
  });
});
