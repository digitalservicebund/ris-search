import { mount } from "@vue/test-utils";
import type { DOMWrapper, VueWrapper } from "@vue/test-utils";
import Menu from "primevue/menu";
import { afterEach } from "vitest";
import type { ActionMenuItem } from "~/components/ActionMenu/ActionsMenu.vue";
import SmallActionsMenu from "~/components/ActionMenu/SmallActionsMenu.vue";
import MdiDotsVertical from "~icons/mdi/dots-vertical";

describe("SmallActionsMenu.vue", () => {
  const command = vi.fn();

  afterEach(() => {
    command.mockClear();
  });

  async function toggleMenu(wrapper: VueWrapper) {
    const toggleButton = wrapper.get(
      'button[aria-label="Aktionen anzeigen"]',
    ) as DOMWrapper<HTMLButtonElement>;
    toggleButton.element.click();
    await nextTick();
  }

  function mountWithActions(actions: ActionMenuItem[]): VueWrapper {
    return mount(SmallActionsMenu, {
      props: {
        actions: actions,
      },
      global: {
        stubs: {
          teleport: true,
        },
        directives: {
          tooltip: () => {},
        },
      },
    });
  }

  it("shows menu after toggle button is clicked", async () => {
    const wrapper = mountWithActions([]);

    expect(wrapper.findComponent(Menu).isVisible()).toBe(false);
    await toggleMenu(wrapper);
    expect(wrapper.findComponent(Menu).isVisible()).toBe(true);
  });

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

    await toggleMenu(wrapper);

    const actions = wrapper.get('[data-pc-section="list"]').findAll("li");
    expect(actions).toHaveLength(2);
    expect(actions[0].text()).toBe("Link");
    expect(actions[0].get("a").attributes("href")).toBe("https://example.com/");

    expect(actions[1].text()).toBe("Action");
    actions[1].get("a").element.click();
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

    await toggleMenu(wrapper);

    const actions = wrapper.get('[data-pc-section="list"]').findAll("li");
    expect(actions).toHaveLength(1);

    expect(actions[0].text()).toBe("Disabled Action");
    expect(actions[0].attributes("aria-disabled")).toBe("true");
    actions[0].get("span").element.click();
    await nextTick();
    expect(command).not.toHaveBeenCalledOnce();
  });
});
