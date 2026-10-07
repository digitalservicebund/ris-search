import { render, screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import { h } from "vue";
import DataTableCells, { type DataTableColumn } from "./DataTableCells.vue";

type Row = {
  fromDate: string;
  status: string;
};

const columns: DataTableColumn<Row>[] = [
  { key: "fromDate", label: "Gültig ab" },
  { key: "status", label: "Status" },
];

const row: Row = { fromDate: "27.10.2026", status: "Zukünftig in Kraft" };

/**
 * `render` can't infer the component's type parameter and falls back to its
 * constraint, so the strongly typed props need a cast to get through.
 */
function renderCells(
  options: {
    columns?: DataTableColumn<Row>[];
    slots?: Record<string, unknown>;
  } = {},
) {
  return render(DataTableCells, {
    props: { columns: options.columns ?? columns, row },
    slots: options.slots,
  } as never);
}

describe("DataTableCells", () => {
  it("renders a label and the value as plain text for each column", () => {
    renderCells();

    expect(screen.getByText("Gültig ab:")).toBeInTheDocument();
    expect(screen.getByText("27.10.2026")).toBeInTheDocument();
    expect(screen.getByText("Status:")).toBeInTheDocument();
    expect(screen.getByText("Zukünftig in Kraft")).toBeInTheDocument();
  });

  it("separates labels and values with spaces in the text content", () => {
    const { container } = renderCells();

    expect(container).toHaveTextContent(
      "Gültig ab: 27.10.2026 Status: Zukünftig in Kraft",
    );
  });

  it("renders the cell slot and passes row and column as scope", () => {
    renderCells({
      slots: {
        "cell-status": (scope: { row: Row; column: DataTableColumn<Row> }) =>
          h("strong", `${scope.column.key}=${scope.row.status}`),
      },
    });

    expect(screen.getByText("status=Zukünftig in Kraft")).toBeInTheDocument();
    expect(screen.queryByText("Zukünftig in Kraft")).not.toBeInTheDocument();
  });

  it("keeps the plain value for columns without a cell slot", () => {
    renderCells({ slots: { "cell-status": () => "Custom" } });

    expect(screen.getByText("27.10.2026")).toBeInTheDocument();
  });

  it("renders a separator after a column, hidden from assistive technology", () => {
    renderCells({
      columns: [{ ...columns[0]!, separatorAfter: "–" }, columns[1]!],
    });

    expect(screen.getByText("–")).toHaveAttribute("aria-hidden", "true");
  });
});
