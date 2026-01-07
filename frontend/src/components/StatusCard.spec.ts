import { render, screen } from "@testing-library/vue";
import StatusCard, { StatusCardType } from "./StatusCard.vue";

const factory = (
  status: StatusCardType,
  header: string = "Test Header",
  content: string = "Test Content",
) =>
  render(StatusCard, {
    props: {
      header: header,
      content: content,
      status: status,
    },
  });
describe("StatusCard", () => {
  it("renders correctly with given props", () => {
    factory(StatusCardType.IMPLEMENTED);
    expect(screen.getByText("Test Header")).toBeInTheDocument();
    expect(screen.getByText("Test Content")).toBeInTheDocument();
  });

  it("displays the correct label for IMPLEMENTED status", () => {
    factory(StatusCardType.IMPLEMENTED);
    expect(screen.getByText("Erste Version verfügbar")).toBeInTheDocument();
  });

  it("displays the correct label for IN_PROGRESS status", () => {
    factory(StatusCardType.IN_PROGRESS);
    expect(screen.getByText("In Arbeit")).toBeInTheDocument();
  });

  it("displays the correct label for PLANNED status", () => {
    factory(StatusCardType.PLANNED);
    expect(screen.getByText("Geplant")).toBeInTheDocument();
  });
});
