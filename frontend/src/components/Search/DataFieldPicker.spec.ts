import userEvent from "@testing-library/user-event";
import { render, screen } from "@testing-library/vue";
import DataFieldPicker from "./DataFieldPicker.vue";
import { DocumentKind } from "~/types";

describe("DataFieldPicker", () => {
  it("displays the document kind name without count", () => {
    render(DataFieldPicker, {
      props: {
        dataFields: undefined,
        documentKind: DocumentKind.CaseLaw,
      },
    });

    expect(
      screen.getByText("In Gerichtsentscheidungen suchen"),
    ).toBeInTheDocument();
  });

  it("displays the document kind name and count", () => {
    render(DataFieldPicker, {
      props: {
        dataFields: undefined,
        count: 1000,
        documentKind: DocumentKind.CaseLaw,
      },
    });

    expect(
      screen.getByText("In 1.000 Gerichtsentscheidungen suchen"),
    ).toBeInTheDocument();
  });

  it("displays the data fields for the document kind", async () => {
    const { rerender } = render(DataFieldPicker, {
      props: {
        dataFields: {
          [DocumentKind.All]: [],
          [DocumentKind.CaseLaw]: [
            { label: "Caselaw 1", pattern: "C1:($)" },
            { label: "Caselaw 2", pattern: "C2:" },
          ],
          [DocumentKind.Norm]: [
            { label: "Norm 1", pattern: "N1:($)" },
            { label: "Norm 2", pattern: "N2:" },
          ],
          [DocumentKind.Literature]: [
            { label: "Literature 1", pattern: "L1:($)" },
            { label: "Literature 2", pattern: "L2:" },
          ],
          [DocumentKind.AdministrativeDirective]: [],
        },
        documentKind: DocumentKind.CaseLaw,
      },
    });

    expect(
      screen.getByRole("button", { name: "Caselaw 1 einfügen" }),
    ).toBeInTheDocument();

    expect(
      screen.getByRole("button", { name: "Caselaw 2 einfügen" }),
    ).toBeInTheDocument();

    expect(
      screen.queryByRole("button", { name: /Norm/ }),
    ).not.toBeInTheDocument();

    expect(
      screen.queryByRole("button", { name: /Literature/ }),
    ).not.toBeInTheDocument();

    await rerender({ documentKind: DocumentKind.Norm });

    expect(
      screen.getByRole("button", { name: "Norm 1 einfügen" }),
    ).toBeInTheDocument();

    expect(
      screen.getByRole("button", { name: "Norm 2 einfügen" }),
    ).toBeInTheDocument();

    expect(
      screen.queryByRole("button", { name: /Caselaw/ }),
    ).not.toBeInTheDocument();

    expect(
      screen.queryByRole("button", { name: /Literature/ }),
    ).not.toBeInTheDocument();

    await rerender({ documentKind: DocumentKind.Literature });

    expect(
      screen.getByRole("button", { name: "Literature 1 einfügen" }),
    ).toBeInTheDocument();

    expect(
      screen.getByRole("button", { name: "Literature 2 einfügen" }),
    ).toBeInTheDocument();

    expect(
      screen.queryByRole("button", { name: /Caselaw/ }),
    ).not.toBeInTheDocument();

    expect(
      screen.queryByRole("button", { name: /Norm/ }),
    ).not.toBeInTheDocument();
  });

  it("displays the search query", () => {
    render(DataFieldPicker, {
      props: {
        dataFields: undefined,
        documentKind: DocumentKind.CaseLaw,
        modelValue: "test query",
      },
    });

    expect(screen.getByRole("textbox", { name: "Suchanfrage" })).toHaveValue(
      "test query",
    );
  });

  it("updates the search query on typing", async () => {
    const user = userEvent.setup();

    const { emitted } = render(DataFieldPicker, {
      props: {
        dataFields: undefined,
        documentKind: DocumentKind.CaseLaw,
        modelValue: "test query",
      },
    });

    await user.type(screen.getByRole("textbox", { name: "Suchanfrage" }), "x");

    expect(emitted("update:modelValue")).toEqual([["test queryx"]]);
  });

  it("inserts a data field", async () => {
    const user = userEvent.setup();

    const { emitted } = render(DataFieldPicker, {
      props: {
        dataFields: {
          [DocumentKind.All]: [],
          [DocumentKind.CaseLaw]: [{ label: "Caselaw 1", pattern: "CS:" }],
          [DocumentKind.Norm]: [],
          [DocumentKind.Literature]: [],
          [DocumentKind.AdministrativeDirective]: [],
        },
        documentKind: DocumentKind.CaseLaw,
        modelValue: "test query",
      },
    });

    await user.click(
      screen.getByRole("button", { name: "Caselaw 1 einfügen" }),
    );

    expect(emitted("update:modelValue")).toEqual([["test query CS:"]]);
  });

  it("removes the cursor marker from the data field", async () => {
    const user = userEvent.setup();

    const { emitted } = render(DataFieldPicker, {
      props: {
        dataFields: {
          [DocumentKind.All]: [],
          [DocumentKind.CaseLaw]: [{ label: "Caselaw 1", pattern: "CS:$" }],
          [DocumentKind.Norm]: [],
          [DocumentKind.Literature]: [],
          [DocumentKind.AdministrativeDirective]: [],
        },
        documentKind: DocumentKind.CaseLaw,
        modelValue: "test query",
      },
    });

    await user.click(
      screen.getByRole("button", { name: "Caselaw 1 einfügen" }),
    );

    expect(emitted("update:modelValue")).toEqual([["test query CS:"]]);
  });

  it("positions the cursor at the end if the data field has no marker", async () => {
    const user = userEvent.setup();

    // JSDom has some issues around setting the selection properly, using calls
    // to the method that sets the selection as a proxy instead
    const setSelectionRange = vi.spyOn(
      globalThis.HTMLInputElement.prototype,
      "setSelectionRange",
    );

    let modelValue = "test query";

    const { rerender } = render(DataFieldPicker, {
      props: {
        dataFields: {
          [DocumentKind.All]: [],
          [DocumentKind.CaseLaw]: [{ label: "Caselaw 1", pattern: "CS:" }],
          [DocumentKind.Norm]: [],
          [DocumentKind.Literature]: [],
          [DocumentKind.AdministrativeDirective]: [],
        },
        documentKind: DocumentKind.CaseLaw,
        modelValue,
        "onUpdate:modelValue": (val) => (modelValue = val),
      },
    });

    await user.click(
      screen.getByRole("button", { name: "Caselaw 1 einfügen" }),
    );

    await rerender({ modelValue });

    const textbox = screen.getByRole<HTMLInputElement>("textbox");

    expect(textbox).toHaveValue("test query CS:");
    expect(textbox).toHaveFocus();

    expect(setSelectionRange).toHaveBeenCalledWith(14, 14);
  });

  it("positions the cursor in the marker position", async () => {
    const user = userEvent.setup();

    // JSDom has some issues around setting the selection properly, using calls
    // to the method that sets the selection as a proxy instead
    const setSelectionRange = vi.spyOn(
      globalThis.HTMLInputElement.prototype,
      "setSelectionRange",
    );

    let modelValue = "test query";

    const { rerender } = render(DataFieldPicker, {
      props: {
        dataFields: {
          [DocumentKind.All]: [],
          [DocumentKind.CaseLaw]: [{ label: "Caselaw 1", pattern: "CS:($)" }],
          [DocumentKind.Norm]: [],
          [DocumentKind.Literature]: [],
          [DocumentKind.AdministrativeDirective]: [],
        },
        documentKind: DocumentKind.CaseLaw,
        modelValue,
        "onUpdate:modelValue": (val) => (modelValue = val),
      },
    });

    await user.click(
      screen.getByRole("button", { name: "Caselaw 1 einfügen" }),
    );

    await rerender({ modelValue });

    const textbox = screen.getByRole<HTMLInputElement>("textbox");

    expect(textbox).toHaveValue("test query CS:()");
    expect(textbox).toHaveFocus();

    expect(setSelectionRange).toHaveBeenCalledWith(15, 15);
  });

  it("does not add leading whitespace", async () => {
    const user = userEvent.setup();

    const { emitted } = render(DataFieldPicker, {
      props: {
        dataFields: {
          [DocumentKind.All]: [],
          [DocumentKind.CaseLaw]: [{ label: "Caselaw 1", pattern: "CS:" }],
          [DocumentKind.Norm]: [],
          [DocumentKind.Literature]: [],
          [DocumentKind.AdministrativeDirective]: [],
        },
        documentKind: DocumentKind.CaseLaw,
        modelValue: "",
      },
    });

    await user.click(
      screen.getByRole("button", { name: "Caselaw 1 einfügen" }),
    );

    expect(emitted("update:modelValue")).toEqual([["CS:"]]);
  });

  it("does not add double whitespace", async () => {
    const user = userEvent.setup();

    const { emitted } = render(DataFieldPicker, {
      props: {
        dataFields: {
          [DocumentKind.All]: [],
          [DocumentKind.CaseLaw]: [{ label: "Caselaw 1", pattern: "CS:" }],
          [DocumentKind.Norm]: [],
          [DocumentKind.Literature]: [],
          [DocumentKind.AdministrativeDirective]: [],
        },
        documentKind: DocumentKind.CaseLaw,
        modelValue: "test query ",
      },
    });

    await user.click(
      screen.getByRole("button", { name: "Caselaw 1 einfügen" }),
    );

    expect(emitted("update:modelValue")).toEqual([["test query CS:"]]);
  });

  it("emits the submit event on button click", async () => {
    const user = userEvent.setup();

    const { emitted } = render(DataFieldPicker, {
      props: {
        dataFields: undefined,
        documentKind: DocumentKind.CaseLaw,
        loading: false,
      },
    });

    await user.click(screen.getByRole("button", { name: "Suchen" }));

    expect(emitted("submit")).toBeTruthy();
  });

  it("emits the submit event on enter press", async () => {
    const user = userEvent.setup();

    const { emitted } = render(DataFieldPicker, {
      props: {
        dataFields: undefined,
        documentKind: DocumentKind.CaseLaw,
        loading: false,
      },
    });

    await user.type(
      screen.getByRole("textbox", { name: "Suchanfrage" }),
      "{enter}",
    );

    expect(emitted("submit")).toBeTruthy();
  });

  it("does not emit submit events when the component is loading", async () => {
    const user = userEvent.setup();

    const { emitted } = render(DataFieldPicker, {
      props: {
        dataFields: undefined,
        documentKind: DocumentKind.CaseLaw,
        loading: true,
      },
    });

    await user.click(screen.getByRole("button", { name: "Suchen" }));

    await user.type(
      screen.getByRole("textbox", { name: "Suchanfrage" }),
      "{enter}",
    );

    expect(emitted("submit")).toBeFalsy();
  });

  it("associates the ID with the underlying form element", () => {
    render(DataFieldPicker, {
      props: {
        documentKind: DocumentKind.CaseLaw,
        formId: "example",
      },
    });

    // An unlabeled form doesn't have the form role, using a test ID instead.
    // The label was removed because it would be redundant with the search element.
    expect(screen.getByTestId("form")).toHaveAttribute("id", "example");
  });

  it("shows the operators help", () => {
    render(DataFieldPicker, {
      props: {
        documentKind: DocumentKind.CaseLaw,
      },
    });

    expect(screen.getAllByText("Hilfestellung zu Suchoperatoren")).toBeTruthy();
  });
});
