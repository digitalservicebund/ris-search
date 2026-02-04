import { renderSuspended } from "@nuxt/test-utils/runtime";
import { userEvent } from "@testing-library/user-event";
import { screen } from "@testing-library/vue";
import { InputText } from "primevue";
import { describe, expect, it } from "vitest";
import YearRangeFilter from "./YearRangeFilter.vue";
import type { DateFilterValue } from "~/utils/search/dateFilterType";

describe("YearRangeFilter", () => {
  it.each<[string, DateFilterValue, number]>([
    ["allTime", { type: "allTime" }, 0],
    ["equal", { type: "period", from: "2000-01-01", to: "2000-12-31" }, 1],
    ["after", { type: "after" }, 1],
    ["before", { type: "before" }, 1],
    ["range", { type: "period" }, 2],
  ])(
    'mode "%s" renders $2 input fields',
    async (_mode, modelValue, fieldCount) => {
      await renderSuspended(YearRangeFilter, {
        props: { modelValue },
        global: { stubs: { InputMask: InputText } },
      });

      expect(screen.queryAllByRole("textbox")).toHaveLength(fieldCount);
    },
  );

  it("renders no input fields for allTime filter", async () => {
    await renderSuspended(YearRangeFilter, {
      props: { modelValue: { type: "allTime" } },
    });

    expect(screen.queryByRole("textbox")).toBeNull();
  });

  it("emits correct values when entering years in range mode", async () => {
    const user = userEvent.setup();

    const { emitted } = await renderSuspended(YearRangeFilter, {
      props: { modelValue: { type: "period" } },
      global: { stubs: { InputMask: InputText } },
    });

    const inputs = screen.getAllByRole("textbox");
    expect(inputs).toHaveLength(2);

    await user.type(inputs[0]!, "2000");
    await user.type(inputs[1]!, "2020");

    const updates = emitted("update:modelValue");
    expect(updates).toContainEqual([
      { from: "2000-01-01", to: "2020-12-31", type: "period" },
    ]);
  });

  it("synchronizes start and end in equal year mode", async () => {
    const user = userEvent.setup();

    const { emitted } = await renderSuspended(YearRangeFilter, {
      props: {
        modelValue: { type: "period", from: "2000-01-01", to: "2000-12-31" },
      },
      global: { stubs: { InputMask: InputText } },
    });

    const input = screen.getByRole("textbox");
    await user.clear(input);
    await user.type(input, "2022");

    const updates = emitted("update:modelValue");
    expect(updates).toContainEqual([
      { from: "2022-01-01", to: "2022-12-31", type: "period" },
    ]);
  });

  it("emits filter with undefined date for invalid year input", async () => {
    const user = userEvent.setup();

    const { emitted } = await renderSuspended(YearRangeFilter, {
      props: {
        modelValue: { type: "after", from: "2000-01-01" },
      },
      global: { stubs: { InputMask: InputText } },
    });

    const input = screen.getByRole("textbox");
    await user.clear(input);
    await user.type(input, "abcd");

    const updates = emitted("update:modelValue");
    expect(updates).toContainEqual([
      { from: undefined, to: undefined, type: "after" },
    ]);
  });
});
