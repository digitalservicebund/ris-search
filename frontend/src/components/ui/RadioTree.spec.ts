import { userEvent } from "@testing-library/user-event";
import { render, screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import RadioTree, { type RadioTreeItem } from "./RadioTree.vue";

const items: RadioTreeItem[] = [
  { value: "A", label: "Alle Dokumentarten" },
  { value: "N", label: "Gesetze & Verordnungen" },
  {
    value: "R",
    label: "Gerichtsentscheidungen",
    selfLabel: "Alle Gerichtsentscheidungen",
    children: [
      { value: "R.urteil", label: "Urteil" },
      { value: "R.beschluss", label: "Beschluss" },
    ],
  },
  { value: "L", label: "Literaturnachweise" },
];

describe("RadioTree", () => {
  it("renders a radio per item", () => {
    render(RadioTree, { props: { items } });

    for (const name of [
      "Alle Dokumentarten",
      "Gesetze & Verordnungen",
      "Gerichtsentscheidungen",
      "Literaturnachweise",
    ]) {
      expect(screen.getByRole("radio", { name })).toBeInTheDocument();
    }
  });

  it("checks the radio matching the model", () => {
    render(RadioTree, { props: { items, modelValue: "N" } });

    expect(
      screen.getByRole("radio", { name: "Gesetze & Verordnungen" }),
    ).toBeChecked();
    expect(
      screen.getByRole("radio", { name: "Alle Dokumentarten" }),
    ).not.toBeChecked();
  });

  it("groups all radios under one name so only one can be checked", async () => {
    const user = userEvent.setup();
    render(RadioTree, { props: { items, modelValue: "R.urteil" } });

    const names = screen
      .getAllByRole("radio")
      .map((radio) => radio.getAttribute("name"));
    expect(new Set(names).size).toBe(1);

    await user.click(screen.getByRole("radio", { name: "Literaturnachweise" }));

    expect(screen.getByRole("radio", { name: "Urteil" })).not.toBeChecked();
  });

  it("hides the children while the branch is not selected", () => {
    render(RadioTree, { props: { items, modelValue: "N" } });

    expect(screen.queryByText("Urteil")).not.toBeInTheDocument();
    expect(
      screen.queryByText("Alle Gerichtsentscheidungen"),
    ).not.toBeInTheDocument();
  });

  it("expands the branch while the item itself is selected", () => {
    render(RadioTree, { props: { items, modelValue: "R" } });

    expect(screen.getByRole("radio", { name: "Urteil" })).toBeInTheDocument();
  });

  it("expands the branch while one of its children is selected", () => {
    render(RadioTree, { props: { items, modelValue: "R.urteil" } });

    expect(screen.getByRole("radio", { name: "Urteil" })).toBeChecked();
    expect(
      screen.getByRole("radio", { name: "Alle Gerichtsentscheidungen" }),
    ).not.toBeChecked();
  });

  it("collapses the branch again when the selection moves away", async () => {
    const { rerender } = render(RadioTree, {
      props: { items, modelValue: "R.urteil" },
    });

    expect(screen.getByRole("radio", { name: "Urteil" })).toBeInTheDocument();

    await rerender({ modelValue: "L" });

    expect(
      screen.queryByRole("radio", { name: "Urteil" }),
    ).not.toBeInTheDocument();
  });

  it("emits the value of a selected item", async () => {
    const user = userEvent.setup();
    const { emitted } = render(RadioTree, {
      props: { items, modelValue: "A" },
    });

    await user.click(
      screen.getByRole("radio", { name: "Gesetze & Verordnungen" }),
    );

    expect(emitted("update:modelValue")).toContainEqual(["N"]);
  });

  it("emits the value of a selected child", async () => {
    const user = userEvent.setup();
    const { emitted } = render(RadioTree, {
      props: { items, modelValue: "R" },
    });

    await user.click(screen.getByRole("radio", { name: "Urteil" }));

    expect(emitted("update:modelValue")).toContainEqual(["R.urteil"]);
  });

  it("selects an item with children like any other item", async () => {
    const user = userEvent.setup();
    const { emitted } = render(RadioTree, {
      props: { items, modelValue: "A" },
    });

    await user.click(
      screen.getByRole("radio", { name: "Gerichtsentscheidungen" }),
    );

    expect(emitted("update:modelValue")).toContainEqual(["R"]);
  });

  describe("self row", () => {
    it("repeats the item at the top of its children while expanded", () => {
      render(RadioTree, { props: { items, modelValue: "R.urteil" } });

      expect(
        screen.getByText("Alle Gerichtsentscheidungen"),
      ).toBeInTheDocument();
    });

    it("selects the item it belongs to when clicked", async () => {
      const user = userEvent.setup();
      const { emitted } = render(RadioTree, {
        props: { items, modelValue: "R.urteil" },
      });

      await user.click(screen.getByText("Alle Gerichtsentscheidungen"));

      expect(emitted("update:modelValue")).toContainEqual(["R"]);
    });

    it("names the radio it labels, rather than being announced separately", () => {
      render(RadioTree, { props: { items, modelValue: "R" } });

      expect(
        screen.getByRole("radio", { name: "Alle Gerichtsentscheidungen" }),
      ).toBeChecked();
      expect(
        screen.getAllByRole("radio", { name: /Gerichtsentscheidungen/ }),
      ).toHaveLength(1);
    });

    it("leaves the item named after its own row while collapsed", () => {
      render(RadioTree, { props: { items, modelValue: "A" } });

      expect(
        screen.getByRole("radio", { name: "Gerichtsentscheidungen" }),
      ).toBeInTheDocument();
    });

    it("is omitted for items without a selfLabel", () => {
      const withoutSelfLabel: RadioTreeItem[] = [
        {
          value: "R",
          label: "Gerichtsentscheidungen",
          children: [{ value: "R.urteil", label: "Urteil" }],
        },
      ];

      render(RadioTree, {
        props: { items: withoutSelfLabel, modelValue: "R" },
      });

      expect(
        screen.getByRole("radio", { name: "Gerichtsentscheidungen" }),
      ).toBeChecked();
      expect(screen.getByRole("radio", { name: "Urteil" })).toBeInTheDocument();
    });
  });

  it("reflects external model changes", async () => {
    const { rerender } = render(RadioTree, {
      props: { items, modelValue: "A" },
    });

    expect(
      screen.getByRole("radio", { name: "Alle Dokumentarten" }),
    ).toBeChecked();

    await rerender({ modelValue: "L" });

    expect(
      screen.getByRole("radio", { name: "Literaturnachweise" }),
    ).toBeChecked();
    expect(
      screen.getByRole("radio", { name: "Alle Dokumentarten" }),
    ).not.toBeChecked();
  });

  it("checks nothing when the model matches no item", () => {
    render(RadioTree, { props: { items, modelValue: "unknown" } });

    for (const radio of screen.getAllByRole("radio")) {
      expect(radio).not.toBeChecked();
    }
  });
});
