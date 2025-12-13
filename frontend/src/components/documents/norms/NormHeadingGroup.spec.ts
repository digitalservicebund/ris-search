import { RisExpandableText } from "@digitalservicebund/ris-ui/components";
import { mount, shallowMount } from "@vue/test-utils";
import { describe, it, expect, vi } from "vitest";
import { nextTick } from "vue";
import NormHeadingGroup from "./NormHeadingGroup.vue";
import type { LegislationWork } from "~/types";

const createDefaultProps = () => ({
  htmlParts: {
    heading: "<h1>Test Heading</h1>",
    headingAuthorialNotes: "<p>Test Notes</p>",
    headingAuthorialNotesLength: 180,
  },
  metadata: {
    name: "Test Law Name",
    alternateName: "Test Alternate Name",
  } as LegislationWork,
});

vi.mock("@digitalservicebund/ris-ui/components", () => ({
  RisExpandableText: {
    name: "RisExpandableText",
    props: ["length"],
    template: '<div class="mock-expandable-text"><slot /></div>',
  },
}));

vi.mock("./titles", () => ({
  getNormTitle: vi.fn(() => `getNormTitle title`),
}));

describe("HeadingGroup", () => {
  it("renders correctly with all props provided", async () => {
    const wrapper = mount(NormHeadingGroup, {
      props: createDefaultProps(),
      global: { stubs: { NormHeadingFootnotes: true } },
    });
    await nextTick();
    expect(wrapper.find(".dokumentenkopf").exists()).toBe(true);
    expect(wrapper.get(".ris-heading3-regular").text()).toBe(
      "Test Alternate Name",
    );
    expect(wrapper.findAllComponents(RisExpandableText)).toHaveLength(1);

    const headingElement = wrapper.find("h1");
    expect(headingElement.text()).toBe("Test Heading");

    const notesStub = wrapper.get("norm-heading-footnotes-stub");
    expect(notesStub.attributes("html")).toBe("<p>Test Notes</p>");
    expect(notesStub.attributes("textlength")).toBe("180");
  });

  it("adds data-longtitle attribute for long titles", async () => {
    const longName =
      "Beschwerde- und Rechtsmittelzulassungsgesetz zur Sicherstellung der Glaubwürdigkeit von Entscheidungen der Einzelhandelsgerichte in Angelegenheiten des E-Commerce und der Vertragsabwicklung durch besondere Anforderungen an die Präsentation digitaler Gerichtsentscheidungen sowie zur Änderung des E-Commerce-Marktgesetzes und der Verbraucherinformationsgesetzgebung";
    const props = createDefaultProps();
    props.metadata.name = longName;

    const wrapper = mount(NormHeadingGroup, {
      props,
    });

    await nextTick();

    const headingWrapper = wrapper
      .findComponent(RisExpandableText)
      .find("div[data-longtitle]");

    expect(headingWrapper.attributes("data-longtitle")).toBe("true");
  });

  it("renders just one RisExpandableText if no headingAuthorialNotes are provided", async () => {
    const props = createDefaultProps();
    props.htmlParts.headingAuthorialNotes = "";

    const wrapper = mount(NormHeadingGroup, {
      props,
    });

    await nextTick();

    // Should only have one RisExpandableText component
    expect(wrapper.findAllComponents(RisExpandableText)).toHaveLength(1);
  });

  it("renders the client-only fallback content correctly", async () => {
    // To test the fallback content, we need to mock the client-only behavior
    // In this test, we'll just verify that the fallback template exists

    const wrapper = shallowMount(NormHeadingGroup, {
      props: createDefaultProps(),
      global: {
        stubs: {
          "client-only": {
            template: '<div class="fallback"><slot name="fallback" /></div>',
          },
          NormHeadingFootnotes: true,
        },
      },
    });

    await nextTick();

    // Check if fallback content is rendered
    expect(wrapper.find(".titel").exists()).toBe(false);
  });

  it("renders the fallback content without heading correctly", async () => {
    const props = createDefaultProps();
    props.htmlParts.heading = "";

    const wrapper = shallowMount(NormHeadingGroup, {
      props,
      global: {
        stubs: {
          "client-only": {
            template: '<div><slot name="fallback" /></div>',
          },
        },
      },
    });

    await nextTick();

    // Should render the .titel element in fallback
    expect(wrapper.find(".titel").exists()).toBe(true);
  });
});
