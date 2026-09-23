import { renderSuspended } from "@nuxt/test-utils/runtime";
import { userEvent } from "@testing-library/user-event";
import { screen, waitFor } from "@testing-library/vue";
import MobileActionDrawer from "./MobileActionDrawer.vue";

const renderDrawer = (slotContent = "<p>Body content</p>") =>
  renderSuspended(MobileActionDrawer, {
    props: {
      label: "Filtern",
      icon: h("span", { "aria-hidden": "true" }, "FilterIcon"),
    },
    slots: { default: slotContent },
  });

describe("MobileActionDrawer", () => {
  it("renders the trigger button with the given label and icon", async () => {
    await renderDrawer();

    const button = screen.getByRole("button", { name: "Filtern" });
    expect(button).toBeVisible();
    expect(screen.getByText("FilterIcon")).toBeVisible();
  });

  it("opens the drawer when the trigger button is clicked", async () => {
    const user = userEvent.setup();
    await renderDrawer();

    expect(
      screen.queryByRole("dialog", { name: "Filtern" }),
    ).not.toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Filtern" }));

    expect(screen.getByRole("dialog", { name: "Filtern" })).toBeVisible();
  });

  it("renders the slot content inside the drawer", async () => {
    const user = userEvent.setup();
    await renderDrawer("<p>Custom body</p>");

    await user.click(screen.getByRole("button", { name: "Filtern" }));

    expect(screen.getByText("Custom body")).toBeVisible();
  });

  it("emits reset and closes the drawer when 'Zurücksetzen' is clicked", async () => {
    const user = userEvent.setup();
    const { emitted } = await renderDrawer();

    await user.click(screen.getByRole("button", { name: "Filtern" }));
    await user.click(screen.getByRole("button", { name: "Zurücksetzen" }));

    expect(emitted("reset")).toHaveLength(1);
    await waitFor(() =>
      expect(
        screen.queryByRole("dialog", { name: "Filtern" }),
      ).not.toBeInTheDocument(),
    );
  });

  it("emits apply and closes the drawer when 'Anwenden' is clicked", async () => {
    const user = userEvent.setup();
    const { emitted } = await renderDrawer();

    await user.click(screen.getByRole("button", { name: "Filtern" }));
    await user.click(screen.getByRole("button", { name: "Anwenden" }));

    expect(emitted("apply")).toHaveLength(1);
    await waitFor(() =>
      expect(
        screen.queryByRole("dialog", { name: "Filtern" }),
      ).not.toBeInTheDocument(),
    );
  });

  it("closes the drawer without emitting apply when the close button is clicked", async () => {
    const user = userEvent.setup();
    const { emitted } = await renderDrawer();

    await user.click(screen.getByRole("button", { name: "Filtern" }));
    await user.click(screen.getByRole("button", { name: "Schließen" }));

    expect(emitted("apply")).toBeFalsy();
    await waitFor(() =>
      expect(
        screen.queryByRole("dialog", { name: "Filtern" }),
      ).not.toBeInTheDocument(),
    );
  });

  it("returns focus to the trigger button after closing", async () => {
    const user = userEvent.setup();
    await renderDrawer();

    const triggerButton = screen.getByRole("button", { name: "Filtern" });
    await user.click(triggerButton);
    await user.click(screen.getByRole("button", { name: "Schließen" }));

    await waitFor(() => expect(triggerButton).toHaveFocus());
  });
});
