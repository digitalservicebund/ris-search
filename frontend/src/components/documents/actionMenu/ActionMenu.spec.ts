import { renderSuspended } from "@nuxt/test-utils/runtime";
import { userEvent } from "@testing-library/user-event";
import { screen, waitFor } from "@testing-library/vue";
import Tooltip from "primevue/tooltip";
import { vi } from "vitest";
import ActionMenu, {
  type ActionMenuItem,
} from "~/components/documents/actionMenu/ActionMenu.vue";

const mockCommand = vi.fn();
const actions: ActionMenuItem[] = [
  {
    label: "Command Action",
    iconComponent: h("span", "CommandIcon"),
    command: mockCommand,
  },
  {
    label: "Navigate Action",
    url: "https://example.com",
    iconComponent: h("span", "NavigateIcon"),
  },
];

const nuxtLinkStub = {
  template: '<a :href="to"><slot /></a>',
  props: ["to"],
};

describe("ActionMenu (desktop)", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("renders provided action items", async () => {
    const user = userEvent.setup();

    await renderSuspended(ActionMenu, {
      props: {
        actions: actions,
      },
      global: {
        directives: { tooltip: Tooltip },
        stubs: {
          NuxtLink: nuxtLinkStub,
        },
      },
    });

    const commandActionButton = screen.getByRole("menuitem", {
      name: "Command Action",
    });

    expect(commandActionButton).toBeVisible();
    expect(commandActionButton).toBeEnabled();

    // Displays the provided icon component
    expect(screen.getByText("CommandIcon")).toBeVisible();

    // Shows tooltip when hovered
    await user.hover(commandActionButton);
    expect(await screen.findByRole("tooltip")).toHaveTextContent(
      "Command Action",
    );

    // Executes provided command when clicked
    expect(mockCommand).not.toHaveBeenCalled();
    await user.click(commandActionButton);
    expect(mockCommand).toHaveBeenCalledOnce();

    const navigateActionLink = screen.getByRole("menuitem", {
      name: "Navigate Action",
    });

    expect(navigateActionLink).toBeVisible();
    expect(navigateActionLink).toBeEnabled();
    expect(navigateActionLink).toHaveAttribute("href", "https://example.com");

    // Displays the provided icon component
    expect(screen.getByText("NavigateIcon")).toBeVisible();

    // Shows tooltip when hovered
    await user.hover(navigateActionLink);
    expect(await screen.findByRole("tooltip")).toHaveTextContent(
      "Navigate Action",
    );
  });

  it("renders disabled action items", async () => {
    const user = userEvent.setup();

    await renderSuspended(ActionMenu, {
      props: {
        actions: [
          {
            label: "Disabled Action",
            iconComponent: h("span", "DisabledIcon"),
            command: mockCommand,
            disabled: true,
          },
        ],
      },
      global: {
        directives: { tooltip: Tooltip },
      },
    });

    const disabledButton = screen.getByRole("menuitem", {
      name: "Disabled Action",
    });

    expect(disabledButton).toBeVisible();
    expect(disabledButton).toBeDisabled();

    // Executes provided command when clicked
    expect(mockCommand).not.toHaveBeenCalled();
    await user.click(disabledButton);
    expect(mockCommand).not.toHaveBeenCalled();
  });
});

describe("ActionMenu (mobile)", () => {
  const renderDrawer = (drawerActions: ActionMenuItem[]) =>
    renderSuspended(ActionMenu, {
      props: { actions: drawerActions },
      global: { stubs: { NuxtLink: nuxtLinkStub } },
    });

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("opens when the trigger button is clicked", async () => {
    const user = userEvent.setup();
    await renderDrawer([]);

    expect(
      screen.queryByRole("dialog", { name: "Aktionen" }),
    ).not.toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Aktionen anzeigen" }));

    expect(screen.getByRole("dialog", { name: "Aktionen" })).toBeVisible();
  });

  it("closes after a command item is clicked", async () => {
    const user = userEvent.setup();
    await renderDrawer([
      { label: "Do Something", iconComponent: h("span"), command: mockCommand },
    ]);

    await user.click(screen.getByRole("button", { name: "Aktionen anzeigen" }));
    await user.click(screen.getByRole("button", { name: "Do Something" }));

    expect(mockCommand).toHaveBeenCalledOnce();
    await waitFor(() =>
      expect(
        screen.queryByRole("dialog", { name: "Aktionen" }),
      ).not.toBeInTheDocument(),
    );
  });

  it("stays open when keepDrawerOpen is true", async () => {
    const user = userEvent.setup();
    await renderDrawer([
      {
        label: "Copy Something",
        iconComponent: h("span"),
        command: mockCommand,
        keepDrawerOpen: true,
      },
    ]);

    await user.click(screen.getByRole("button", { name: "Aktionen anzeigen" }));
    await user.click(screen.getByRole("button", { name: "Copy Something" }));

    expect(mockCommand).toHaveBeenCalledOnce();
    expect(screen.getByRole("dialog", { name: "Aktionen" })).toBeVisible();
  });

  it("renders navigate items as links with the correct href", async () => {
    const user = userEvent.setup();
    await renderDrawer([
      {
        label: "Go Somewhere",
        iconComponent: h("span"),
        url: "https://example.com",
      },
    ]);

    await user.click(screen.getByRole("button", { name: "Aktionen anzeigen" }));

    const link = screen.getByRole("link", { name: "Go Somewhere" });
    expect(link).toBeVisible();
    expect(link).toHaveAttribute("href", "https://example.com");
  });
});
