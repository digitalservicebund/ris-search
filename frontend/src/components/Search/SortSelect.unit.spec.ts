import userEvent from "@testing-library/user-event";
import { render, screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import { sortMode } from "../types";
import SortOptionsComponent from "./SortSelect.vue";
import { DocumentKind } from "~/types";

describe("SortSelect", () => {
  it("computes correct sort options for 'all' document kind", async () => {
    const user = userEvent.setup();

    render(SortOptionsComponent, {
      props: {
        documentKind: DocumentKind.All,
      },
    });

    await user.click(screen.getByRole("combobox"));

    const options = screen.getAllByRole("option");
    expect(options).toHaveLength(3);
    expect(options[0]).toHaveTextContent("Relevanz");
    expect(options[1]).toHaveTextContent("Datum: Älteste zuerst");
    expect(options[2]).toHaveTextContent("Datum: Neueste zuerst");
  });

  it("computes correct sort options for 'norm' document kind", async () => {
    const user = userEvent.setup();

    render(SortOptionsComponent, {
      props: {
        documentKind: DocumentKind.Norm,
      },
    });

    await user.click(screen.getByRole("combobox"));

    const options = screen.getAllByRole("option");
    expect(options).toHaveLength(3);
    expect(options[0]).toHaveTextContent("Relevanz");
    expect(options[1]).toHaveTextContent("Ausfertigungsdatum: Älteste zuerst");
    expect(options[2]).toHaveTextContent("Ausfertigungsdatum: Neueste zuerst");
  });

  it("computes correct sort options for 'caselaw' document kind", async () => {
    const user = userEvent.setup();

    render(SortOptionsComponent, {
      props: {
        documentKind: DocumentKind.CaseLaw,
      },
    });

    await user.click(screen.getByRole("combobox"));

    const options = screen.getAllByRole("option");
    expect(options).toHaveLength(5);
    expect(options[0]).toHaveTextContent("Relevanz");
    expect(options[1]).toHaveTextContent("Gericht: Von A nach Z");
    expect(options[2]).toHaveTextContent("Gericht: Von Z nach A");
    expect(options[3]).toHaveTextContent("Entscheidungsdatum: Älteste zuerst");
    expect(options[4]).toHaveTextContent("Entscheidungsdatum: Neueste zuerst");
  });

  it("updates sort options when the document kind changes", async () => {
    const user = userEvent.setup();

    const { rerender } = render(SortOptionsComponent, {
      props: {
        documentKind: DocumentKind.All,
      },
    });

    await rerender({ documentKind: DocumentKind.CaseLaw });

    await user.click(screen.getByRole("combobox"));

    const options = screen.getAllByRole("option");
    expect(options).toHaveLength(5);
    expect(options[0]).toHaveTextContent("Relevanz");
    expect(options[1]).toHaveTextContent("Gericht: Von A nach Z");
    expect(options[2]).toHaveTextContent("Gericht: Von Z nach A");
    expect(options[3]).toHaveTextContent("Entscheidungsdatum: Älteste zuerst");
    expect(options[4]).toHaveTextContent("Entscheidungsdatum: Neueste zuerst");
  });

  it("emits the new model value when the dropdown value changes", async () => {
    const user = userEvent.setup();

    const { emitted } = render(SortOptionsComponent, {
      props: {
        documentKind: DocumentKind.All,
      },
    });

    await user.click(screen.getByRole("combobox"));
    await user.click(
      screen.getByRole("option", { name: "Datum: Älteste zuerst" }),
    );

    expect(emitted("update:modelValue")).toContainEqual(["date"]);
  });

  it("resets the filter value when the filter is not supported by the new document kind", async () => {
    const { emitted, rerender } = render(SortOptionsComponent, {
      props: {
        documentKind: DocumentKind.CaseLaw,
        modelValue: sortMode.courtName,
      },
    });

    await rerender({ documentKind: DocumentKind.Norm });

    expect(emitted("update:modelValue")).toContainEqual(["default"]);
  });

  it("keeps the filter value when the filter is supported by the new document kind", async () => {
    const { emitted, rerender } = render(SortOptionsComponent, {
      props: {
        documentKind: DocumentKind.CaseLaw,
        modelValue: sortMode.date,
      },
    });

    await rerender({ documentKind: DocumentKind.Norm });

    expect(emitted("update:modelValue")).toBeFalsy();
  });
});
