import { renderSuspended } from "@nuxt/test-utils/runtime";
import { userEvent } from "@testing-library/user-event";
import { screen, waitFor } from "@testing-library/vue";
import { InputText } from "primevue";
import { describe, expect, it } from "vitest";
import DateRangeFilter from "./DateRangeFilter.vue";
import { DateSearchMode } from "~/composables/useSimpleSearchParams/useSimpleSearchParams";

describe("DateRangeFilter", () => {
  const testDates = {
    date: "1999-12-31",
    dateAfter: "2000-01-01",
    dateBefore: "2025-12-31",
  };

  const setup: [DateSearchMode, string[]][] = [
    [DateSearchMode.None, []],
    [DateSearchMode.Equal, ["date"]],
    [DateSearchMode.After, ["dateAfter"]],
    [DateSearchMode.Before, ["dateBefore"]],
    [DateSearchMode.Range, ["dateAfter", "dateBefore"]],
  ];

  test.each(setup)('mode "%s" renders "%s" fields', async (mode, fields) => {
    await renderSuspended(DateRangeFilter, {
      props: {
        dateSearchMode: mode,
        date: undefined,
        dateAfter: undefined,
        dateBefore: undefined,
      },
      global: { stubs: { InputMask: InputText } },
    });

    expect(screen.queryAllByRole("textbox")).toHaveLength(fields.length);
  });

  test.each(setup)(
    'correctly updates the input date(s) for mode "%s"',
    async (mode, fields) => {
      const user = userEvent.setup();

      const { emitted } = await renderSuspended(DateRangeFilter, {
        props: {
          dateSearchMode: mode,
          date: undefined,
          dateAfter: undefined,
          dateBefore: undefined,
        },
        global: { stubs: { InputMask: InputText } },
      });

      const inputs = screen.queryAllByRole("textbox");

      for (const input of inputs) {
        await user.clear(input);
        await user.type(input, "02.01.2000");
      }

      for (const field of fields) {
        expect(emitted(`update:${field}`)).toContainEqual(["2000-01-02"]);
      }
    },
  );

  it("renders just the dropdown if no mode is set", async () => {
    await renderSuspended(DateRangeFilter, {
      props: {
        dateSearchMode: DateSearchMode.None,
        date: undefined,
        dateAfter: undefined,
        dateBefore: undefined,
      },
      global: { stubs: { InputMask: InputText } },
    });

    expect(screen.queryAllByRole("textbox")).toHaveLength(0);
  });

  it("ignores other parameters when showing single date input", async () => {
    await renderSuspended(DateRangeFilter, {
      props: { dateSearchMode: DateSearchMode.Equal, ...testDates },
      global: { stubs: { InputMask: InputText } },
    });

    const inputs = screen.getAllByRole("textbox");
    expect(inputs).toHaveLength(1);
    expect((inputs[0] as HTMLInputElement).value).toBe("31.12.1999");
  });

  it("renders a correct date range", async () => {
    await renderSuspended(DateRangeFilter, {
      props: {
        dateSearchMode: DateSearchMode.Range,
        date: undefined,
        dateAfter: testDates.dateAfter,
        dateBefore: testDates.dateBefore,
      },
      global: { stubs: { InputMask: InputText } },
    });

    const inputs = screen.getAllByRole("textbox");
    expect(inputs).toHaveLength(2);
    expect(inputs[0]).toHaveValue("01.01.2000");
    expect(inputs[1]).toHaveValue("31.12.2025");
  });

  it("emits dateBefore when entering value in range mode", async () => {
    const user = userEvent.setup();

    const { emitted } = await renderSuspended(DateRangeFilter, {
      props: {
        dateSearchMode: DateSearchMode.Range,
        date: undefined,
        dateAfter: "1949-05-23",
        dateBefore: undefined,
      },
      global: { stubs: { InputMask: InputText } },
    });

    const inputs = screen.getAllByRole("textbox");
    expect(inputs).toHaveLength(2);
    expect(inputs[0]).toHaveValue("23.05.1949");
    expect(inputs[1]).toHaveValue("");

    await user.type(inputs[1]!, "03.10.1990");

    expect(emitted("update:dateBefore")).toContainEqual(["1990-10-03"]);
  });

  it("shows validation error for invalid date in equal mode", async () => {
    const user = userEvent.setup();

    await renderSuspended(DateRangeFilter, {
      props: {
        dateSearchMode: DateSearchMode.Equal,
        date: undefined,
        dateAfter: undefined,
        dateBefore: undefined,
      },
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
      props: {
        dateSearchMode: DateSearchMode.After,
        date: undefined,
        dateAfter: undefined,
        dateBefore: undefined,
      },
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
      props: {
        dateSearchMode: DateSearchMode.Before,
        date: undefined,
        dateAfter: undefined,
        dateBefore: undefined,
      },
      global: { stubs: { InputMask: InputText } },
    });

    const input = screen.getByRole("textbox");
    await user.type(input, "00.01.2000");
    await user.tab();

    await waitFor(() => {
      expect(screen.getByText("Kein valides Datum")).toBeInTheDocument();
    });
  });

  it("shows validation errors for both dates in range mode", async () => {
    const user = userEvent.setup();

    await renderSuspended(DateRangeFilter, {
      props: {
        dateSearchMode: DateSearchMode.Range,
        date: undefined,
        dateAfter: undefined,
        dateBefore: undefined,
      },
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
      props: {
        dateSearchMode: DateSearchMode.Equal,
        date: undefined,
        dateAfter: undefined,
        dateBefore: undefined,
      },
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
      props: {
        dateSearchMode: DateSearchMode.Equal,
        date: undefined,
        dateAfter: undefined,
        dateBefore: undefined,
      },
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
