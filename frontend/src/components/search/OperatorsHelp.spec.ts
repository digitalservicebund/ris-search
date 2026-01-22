import { userEvent } from "@testing-library/user-event";
import { render, screen } from "@testing-library/vue";
import OperatorsHelp from "./OperatorsHelp.vue";

describe("OperatorsHelp", () => {
  it("should render", () => {
    render(OperatorsHelp);
    const texts = screen.getByRole("table").textContent;
    expect(texts).toMatchSnapshot();
  });

  it("toggles", async () => {
    const user = userEvent.setup();

    render(OperatorsHelp);

    const toggle = screen.getByRole("button", {
      name: "Hilfestellung zu Suchoperatoren",
    });

    await user.click(toggle);
    expect(screen.getByText("Bedeutung")).not.toBeVisible();

    await user.click(toggle);
    expect(screen.getByText("Bedeutung")).toBeVisible();
  });
});
