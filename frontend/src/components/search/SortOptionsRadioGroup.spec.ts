import { userEvent } from "@testing-library/user-event";
import { render, screen } from "@testing-library/vue";
import { DocumentKind } from "~/types/api";
import { sortMode } from "~/utils/search/sortMode";
import SortOptionsRadioGroup from "./SortOptionsRadioGroup.vue";

describe("SortOptionsRadioGroup", () => {
  it("computes correct sort options for 'all' document kind", () => {
    render(SortOptionsRadioGroup, {
      props: { documentKind: DocumentKind.All },
    });

    const options = screen.getAllByRole("radio");
    expect(options).toHaveLength(3);
    expect(screen.getByRole("radio", { name: "Relevanz" })).toBeInTheDocument();
    expect(
      screen.getByRole("radio", { name: "Datum: Älteste zuerst" }),
    ).toBeInTheDocument();
    expect(
      screen.getByRole("radio", { name: "Datum: Neueste zuerst" }),
    ).toBeInTheDocument();
  });

  it("computes correct sort options for 'caselaw' document kind", () => {
    render(SortOptionsRadioGroup, {
      props: { documentKind: DocumentKind.CaseLaw },
    });

    expect(screen.getAllByRole("radio")).toHaveLength(5);
    expect(
      screen.getByRole("radio", { name: "Gericht: Von A nach Z" }),
    ).toBeInTheDocument();
    expect(
      screen.getByRole("radio", { name: "Gericht: Von Z nach A" }),
    ).toBeInTheDocument();
  });

  it("reflects the current model value as the checked option", () => {
    render(SortOptionsRadioGroup, {
      props: {
        documentKind: DocumentKind.CaseLaw,
        modelValue: sortMode.date,
      },
    });

    expect(
      screen.getByRole("radio", {
        name: "Entscheidungsdatum: Älteste zuerst",
      }),
    ).toBeChecked();
    expect(screen.getByRole("radio", { name: "Relevanz" })).not.toBeChecked();
  });

  it("emits the new model value when an option is selected", async () => {
    const user = userEvent.setup();

    const { emitted } = render(SortOptionsRadioGroup, {
      props: { documentKind: DocumentKind.All },
    });

    await user.click(
      screen.getByRole("radio", { name: "Datum: Älteste zuerst" }),
    );

    expect(emitted("update:modelValue")).toContainEqual(["date"]);
  });

  it("resets the value when the filter is not supported by the new document kind", async () => {
    const { emitted, rerender } = render(SortOptionsRadioGroup, {
      props: {
        documentKind: DocumentKind.CaseLaw,
        modelValue: sortMode.courtName,
      },
    });

    await rerender({ documentKind: DocumentKind.Norm });

    expect(emitted("update:modelValue")).toContainEqual(["default"]);
  });
});
