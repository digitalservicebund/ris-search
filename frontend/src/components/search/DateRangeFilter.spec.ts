import { renderSuspended } from "@nuxt/test-utils/runtime";
import { userEvent } from "@testing-library/user-event";
import { screen, waitFor } from "@testing-library/vue";
import { InputText } from "primevue";
import { describe, expect, it } from "vitest";
import DateRangeFilter from "./DateRangeFilter.vue";
import type {
  DateFilterValue,
  FilterType,
} from "~/utils/search/dateFilterType";

describe("DateRangeFilter", () => {
  const renderModes: [FilterType, number][] = [
    ["allTime", 0],
    ["specificDate", 1],
    ["after", 1],
    ["before", 1],
    ["period", 2],
  ];

  it.each(renderModes)(
    'type "%s" renders %d input fields',
    async (type, fieldCount) => {
      await renderSuspended(DateRangeFilter, {
        props: { modelValue: { type } },
        global: { stubs: { InputMask: InputText } },
      });

      expect(screen.queryAllByRole("textbox")).toHaveLength(fieldCount);
    },
  );

  it.each<[FilterType, Partial<DateFilterValue>]>([
    ["specificDate", { from: "2000-01-02" }],
    ["after", { from: "2000-01-02" }],
    ["before", { to: "2000-01-02" }],
  ])(
    'correctly emits date filter value for type "%s"',
    async (type, expected) => {
      const user = userEvent.setup();

      const { emitted } = await renderSuspended(DateRangeFilter, {
        props: { modelValue: { type } },
        global: { stubs: { InputMask: InputText } },
      });

      const inputs = screen.queryAllByRole("textbox");
      for (const input of inputs) {
        await user.clear(input);
        await user.type(input, "02.01.2000");
      }

      const updates = emitted("update:modelValue") as DateFilterValue[][];
      for (const [key, value] of Object.entries(expected)) {
        expect(
          updates.some(([f]) => f?.[key as keyof DateFilterValue] === value),
        ).toBe(true);
      }
    },
  );

  it("correctly emits both dates in period mode", async () => {
    const user = userEvent.setup();

    const { emitted } = await renderSuspended(DateRangeFilter, {
      props: { modelValue: { type: "period" } },
      global: { stubs: { InputMask: InputText } },
    });

    const inputs = screen.queryAllByRole("textbox");
    expect(inputs).toHaveLength(2);

    await user.clear(inputs[0]!);
    await user.type(inputs[0]!, "02.01.2000");
    await user.clear(inputs[1]!);
    await user.type(inputs[1]!, "02.01.2001");

    const updates = emitted("update:modelValue");
    expect(updates).toContainEqual([
      { from: "2000-01-02", to: undefined, type: "period" },
    ]);
    expect(updates).toContainEqual([
      { from: "2000-01-02", to: "2001-01-02", type: "period" },
    ]);
  });

  it("renders just the dropdown if type is allTime", async () => {
    await renderSuspended(DateRangeFilter, {
      props: { modelValue: { type: "allTime" } },
      global: { stubs: { InputMask: InputText } },
    });

    expect(screen.queryAllByRole("textbox")).toHaveLength(0);
  });

  it("shows existing date for specificDate type", async () => {
    await renderSuspended(DateRangeFilter, {
      props: {
        modelValue: { type: "specificDate", from: "1999-12-31" },
      },
      global: { stubs: { InputMask: InputText } },
    });

    const inputs = screen.getAllByRole("textbox");
    expect(inputs).toHaveLength(1);
    expect((inputs[0] as HTMLInputElement).value).toBe("31.12.1999");
  });

  it("renders a correct date range", async () => {
    await renderSuspended(DateRangeFilter, {
      props: {
        modelValue: {
          type: "period",
          from: "2000-01-01",
          to: "2025-12-31",
        },
      },
      global: { stubs: { InputMask: InputText } },
    });

    const inputs = screen.getAllByRole("textbox");
    expect(inputs).toHaveLength(2);
    expect(inputs[0]).toHaveValue("01.01.2000");
    expect(inputs[1]).toHaveValue("31.12.2025");
  });

  it("emits updated filter when entering 'to' date in period mode", async () => {
    const user = userEvent.setup();

    const { emitted } = await renderSuspended(DateRangeFilter, {
      props: {
        modelValue: { type: "period", from: "1949-05-23" },
      },
      global: { stubs: { InputMask: InputText } },
    });

    const inputs = screen.getAllByRole("textbox");
    expect(inputs).toHaveLength(2);
    expect(inputs[0]).toHaveValue("23.05.1949");
    expect(inputs[1]).toHaveValue("");

    await user.type(inputs[1]!, "03.10.1990");

    const updates = emitted("update:modelValue");
    expect(updates).toContainEqual([
      { to: "1990-10-03", from: "1949-05-23", type: "period" },
    ]);
  });

  it("shows validation error for invalid date in specificDate mode", async () => {
    const user = userEvent.setup();

    await renderSuspended(DateRangeFilter, {
      props: { modelValue: { type: "specificDate" } },
      global: { stubs: { InputMask: InputText } },
    });

    const input = screen.getByRole("textbox");
    await user.type(input, "29.02.2001");
    await user.tab();

    await waitFor(() => {
      expect(screen.getByText("Kein valides Datum")).toBeInTheDocument();
    });
  });

  it("shows validation error for invalid date in after mode", async () => {
    const user = userEvent.setup();

    await renderSuspended(DateRangeFilter, {
      props: { modelValue: { type: "after" } },
      global: { stubs: { InputMask: InputText } },
    });

    const input = screen.getByRole("textbox");
    await user.type(input, "32.12.2000");
    await user.tab();

    await waitFor(() => {
      expect(screen.getByText("Kein valides Datum")).toBeInTheDocument();
    });
  });

  it("shows validation error for invalid date in before mode", async () => {
    const user = userEvent.setup();

    await renderSuspended(DateRangeFilter, {
      props: { modelValue: { type: "before" } },
      global: { stubs: { InputMask: InputText } },
    });

    const input = screen.getByRole("textbox");
    await user.type(input, "00.01.2000");
    await user.tab();

    await waitFor(() => {
      expect(screen.getByText("Kein valides Datum")).toBeInTheDocument();
    });
  });

  it("shows validation errors for both dates in period mode", async () => {
    const user = userEvent.setup();

    await renderSuspended(DateRangeFilter, {
      props: { modelValue: { type: "period" } },
      global: { stubs: { InputMask: InputText } },
    });

    const inputs = screen.getAllByRole("textbox");
    expect(inputs).toHaveLength(2);

    await user.type(inputs[0]!, "31.02.2000");
    await user.tab();

    await waitFor(() => {
      expect(screen.getByText("Kein valides Datum")).toBeInTheDocument();
    });

    await user.type(inputs[1]!, "29.02.2001");
    await user.tab();

    await waitFor(() => {
      expect(screen.getAllByText("Kein valides Datum")).toHaveLength(2);
    });
  });

  it("clears validation error when valid date is entered", async () => {
    const user = userEvent.setup();

    await renderSuspended(DateRangeFilter, {
      props: { modelValue: { type: "specificDate" } },
      global: { stubs: { InputMask: InputText } },
    });

    const input = screen.getByRole("textbox");

    await user.type(input, "29.02.2001");
    await user.tab();

    await waitFor(() => {
      expect(screen.getByText("Kein valides Datum")).toBeInTheDocument();
    });

    await user.clear(input);
    await user.type(input, "28.02.2001");

    await waitFor(() => {
      expect(screen.queryByText("Kein valides Datum")).not.toBeInTheDocument();
    });
  });

  it("shows validation error for incomplete date", async () => {
    const user = userEvent.setup();

    await renderSuspended(DateRangeFilter, {
      props: { modelValue: { type: "specificDate" } },
      global: { stubs: { InputMask: InputText } },
    });

    const input = screen.getByRole("textbox");
    await user.type(input, "01.01");
    await user.tab();

    await waitFor(() => {
      expect(screen.getByText("Unvollständiges Datum")).toBeInTheDocument();
    });
  });
});
