import { vi } from "vitest";
import type { ActionMenuItem } from "~/components/ActionMenu/ActionsMenu.vue";
import { createActionMenuItems } from "~/utils/actionMenu";
import PdfIcon from "~icons/custom/pdf";
import UpdatingLinkIcon from "~icons/custom/updatingLink";
import XmlIcon from "~icons/custom/xml";
import MaterialSymbolsLink from "~icons/material-symbols/link";
import MaterialSymbolsPrint from "~icons/material-symbols/print";

const { mockNavigationCommand, mockCopyUrlCommand } = vi.hoisted(() => ({
  mockNavigationCommand: vi.fn(),
  mockCopyUrlCommand: vi.fn(),
}));

const minimalExpectedActions = [
  {
    key: "permalink",
    label: "Link kopieren",
    iconComponent: MaterialSymbolsLink,
    url: "https://permalink.com/",
  },
  {
    key: "print",
    label: "Drucken",
    iconComponent: MaterialSymbolsPrint,
  },
  {
    key: "pdf",
    label: "Als PDF speichern",
    iconComponent: PdfIcon,
    disabled: true,
  },
];

function findCommandForActionWithKey(actions: ActionMenuItem[], key: string) {
  return actions.find((action) => action.key === key)?.command;
}

describe("actionMenuUtils.ts", () => {
  it("returns permalink, print and pdf actions if only permalink is given", () => {
    const result = createActionMenuItems(
      {
        permalink: {
          url: "https://permalink.com/",
          label: "Link kopieren",
        },
      },
      mockCopyUrlCommand,
      mockNavigationCommand,
    );

    expect(result).toMatchObject(minimalExpectedActions);
  });

  it("prepends link action if given", () => {
    const result = createActionMenuItems(
      {
        link: {
          url: "https://link.com/",
          label: "Copy link",
        },
        permalink: {
          url: "https://permalink.com/",
          label: "Link kopieren",
          disabled: true,
        },
      },
      mockCopyUrlCommand,
      mockNavigationCommand,
    );

    const expectedActions = [
      {
        key: "link",
        label: "Copy link",
        iconComponent: UpdatingLinkIcon,
        url: "https://link.com/",
      },
      ...minimalExpectedActions,
    ];

    expect(result).toMatchObject(expectedActions);
  });

  it("appends xml action if given", () => {
    const result = createActionMenuItems(
      {
        permalink: {
          url: "https://permalink.com/",
          label: "Link kopieren",
          disabled: true,
        },
        xmlUrl: "https://xml.xml",
      },
      mockCopyUrlCommand,
      mockNavigationCommand,
    );

    const expectedActions = [
      ...minimalExpectedActions,
      {
        key: "xml",
        label: "XML anzeigen",
        iconComponent: XmlIcon,
        url: "https://xml.xml",
        dataAttribute: "xml-view",
      },
    ];

    expect(result).toMatchObject(expectedActions);
  });

  it("sets copyLink callback for link actions", async () => {
    const result = createActionMenuItems(
      {
        link: {
          url: "https://link.com/",
          label: "Copy link",
        },
        permalink: {
          url: "https://permalink.com/",
          label: "Copy permalink",
        },
      },
      mockCopyUrlCommand,
      mockNavigationCommand,
    );

    const copyLinkCommand = findCommandForActionWithKey(result, "link");
    copyLinkCommand?.();
    expect(mockCopyUrlCommand).toHaveBeenCalledWith("https://link.com/");

    const copyPermalinkCommand = findCommandForActionWithKey(
      result,
      "permalink",
    );
    copyPermalinkCommand?.();
    expect(mockCopyUrlCommand).toHaveBeenCalledWith("https://permalink.com/");
  });

  it("print action command opens print dialog", () => {
    const printSpy = vi.spyOn(globalThis, "print").mockImplementation(() => {});
    const result = createActionMenuItems(
      {
        permalink: {
          url: "https://permalink.com/",
          label: "Link kopieren",
          disabled: true,
        },
      },
      mockCopyUrlCommand,
      mockNavigationCommand,
    );

    const printCommand = findCommandForActionWithKey(result, "print");
    printCommand?.();

    expect(printSpy).toHaveBeenCalled();

    printSpy.mockRestore();
  });

  it("can navigate to xml", () => {
    const result = createActionMenuItems(
      {
        permalink: {
          url: "",
          label: "Foo",
        },
        xmlUrl: "https://xml.xml",
      },
      mockCopyUrlCommand,
      mockNavigationCommand,
    );

    const xmlCommand = findCommandForActionWithKey(result, "xml");
    xmlCommand?.();

    expect(mockNavigationCommand).toHaveBeenCalledExactlyOnceWith(
      "https://xml.xml",
    );
  });
});
