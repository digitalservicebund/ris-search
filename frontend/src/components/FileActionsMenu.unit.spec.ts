import { mount } from "@vue/test-utils";
import FileActionsMenu from "./FileActionsMenu.vue";

describe("FileActionsMenu.vue", () => {
  const props = {
    xmlUrl: "https://server/myFile.xml",
  } as Record<string, string>;
  it(`renders the small-viewport menu items on toggle`, async () => {
    const wrapper = mount(FileActionsMenu, {
      props,
      global: {
        stubs: {
          teleport: true,
        },
      },
    });

    expect(wrapper.findAll("a")).toHaveLength(0);

    const button = wrapper.find("button");
    button.element.click();

    await nextTick();

    const links = wrapper.findAll("a");
    expect(links).toHaveLength(2);
    expect(links[0].element.href).toBe("");
    expect(links[1].element.href).toBe("https://server/myFile.xml");
  });

  it(`renders the larger-viewport buttons`, async () => {
    const wrapper = mount(FileActionsMenu, {
      props,
    });
    const toggleButton = wrapper.get("button");
    expect(toggleButton.classes()).toContain("sm:hidden");

    const actionButtons = wrapper.findAll(".hidden button");

    expect(actionButtons).toHaveLength(3);
    expect(actionButtons[0].attributes("data-p-disabled")).toBe("true");
    expect(actionButtons[0].attributes("aria-label")).toBe("Link kopieren");

    expect(actionButtons[1].attributes("data-p-disabled")).toBe("false");
    expect(actionButtons[1].attributes("aria-label")).toBe(
      "Drucken oder als PDF speichern",
    );

    expect(actionButtons[2].attributes("data-p-disabled")).toBe("false");
    expect(actionButtons[2].attributes("aria-label")).toBe("XML anzeigen");
  });
});
