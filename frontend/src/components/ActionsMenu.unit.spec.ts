import type { DOMWrapper } from "@vue/test-utils";
import { mount } from "@vue/test-utils";
import ActionsMenu, { type ActionsMenuProps } from "./ActionsMenu.vue";
import MdiDotsVertical from "~icons/mdi/dots-vertical";
import { afterEach } from "vitest";

describe("ActionsMenu.vue", () => {
  const command = vi.fn();

  const props = {
    items: [
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
    ],
  } satisfies ActionsMenuProps;

  afterEach(() => {
    command.mockClear();
  });

  const toggleButtonSelector = 'button[aria-label="Aktionen anzeigen"]';

  it("renders the small-viewport menu items on toggle", async () => {
    const wrapper = mount(ActionsMenu, {
      props,
      global: {
        stubs: {
          teleport: true,
        },
        directives: {
          tooltip: () => {},
        },
      },
    });

    expect(wrapper.find('[data-pc-section="list"]').exists()).toBe(false);

    const toggleButton = wrapper.get(
      toggleButtonSelector,
    ) as DOMWrapper<HTMLButtonElement>;
    toggleButton.element.click();

    await nextTick();

    const actions = wrapper.get('[data-pc-section="list"]').findAll("li");
    expect(actions).toHaveLength(2);
    expect(actions[0].text()).toBe("Link");
    expect(actions[0].get("a").attributes("href")).toBe("https://example.com/");

    expect(actions[1].text()).toBe("Action");
    actions[1].get("a").element.click();
    await nextTick();
    expect(command).toHaveBeenCalledOnce();
  });

  it("renders the larger-viewport elements", async () => {
    const wrapper = mount(ActionsMenu, {
      props,
      global: {
        directives: {
          tooltip: () => {},
        },
      },
    });
    const toggleButton = wrapper.get(toggleButtonSelector);
    expect(toggleButton.classes()).toContain("sm:hidden");

    const actionButtonSection = wrapper.get("div.hidden");
    expect(actionButtonSection.classes()).toContain("sm:flex");

    const links = actionButtonSection.findAll("a");
    expect(links).toHaveLength(1);
    expect(links[0].attributes("aria-label")).toBe("Link");
    expect(links[0].element.href).toBe("https://example.com/");

    const actionButtons = actionButtonSection.findAll("button");
    expect(actionButtons).toHaveLength(1);
    expect(actionButtons[0].attributes("aria-label")).toBe("Action");
    actionButtons[0].element.click();
    await nextTick();
    expect(command).toHaveBeenCalledOnce();
  });
});
