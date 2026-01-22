import { describe, expect, it } from "vitest";
import { useXmlActionItem } from "~/composables/useActionMenuItem/useXmlActionItem";
import XmlIcon from "~icons/custom/xml";

describe("useXmlActionItem", () => {
  it("creates an ActionMenuItem with with xml url", async () => {
    const xmlItem = useXmlActionItem("https://example.com/foo.xml");

    expect(xmlItem.label).toEqual("XML anzeigen");
    expect(xmlItem.iconComponent).toEqual(XmlIcon);
    expect(xmlItem.disabled).toBeFalsy();
    expect(xmlItem.url).toEqual("https://example.com/foo.xml");
    expect(xmlItem.command).toBeUndefined();
  });

  it("sets disabled to true if the url is undefined", async () => {
    const xmlItem = useXmlActionItem();

    expect(xmlItem.label).toEqual("XML anzeigen");
    expect(xmlItem.iconComponent).toEqual(XmlIcon);
    expect(xmlItem.disabled).toBeTruthy();
    expect(xmlItem.url).toBeUndefined();
    expect(xmlItem.command).toBeUndefined();
  });
});
