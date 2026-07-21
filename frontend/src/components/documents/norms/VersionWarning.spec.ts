import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { render, screen } from "@testing-library/vue";
import type { LegislationExpression } from "~/types/api";
import VersionWarning from "./VersionWarning.vue";

const { useRouteMock } = vi.hoisted(() => ({
  useRouteMock: vi.fn(() => ({ query: {} })),
}));

mockNuxtImport("useRoute", () => useRouteMock);

describe("VersionWarning", () => {
  const testVersions = [
    {
      temporalCoverage: "2020-01-01/2022-12-31",
      legislationIdentifier: "eli/bund/bgbl-1/2020/s1126/2020-08-04/1/deu",
      legislationLegalForce: "NotInForce",
    },
    {
      temporalCoverage: "2023-01-01/2823-12-31",
      legislationIdentifier: "eli/bund/bgbl-1/2020/s1126/2023-01-01/1/deu",
      legislationLegalForce: "InForce",
    },
    {
      temporalCoverage: "2824-01-01/2923-12-31",
      legislationIdentifier: "eli/bund/bgbl-1/2020/s1126/2024-01-01/1/deu",
      legislationLegalForce: "NotInForce",
    },
    {
      temporalCoverage: "2924-01-01/..",
      legislationIdentifier: "eli/bund/bgbl-1/2020/s1126/2924-01-01/1/deu",
      legislationLegalForce: "NotInForce",
    },
  ] as LegislationExpression[];

  const testCases = [
    {
      label: "inForce points to next neue Fassung",
      currentlyRendered: {
        temporalCoverage: testVersions[1]!.temporalCoverage,
        legislationIdentifier: testVersions[1]!.legislationIdentifier,
        legislationLegalForce: testVersions[1]!.legislationLegalForce,
      },
      messageText: "Ab 01.01.2824 gilt eine neue Fassung.",
      link: `/gesetze/${testVersions[2]!.legislationIdentifier}`,
      linkText: "Zur zukünftigen Fassung",
    },
    {
      label: "historic version points to inForce Fassung",
      currentlyRendered: {
        temporalCoverage: testVersions[0]!.temporalCoverage,
        legislationIdentifier: testVersions[0]!.legislationIdentifier,
        legislationLegalForce: testVersions[0]!.legislationLegalForce,
      },
      messageText: "Sie lesen eine historische Fassung.",
      link: `/gesetze/${testVersions[1]!.legislationIdentifier}`,
      linkText: "Zur aktuell gültigen Fassung",
    },
    {
      label: "futureInForce points to inForce Fassung",
      currentlyRendered: {
        temporalCoverage: testVersions[2]!.temporalCoverage,
        legislationIdentifier: testVersions[2]!.legislationIdentifier,
        legislationLegalForce: testVersions[2]!.legislationLegalForce,
      },
      messageText: "Sie lesen eine zukünftige Fassung.",
      link: `/gesetze/${testVersions[1]!.legislationIdentifier}`,
      linkText: "Zur aktuell gültigen Fassung",
    },
  ];

  it.each(testCases)("$label", async (testData) => {
    render(VersionWarning, {
      props: {
        versions: testVersions,
        currentVersion: testData.currentlyRendered as LegislationExpression,
      },
      global: {
        stubs: {
          RouterLink: {
            props: ["to"],
            template: '<a :href="to.path"><slot/></a>',
          },
        },
      },
    });

    const fassungText = await screen.findByText((content) =>
      content.includes(testData.messageText),
    );

    expect(fassungText).toBeInTheDocument();

    const link = screen.getByRole("link", {
      name: testData.linkText,
      description: testData.messageText,
    });

    expect(link).toBeInTheDocument();
    expect(link).toHaveAttribute("href", testData.link);
  });

  it("does not render a message if there are no future versions existing for the current in force version", () => {
    const { container } = render(VersionWarning, {
      props: {
        versions: [testVersions[0]!, testVersions[1]!],
        currentVersion: {
          temporalCoverage: testVersions[1]!.temporalCoverage,
          legislationIdentifier: testVersions[1]!.legislationIdentifier,
          legislationLegalForce: testVersions[1]!.legislationLegalForce,
        } as LegislationExpression,
      },
      global: {
        stubs: ["RouterLink"],
      },
    });

    expect(container).toBeEmptyDOMElement();
  });

  it("renders message but no link when no InForce version exists", () => {
    const versionsWithoutInForce = [
      testVersions[0]!,
      testVersions[2]!,
      testVersions[3]!,
    ];

    const { container } = render(VersionWarning, {
      props: {
        versions: versionsWithoutInForce,
        currentVersion: {
          temporalCoverage: testVersions[0]!.temporalCoverage,
          legislationIdentifier: testVersions[0]!.legislationIdentifier,
          legislationLegalForce: testVersions[0]!.legislationLegalForce,
        } as LegislationExpression,
      },
      global: {
        stubs: {
          RouterLink: {
            props: ["to"],
            template: '<a :href="to"><slot/></a>',
          },
        },
      },
    });

    expect(
      screen.getByText("Sie lesen eine historische Fassung."),
    ).toBeInTheDocument();
    expect(screen.queryByRole("link")).toBeNull();
    expect(container).not.toBeEmptyDOMElement();
  });

  it("keeps the from query parameter in version links", async () => {
    useRouteMock.mockReturnValue({ query: { from: "/suche?q=test" } });

    render(VersionWarning, {
      props: {
        versions: testVersions,
        currentVersion: testVersions[0] as LegislationExpression,
      },
      global: {
        stubs: {
          RouterLink: {
            props: ["to"],
            template:
              '<a :href="to.path" :data-from="to.query?.from"><slot/></a>',
          },
        },
      },
    });

    const link = await screen.findByRole("link", {
      name: "Zur aktuell gültigen Fassung",
    });
    expect(link).toHaveAttribute("data-from", "/suche?q=test");
  });
});
