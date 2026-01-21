import userEvent from "@testing-library/user-event/dist/cjs/index.js";
import { render, screen } from "@testing-library/vue";
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

// NOTE: only testing the "desktop" variant here as testing the different
// variants which are based on the screen size is not reliably doable
// without a real browser
describe("ActionMenu", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("renders provided action items", async () => {
    const user = userEvent.setup();

    render(ActionMenu, {
      props: {
        actions: actions,
      },
      global: {
        directives: { tooltip: Tooltip },
        stubs: {
          NuxtLink: {
            template: '<a :href="to"><slot /></a>',
            props: ["to"],
          },
        },
      },
    });

    // Command actions are rendered as buttons
    const commandActionButton = screen.getByRole("button", {
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

    // navigate actions are rendered as links
    const navigateActionLink = screen.getByRole("link", {
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

    render(ActionMenu, {
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

    const disabledButton = screen.getByRole("button", {
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
