import { userEvent } from "@testing-library/user-event";
import { render, screen } from "@testing-library/vue";
import ClearButtonWrapper from "./ClearButtonWrapper.vue";

function renderComponent(options?: {
  clearable?: boolean;
  slotContent?: string;
}) {
  const user = userEvent.setup();
  const utils = render(ClearButtonWrapper, {
    props: {
      clearButtonVisible: options?.clearable,
    },
    slots: {
      default: options?.slotContent ?? '<input type="text" />',
    },
  });
  return { user, ...utils };
}

describe("ClearButtonWrapper", () => {
  it("renders slot content", () => {
    renderComponent({
      slotContent: '<input type="text" aria-label="Datum" />',
    });
    expect(screen.getByRole("textbox", { name: "Datum" })).toBeInTheDocument();
  });

  it("does not show the clear button when clearable is false", () => {
    renderComponent({ clearable: false });
    expect(screen.queryByRole("button")).not.toBeInTheDocument();
  });

  it("does not show the clear button by default", () => {
    renderComponent();
    expect(screen.queryByRole("button")).not.toBeInTheDocument();
  });

  it("shows the clear button when clearable is true", () => {
    renderComponent({ clearable: true });
    expect(
      screen.getByRole("button", { name: "Entfernen" }),
    ).toBeInTheDocument();
  });

  it("emits clear when the button is clicked", async () => {
    const { user, emitted } = renderComponent({ clearable: true });
    await user.click(screen.getByRole("button", { name: "Entfernen" }));
    expect(emitted("clear")).toHaveLength(1);
  });
});
