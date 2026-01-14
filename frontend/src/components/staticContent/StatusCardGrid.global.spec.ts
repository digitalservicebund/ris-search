import { render, screen } from "@testing-library/vue";
import StatusCardGrid from "./StatusCardGrid.global.vue";

describe("StatusCardGrid", () => {
  it("renders without error", () => {
    render(StatusCardGrid);
    expect(document.querySelector(".grid")).toBeInTheDocument();
  });

  it("renders slot content", () => {
    render(StatusCardGrid, {
      slots: {
        default: "<div>Card 1</div><div>Card 2</div>",
      },
    });
    expect(screen.getByText("Card 1")).toBeInTheDocument();
    expect(screen.getByText("Card 2")).toBeInTheDocument();
  });
});
