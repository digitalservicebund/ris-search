import { render, screen } from "@testing-library/vue";
import StatusCard, { type StatusCardType } from "./StatusCard.global.vue";

const factory = (
  status: StatusCardType,
  header: string = "Test Header",
  content: string = "Test Content",
) =>
  render(StatusCard, {
    props: { status },
    slots: {
      header: header,
      default: content,
    },
  });

describe("StatusCard", () => {
  it("renders header and default slots", () => {
    factory("implemented");
    expect(screen.getByText("Test Header")).toBeInTheDocument();
    expect(screen.getByText("Test Content")).toBeInTheDocument();
  });

  it("displays the correct label for IMPLEMENTED status", () => {
    factory("implemented");
    expect(screen.getByText("Erste Version verfügbar")).toBeInTheDocument();
  });

  it("displays the correct label for IN_PROGRESS status", () => {
    factory("in_progress");
    expect(screen.getByText("In Arbeit")).toBeInTheDocument();
  });

  it("displays the correct label for PLANNED status", () => {
    factory("planned");
    expect(screen.getByText("Geplant")).toBeInTheDocument();
  });
});
