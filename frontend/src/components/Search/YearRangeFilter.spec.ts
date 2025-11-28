import { renderSuspended } from "@nuxt/test-utils/runtime";
import userEvent from "@testing-library/user-event";
import { screen } from "@testing-library/vue";
import { InputText } from "primevue";
import { describe, expect, it } from "vitest";
import YearRangeFilter from "./YearRangeFilter.vue";
import { DateSearchMode } from "~/stores/searchParams";

describe("YearRangeFilter", () => {
  const setup: [
    mode: DateSearchMode,
    fields: string[],
    expectations: Record<string, string>,
  ][] = [
    [DateSearchMode.None, [], {}],
    [
      DateSearchMode.Equal,
      ["yearEqual"],
      { dateAfter: "2000-01-01", dateBefore: "2000-12-31" },
    ],
    [DateSearchMode.After, ["yearAfter"], { dateAfter: "2000-01-01" }],
    [DateSearchMode.Before, ["yearBefore"], { dateBefore: "2000-12-31" }],
    [
      DateSearchMode.Range,
      ["yearAfter", "yearBefore"],
      { dateAfter: "2000-01-01", dateBefore: "2000-12-31" },
    ],
  ];

  test.each(setup)(
    'mode "%s" renders "%s" and emits correct values',
    async (mode, fields, expectations) => {
      const user = userEvent.setup();

      const { emitted } = await renderSuspended(YearRangeFilter, {
        props: {
          dateSearchMode: mode,
          dateAfter: undefined,
          dateBefore: undefined,
        },
        global: { stubs: { InputMask: InputText } },
      });

      // check rendered inputs match expected count
      const inputs = screen.queryAllByRole("textbox");
      expect(inputs).toHaveLength(fields.length);

      // set values if any
      for (const input of inputs) {
        await user.clear(input);
        await user.type(input, "2000");
      }

      // verify emitted values
      if ("dateAfter" in expectations) {
        expect(emitted("update:dateAfter")).toContainEqual([
          expectations.dateAfter,
        ]);
      }

      if ("dateBefore" in expectations) {
        expect(emitted("update:dateBefore")).toContainEqual([
          expectations.dateBefore,
        ]);
      }
    },
  );

  it("renders no input fields if no date search mode selected", async () => {
    await renderSuspended(YearRangeFilter, {
      props: {
        dateSearchMode: DateSearchMode.None,
        dateAfter: undefined,
        dateBefore: undefined,
      },
    });

    expect(screen.queryByRole("textbox")).toBeNull();
  });

  it("emits values when entering years in Range mode", async () => {
    const user = userEvent.setup();

    const { emitted } = await renderSuspended(YearRangeFilter, {
      props: {
        dateSearchMode: DateSearchMode.Range,
        dateAfter: undefined,
        dateBefore: undefined,
      },
      global: { stubs: { InputMask: InputText } },
    });

    const inputs = screen.getAllByRole("textbox");
    expect(inputs).toHaveLength(2);

    await user.type(inputs[0]!, "2000");
    await user.type(inputs[1]!, "2020");

    expect(emitted("update:dateAfter")).toContainEqual(["2000-01-01"]);
    expect(emitted("update:dateBefore")).toContainEqual(["2020-12-31"]);
  });

  it('keeps "Equal" mode start and end synchronized', async () => {
    const user = userEvent.setup();

    const { emitted } = await renderSuspended(YearRangeFilter, {
      props: {
        dateSearchMode: DateSearchMode.Equal,
        dateAfter: undefined,
        dateBefore: undefined,
      },
      global: { stubs: { InputMask: InputText } },
    });

    const input = screen.getByRole("textbox");
    await user.clear(input);
    await user.type(input, "2022");

    expect(emitted("update:dateAfter")).toContainEqual(["2022-01-01"]);
    expect(emitted("update:dateBefore")).toContainEqual(["2022-12-31"]);
  });

  it("emits undefined for invalid year input", async () => {
    const user = userEvent.setup();

    const { emitted } = await renderSuspended(YearRangeFilter, {
      props: {
        dateSearchMode: DateSearchMode.After,
        dateAfter: "2000-01-01",
        dateBefore: undefined,
      },
      global: { stubs: { InputMask: InputText } },
    });

    const input = screen.getByRole("textbox");
    await user.clear(input);
    await user.type(input, "abcd");

    expect(emitted("update:dateAfter")).toContainEqual([undefined]);
  });
});
