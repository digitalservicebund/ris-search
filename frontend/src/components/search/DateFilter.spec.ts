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

  describe("administrative directive", () => {
    it("shows the filters for administrative directives", () => {
      render(DateFilter, {
        props: {
          documentKind: DocumentKind.AdministrativeDirective,
        },
        global: { stubs: { InputMask: InputText } },
      });

      expect(
        screen.getByRole("form", { name: "Filter nach Inkrafttreten" }),
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
          documentKind: DocumentKind.AdministrativeDirective,
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
          documentKind: DocumentKind.AdministrativeDirective,
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
          documentKind: DocumentKind.AdministrativeDirective,
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
          documentKind: DocumentKind.AdministrativeDirective,
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
          documentKind: DocumentKind.AdministrativeDirective,
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
          documentKind: DocumentKind.AdministrativeDirective,
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

  describe("literature", () => {
    it("shows the filters for literature", () => {
      render(DateFilter, {
        props: {
          documentKind: DocumentKind.Literature,
        },
        global: { stubs: { InputMask: InputText } },
      });

      expect(
        screen.getByRole("form", { name: "Filter nach Veröffentlichungsjahr" }),
      ).toBeInTheDocument();

      expect(
        screen.queryByRole("radio", { name: "Aktuell gültig" }),
      ).not.toBeInTheDocument();

      expect(
        screen.getByRole("radio", { name: "Keine zeitliche Begrenzung" }),
      ).toBeInTheDocument();

      expect(
        screen.queryByRole("radio", { name: "Bestimmtes Datum" }),
      ).not.toBeInTheDocument();

      expect(
        screen.getByRole("radio", { name: "Innerhalb einer Zeitspanne" }),
      ).toBeInTheDocument();
    });

    it("filters by 'Keine zeitliche Begrenzung'", async () => {
      const user = userEvent.setup();

      const { emitted } = render(DateFilter, {
        props: {
          documentKind: DocumentKind.Literature,
          modelValue: { type: "period", from: "2020", to: "2024" },
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

    it("filters by 'Innerhalb einer Zeitspanne'", async () => {
      const user = userEvent.setup();

      const { emitted } = render(DateFilter, {
        props: {
          documentKind: DocumentKind.Literature,
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

    it("sets 'from' year of a period", async () => {
      const user = userEvent.setup();
      let modelValue: DateFilterValue = { type: "period", to: "2024" };

      render(DateFilter, {
        props: {
          documentKind: DocumentKind.Literature,
          modelValue,
          "onUpdate:modelValue": (val) => (modelValue = val),
        },
        global: { stubs: { InputMask: InputText } },
      });

      await user.type(screen.getByRole("textbox", { name: "von" }), "2020");

      expect(modelValue).toEqual({
        type: "period",
        from: "2020",
        to: "2024",
      });
    });

    it("sets 'to' year of a period", async () => {
      const user = userEvent.setup();
      let modelValue: DateFilterValue = { type: "period", from: "2020" };

      render(DateFilter, {
        props: {
          documentKind: DocumentKind.Literature,
          modelValue,
          "onUpdate:modelValue": (val) => (modelValue = val),
        },
        global: { stubs: { InputMask: InputText } },
      });

      await user.type(screen.getByRole("textbox", { name: "bis" }), "2024");

      expect(modelValue).toEqual({
        type: "period",
        from: "2020",
        to: "2024",
      });
    });
  });

  describe("resetting filter when switching document kind", () => {
    it("defaults to 'all time' when switching to caselaw", async () => {
      let modelValue: DateFilterValue = { type: "currentlyInForce" };

      const { rerender } = render(DateFilter, {
        props: {
          documentKind: DocumentKind.Norm,
          modelValue,
          "onUpdate:modelValue": (val) => (modelValue = val),
        },
        global: { stubs: { InputMask: InputText } },
      });

      await rerender({ documentKind: DocumentKind.CaseLaw });

      expect(modelValue).toEqual({ type: "allTime" });
    });

    it("defaults to 'currently in force' when switching to legislation", async () => {
      let modelValue: DateFilterValue = {
        type: "period",
        from: "2020-03-15",
        to: "2024-09-20",
      };

      const { rerender } = render(DateFilter, {
        props: {
          documentKind: DocumentKind.CaseLaw,
          modelValue,
          "onUpdate:modelValue": (val) => (modelValue = val),
        },
        global: { stubs: { InputMask: InputText } },
      });

      await rerender({ documentKind: DocumentKind.Norm });

      expect(modelValue).toEqual({ type: "currentlyInForce" });
    });

    it("defaults to 'all time' when switching to literature", async () => {
      let modelValue: DateFilterValue = {
        type: "specificDate",
        from: "2024-01-01",
      };

      const { rerender } = render(DateFilter, {
        props: {
          documentKind: DocumentKind.Norm,
          modelValue,
          "onUpdate:modelValue": (val) => (modelValue = val),
        },
        global: { stubs: { InputMask: InputText } },
      });

      await rerender({ documentKind: DocumentKind.Literature });

      expect(modelValue).toEqual({ type: "allTime" });
    });

    it("defaults to 'all time' when switching to administrative directive", async () => {
      let modelValue: DateFilterValue = {
        type: "specificDate",
        from: "2024-01-01",
      };

      const { rerender } = render(DateFilter, {
        props: {
          documentKind: DocumentKind.Norm,
          modelValue,
          "onUpdate:modelValue": (val) => (modelValue = val),
        },
        global: { stubs: { InputMask: InputText } },
      });

      await rerender({ documentKind: DocumentKind.AdministrativeDirective });

      expect(modelValue).toEqual({ type: "allTime" });
    });
  });
});
