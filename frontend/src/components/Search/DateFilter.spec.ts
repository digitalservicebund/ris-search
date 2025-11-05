import userEvent from "@testing-library/user-event";
import { render, screen } from "@testing-library/vue";
import { InputText } from "primevue";
import DateFilter from "./DateFilter.vue";
import { DocumentKind } from "~/types";
import type { DateFilterValue } from "~/utils/search/filterType";

describe("DateFilter", () => {
  describe("caselaw", () => {
    it("shows the filters for caselaw", () => {
      render(DateFilter, {
        props: {
          documentKind: DocumentKind.CaseLaw,
        },
        global: { stubs: { InputMask: InputText } },
      });

      expect(
        screen.getByRole("form", { name: "Filter nach Entscheidungsdatum" }),
      ).toBeInTheDocument();

      expect(
        screen.queryByRole("radio", { name: "Aktuell gültig" }),
      ).not.toBeInTheDocument();

      expect(
        screen.getByRole("radio", { name: "Keine zeitliche Begrenzung" }),
      ).toBeInTheDocument();

      expect(
        screen.getByRole("radio", { name: "Bestimmtes Datum" }),
      ).toBeInTheDocument();

      expect(
        screen.getByRole("radio", { name: "Innerhalb einer Zeitspanne" }),
      ).toBeInTheDocument();
    });

    it("filters by 'Keine zeitliche Begrenzung'", async () => {
      const user = userEvent.setup();

      const { emitted } = render(DateFilter, {
        props: {
          documentKind: DocumentKind.CaseLaw,
          modelValue: { type: "specificDate", from: "2025-09-26" },
        },
        global: { stubs: { InputMask: InputText } },
      });

      await user.click(
        screen.getByRole("radio", { name: "Keine zeitliche Begrenzung" }),
      );

      expect(emitted("update:modelValue")).toContainEqual([
        { type: "allTime", form: undefined, to: undefined },
      ]);
    });

    it("filters by 'Bestimmtes Datum'", async () => {
      const user = userEvent.setup();

      const { emitted } = render(DateFilter, {
        props: {
          documentKind: DocumentKind.CaseLaw,
          modelValue: { type: "allTime" },
        },
        global: { stubs: { InputMask: InputText } },
      });

      await user.click(screen.getByRole("radio", { name: "Bestimmtes Datum" }));

      expect(emitted("update:modelValue")).toContainEqual([
        { type: "specificDate", form: undefined, to: undefined },
      ]);
    });

    it("sets a specific date", async () => {
      const user = userEvent.setup();
      let modelValue: DateFilterValue = { type: "specificDate" };

      render(DateFilter, {
        props: {
          documentKind: DocumentKind.CaseLaw,
          modelValue,
          "onUpdate:modelValue": (val) => (modelValue = val),
        },
        global: { stubs: { InputMask: InputText } },
      });

      await user.type(
        screen.getByRole("textbox", { name: "Datum" }),
        "01.01.2026",
      );

      expect(modelValue).toEqual({ type: "specificDate", from: "2026-01-01" });
    });

    it("filters by 'Innerhalb einer Zeitspanne'", async () => {
      const user = userEvent.setup();

      const { emitted } = render(DateFilter, {
        props: {
          documentKind: DocumentKind.CaseLaw,
          modelValue: { type: "allTime" },
        },
        global: { stubs: { InputMask: InputText } },
      });

      await user.click(
        screen.getByRole("radio", { name: "Innerhalb einer Zeitspanne" }),
      );

      expect(emitted("update:modelValue")).toContainEqual([
        { type: "period", form: undefined, to: undefined },
      ]);
    });

    it("sets 'from' date of a period", async () => {
      const user = userEvent.setup();
      let modelValue: DateFilterValue = { type: "period", to: "2026-12-31" };

      render(DateFilter, {
        props: {
          documentKind: DocumentKind.CaseLaw,
          modelValue,
          "onUpdate:modelValue": (val) => (modelValue = val),
        },
        global: { stubs: { InputMask: InputText } },
      });

      await user.type(
        screen.getByRole("textbox", { name: "von" }),
        "01.01.2026",
      );

      expect(modelValue).toEqual({
        type: "period",
        from: "2026-01-01",
        to: "2026-12-31",
      });
    });

    it("sets 'to' date of a period", async () => {
      const user = userEvent.setup();
      let modelValue: DateFilterValue = { type: "period", from: "2026-01-01" };

      render(DateFilter, {
        props: {
          documentKind: DocumentKind.CaseLaw,
          modelValue,
          "onUpdate:modelValue": (val) => (modelValue = val),
        },
        global: { stubs: { InputMask: InputText } },
      });

      await user.type(
        screen.getByRole("textbox", { name: "bis" }),
        "31.12.2026",
      );

      expect(modelValue).toEqual({
        type: "period",
        from: "2026-01-01",
        to: "2026-12-31",
      });
    });
  });

  describe("norms", () => {
    it("shows the filters for norms", () => {
      render(DateFilter, {
        props: {
          documentKind: DocumentKind.Norm,
        },
        global: { stubs: { InputMask: InputText } },
      });

      expect(
        screen.getByRole("form", { name: "Filter nach Gültigkeit" }),
      ).toBeInTheDocument();

      expect(
        screen.getByRole("radio", { name: "Aktuell gültig" }),
      ).toBeInTheDocument();

      expect(
        screen.getByRole("radio", { name: "Bestimmtes Datum" }),
      ).toBeInTheDocument();

      expect(
        screen.getByRole("radio", { name: "Innerhalb einer Zeitspanne" }),
      ).toBeInTheDocument();

      expect(
        screen.getByRole("radio", { name: "Keine zeitliche Begrenzung" }),
      ).toBeInTheDocument();
    });

    it("filters by 'Aktuell gültig'", async () => {
      const user = userEvent.setup();

      const { emitted } = render(DateFilter, {
        props: {
          documentKind: DocumentKind.Norm,
          modelValue: { type: "specificDate", from: "2025-09-26" },
        },
        global: { stubs: { InputMask: InputText } },
      });

      await user.click(screen.getByRole("radio", { name: "Aktuell gültig" }));

      expect(emitted("update:modelValue")).toContainEqual([
        { type: "currentlyInForce", form: undefined, to: undefined },
      ]);
    });

    it("filters by 'Keine zeitliche Begrenzung'", async () => {
      const user = userEvent.setup();

      const { emitted } = render(DateFilter, {
        props: {
          documentKind: DocumentKind.Norm,
          modelValue: { type: "currentlyInForce" },
        },
        global: { stubs: { InputMask: InputText } },
      });

      await user.click(
        screen.getByRole("radio", { name: "Keine zeitliche Begrenzung" }),
      );

      expect(emitted("update:modelValue")).toContainEqual([
        { type: "allTime", form: undefined, to: undefined },
      ]);
    });

    it("filters by 'Bestimmtes Datum'", async () => {
      const user = userEvent.setup();

      const { emitted } = render(DateFilter, {
        props: {
          documentKind: DocumentKind.Norm,
          modelValue: { type: "allTime" },
        },
        global: { stubs: { InputMask: InputText } },
      });

      await user.click(screen.getByRole("radio", { name: "Bestimmtes Datum" }));

      expect(emitted("update:modelValue")).toContainEqual([
        { type: "specificDate", form: undefined, to: undefined },
      ]);
    });

    it("sets a specific date", async () => {
      const user = userEvent.setup();
      let modelValue: DateFilterValue = { type: "specificDate" };

      render(DateFilter, {
        props: {
          documentKind: DocumentKind.Norm,
          modelValue,
          "onUpdate:modelValue": (val) => (modelValue = val),
        },
        global: { stubs: { InputMask: InputText } },
      });

      await user.type(
        screen.getByRole("textbox", { name: "Datum" }),
        "01.01.2026",
      );

      expect(modelValue).toEqual({ type: "specificDate", from: "2026-01-01" });
    });

    it("filters by 'Innerhalb einer Zeitspanne'", async () => {
      const user = userEvent.setup();

      const { emitted } = render(DateFilter, {
        props: {
          documentKind: DocumentKind.Norm,
          modelValue: { type: "allTime" },
        },
        global: { stubs: { InputMask: InputText } },
      });

      await user.click(
        screen.getByRole("radio", { name: "Innerhalb einer Zeitspanne" }),
      );

      expect(emitted("update:modelValue")).toContainEqual([
        { type: "period", form: undefined, to: undefined },
      ]);
    });

    it("sets 'from' date of a period", async () => {
      const user = userEvent.setup();
      let modelValue: DateFilterValue = { type: "period", to: "2026-12-31" };

      render(DateFilter, {
        props: {
          documentKind: DocumentKind.Norm,
          modelValue,
          "onUpdate:modelValue": (val) => (modelValue = val),
        },
        global: { stubs: { InputMask: InputText } },
      });

      await user.type(
        screen.getByRole("textbox", { name: "von" }),
        "01.01.2026",
      );

      expect(modelValue).toEqual({
        type: "period",
        from: "2026-01-01",
        to: "2026-12-31",
      });
    });

    it("sets 'to' date of a period", async () => {
      const user = userEvent.setup();
      let modelValue: DateFilterValue = { type: "period", from: "2026-01-01" };

      render(DateFilter, {
        props: {
          documentKind: DocumentKind.Norm,
          modelValue,
          "onUpdate:modelValue": (val) => (modelValue = val),
        },
        global: { stubs: { InputMask: InputText } },
      });

      await user.type(
        screen.getByRole("textbox", { name: "bis" }),
        "31.12.2026",
      );

      expect(modelValue).toEqual({
        type: "period",
        from: "2026-01-01",
        to: "2026-12-31",
      });
    });
  });

  describe.todo("administrative regulations", () => {});

  describe.todo("literature", () => {});

  it("sets the default filter when switching document kind", async () => {
    const { rerender } = render(DateFilter, {
      props: {
        documentKind: DocumentKind.Norm,
        modelValue: { type: "currentlyInForce" },
      },
      global: { stubs: { InputMask: InputText } },
    });

    expect(screen.getByRole("radio", { name: "Aktuell gültig" })).toBeChecked();

    await rerender({ documentKind: DocumentKind.CaseLaw });
    expect(
      screen.getByRole("radio", { name: "Keine zeitliche Begrenzung" }),
    ).toBeChecked();

    await rerender({ documentKind: DocumentKind.Norm });
    expect(
      screen.getByRole("radio", { name: "Keine zeitliche Begrenzung" }),
    ).toBeChecked();
  });
});
