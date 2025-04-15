import { mount } from "@vue/test-utils";
import FileActionsMenu from "./FileActionsMenu.vue";
describe("FileActionsMenu.vue", () => {
  const cases = {
    XML: {
      props: {
        xmlUrl: "https://server/myFile.xml",
      } as Record<string, string>,
      expectedUrl: "https://server/myFile.xml",
      expectedLabel: "XML anzeigen",
    },
    ZIP: {
      props: {
        zipUrl: "https://server/myFile.zip",
      } as Record<string, string>,
      expectedUrl: "https://server/myFile.zip",
      expectedLabel: "XML-Archiv herunterladen",
    },
  };

  for (const [label, testCase] of Object.entries(cases)) {
    it(`[${label}] renders the small-viewport menu items on toggle`, async () => {
      const wrapper = mount(FileActionsMenu, {
        props: testCase.props,
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
      expect(links).toHaveLength(1);
      expect(links[0].element.href).toBe(testCase.expectedUrl);
    });

    it(`[${label}] renders the larger-viewport buttons`, async () => {
      const wrapper = mount(FileActionsMenu, {
        props: testCase.props,
      });
      const toggleButton = wrapper.get("button");
      expect(toggleButton.classes()).toContain("sm:hidden");

      const actionButtons = wrapper.findAll(".hidden button");

      expect(actionButtons).toHaveLength(3);
      expect(actionButtons[0].attributes("data-p-disabled")).toBe("true");
      expect(actionButtons[0].attributes("aria-label")).toBe("Link kopieren");

      expect(actionButtons[1].attributes("data-p-disabled")).toBe("true");
      expect(actionButtons[1].attributes("aria-label")).toBe("PDF anzeigen");

      expect(actionButtons[2].attributes("data-p-disabled")).toBe("false");
      expect(actionButtons[2].attributes("aria-label")).toBe(
        testCase.expectedLabel,
      );
    });
  }
});
