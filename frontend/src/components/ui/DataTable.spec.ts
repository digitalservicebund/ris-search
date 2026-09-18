import { render, screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import { h } from "vue";
import DataTable, { type DataTableColumn } from "./DataTable.vue";

type Row = {
  key: string;
  attrs?: Record<string, unknown>;
  current?: boolean;
  fromDate: string;
  status: string;
};

const columns: DataTableColumn<Row>[] = [
  { key: "fromDate", label: "Gültig ab" },
  { key: "status", label: "Status" },
];

const rows: Row[] = [
  { key: "a", fromDate: "27.10.2026", status: "Zukünftig in Kraft" },
  { key: "b", fromDate: "15.08.2025", status: "Aktuell gültig" },
];

type Props = {
  columns: DataTableColumn<Row>[];
  rowAs?: string;
  rows: Row[];
};

/**
 * `render` can't infer the component's type parameter and falls back to its
 * constraint, so the strongly typed props need a cast to get through.
 */
function renderTable(
  props: Props,
  options: {
    slots?: Record<string, unknown>;
    attrs?: Record<string, unknown>;
  } = {},
) {
  return render(DataTable, { props, ...options } as never);
}

const linkedRows = rows.map((row) => ({
  ...row,
  attrs: { href: `/${row.key}` },
}));

describe("DataTable", () => {
  it("renders a list with one item per row", () => {
    renderTable({ columns, rows });

    expect(screen.getByRole("list")).toBeInTheDocument();
    expect(screen.getAllByRole("listitem")).toHaveLength(2);
  });

  it("renders cell values as plain text by default", () => {
    renderTable({ columns, rows });

    expect(screen.getByText("27.10.2026")).toBeInTheDocument();
    expect(screen.getByText("Zukünftig in Kraft")).toBeInTheDocument();
  });

  it("renders the column labels once as a header and once per row", () => {
    renderTable({ columns, rows });

    // The visual header row, which is hidden from assistive technology
    expect(screen.getByText("Gültig ab").closest("li")).toHaveAttribute(
      "aria-hidden",
      "true",
    );

    // The per-row labels, which carry the information for screen readers and
    // are shown next to the value on narrow viewports
    expect(screen.getAllByText("Gültig ab:")).toHaveLength(2);
  });

  it("keeps the decorative header row out of the accessibility tree", () => {
    renderTable({ columns, rows });

    // Would be 3 if the header row were exposed as a list item
    expect(screen.getAllByRole("listitem")).toHaveLength(2);
  });

  it("renders rows as links when rowAs and attrs are given", () => {
    renderTable({ columns, rowAs: "a", rows: linkedRows });

    const links = screen.getAllByRole("link");
    expect(links).toHaveLength(2);
    expect(links[0]).toHaveAttribute("href", "/a");
    expect(links[1]).toHaveAttribute("href", "/b");
  });

  it("gives each row link an accessible name built from labels and values", () => {
    renderTable({ columns, rowAs: "a", rows: [linkedRows[0]!] });

    expect(
      screen.getByRole("link", {
        name: "Gültig ab: 27.10.2026 Status: Zukünftig in Kraft",
      }),
    ).toBeInTheDocument();
  });

  it("marks the current row as the current page", () => {
    renderTable({
      columns,
      rowAs: "a",
      rows: linkedRows.map((row) => ({ ...row, current: row.key === "b" })),
    });

    const links = screen.getAllByRole("link");
    expect(links[0]).not.toHaveAttribute("aria-current");
    expect(links[1]).toHaveAttribute("aria-current", "page");
  });

  it("renders the cell slot and passes row and column as scope", () => {
    renderTable(
      { columns, rows },
      {
        slots: {
          "cell-status": (scope: { row: Row; column: DataTableColumn<Row> }) =>
            h(
              "span",
              { "data-testid": "status" },
              `${scope.column.label}: ${scope.row.status}`,
            ),
        },
      },
    );

    const cells = screen.getAllByTestId("status");
    expect(cells).toHaveLength(2);
    expect(cells[0]).toHaveTextContent("Status: Zukünftig in Kraft");
  });

  it("renders the empty slot when there are no rows", () => {
    renderTable(
      { columns, rows: [] },
      { slots: { empty: () => "Keine Ergebnisse gefunden" } },
    );

    expect(screen.getByText("Keine Ergebnisse gefunden")).toBeInTheDocument();
  });

  it("passes through native attributes", () => {
    renderTable({ columns, rows }, { attrs: { "aria-label": "Fassungen" } });

    expect(screen.getByRole("list", { name: "Fassungen" })).toBeInTheDocument();
  });
});
