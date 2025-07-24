import { mount, type VueWrapper } from "@vue/test-utils";
import { afterEach } from "vitest";
import type { ActionMenuItem } from "~/components/ActionMenu/ActionsMenu.vue";
import LargeActionsMenu from "~/components/ActionMenu/LargeActionsMenu.vue";
import MdiDotsVertical from "~icons/mdi/dots-vertical";

describe("LargeActionsMenu.vue", () => {
  const command = vi.fn();

  afterEach(() => {
    command.mockClear();
  });

  function mountWithActions(actions: ActionMenuItem[]): VueWrapper {
    return mount(LargeActionsMenu, {
      props: {
        actions: actions,
      },
      global: {
        directives: {
          tooltip: () => {},
        },
      },
    });
  }

  it("renders action items", async () => {
    const wrapper = mountWithActions([
      {
        key: "link",
        iconComponent: MdiDotsVertical,
        label: "Link",
        url: "https://example.com/",
      },
      {
        key: "action",
        command,
        iconComponent: MdiDotsVertical,
        label: "Action",
      },
    ]);

    const links = wrapper.findAll("a");
    expect(links).toHaveLength(1);
    expect(links[0].attributes("aria-label")).toBe("Link");
    expect(links[0].element.href).toBe("https://example.com/");

    const actionButtons = wrapper.findAll("button");
    expect(actionButtons).toHaveLength(1);
    expect(actionButtons[0].attributes("aria-label")).toBe("Action");
    actionButtons[0].element.click();
    await nextTick();
    expect(command).toHaveBeenCalledOnce();
  });

  it("renders disabled action items", async () => {
    const wrapper = mountWithActions([
      {
        key: "action",
        command,
        iconComponent: MdiDotsVertical,
        label: "Disabled Action",
        disabled: true,
      },
    ]);

    const actionButtons = wrapper.findAll("button");
    expect(actionButtons).toHaveLength(1);
    expect(actionButtons[0].attributes("aria-label")).toBe("Disabled Action");
    actionButtons[0].element.click();
    await nextTick();
    expect(command).not.toHaveBeenCalled();
  });
});
