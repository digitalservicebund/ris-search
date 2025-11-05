import { render, screen } from "@testing-library/vue";
import OperatorsHelp from "./OperatorsHelp.vue";

describe("OperatorsHelp", () => {
  it("should render", () => {
    render(OperatorsHelp);
    const texts = screen.getByRole("table").textContent;
    expect(texts).toMatchSnapshot();
  });
});
