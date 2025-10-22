import { render, screen } from "@testing-library/vue";
import NormVersionWarning from "./NormVersionWarning.vue";
import type { LegislationWork, SearchResult } from "~/types";

describe("NormVersionWarning", () => {
  const testVersions = [
    {
      item: {
        legislationIdentifier: "eli/bund/bgbl-1/2020/s1126/regelungstext-1",
        workExample: {
          temporalCoverage: "2020-01-01/2022-12-31",
          legislationIdentifier:
            "eli/bund/bgbl-1/2020/s1126/2020-08-04/1/deu/regelungstext-1",
          legislationLegalForce: "NotInForce",
        },
      },
    },
    {
      item: {
        legislationIdentifier: "eli/bund/bgbl-1/2020/s1126/regelungstext-1",
        workExample: {
          temporalCoverage: "2023-01-01/2823-12-31",
          legislationIdentifier:
            "eli/bund/bgbl-1/2020/s1126/2023-01-01/1/deu/regelungstext-1",
          legislationLegalForce: "InForce",
        },
      },
    },
    {
      item: {
        legislationIdentifier: "eli/bund/bgbl-1/2020/s1126/regelungstext-1",
        workExample: {
          temporalCoverage: "2824-01-01/2923-12-31",
          legislationIdentifier:
            "eli/bund/bgbl-1/2020/s1126/2024-01-01/1/deu/regelungstext-1",
          legislationLegalForce: "NotInForce",
        },
      },
    },
    {
      item: {
        legislationIdentifier: "eli/bund/bgbl-1/2020/s1126/regelungstext-1",
        workExample: {
          temporalCoverage: "2924-01-01/..",
          legislationIdentifier:
            "eli/bund/bgbl-1/2020/s1126/2924-01-01/1/deu/regelungstext-1",
          legislationLegalForce: "NotInForce",
        },
      },
    },
  ] as SearchResult<LegislationWork>[];

  const testCases = [
    {
      label: "inForce points to next neue Fassung",
      currentlyRendered: testVersions[1]!.item,
      messageText: "Neue Fassung ab 01.01.2824",
      link: `/norms/${testVersions[2]!.item.workExample.legislationIdentifier}`,
      linkText: "Zur zukünftigen Fassung",
    },
    {
      label: "historic version points to inForce Fassung",
      currentlyRendered: testVersions[0]!.item,
      messageText: "Historische Fassung.",
      link: `/norms/${testVersions[1]!.item.workExample.legislationIdentifier}`,
      linkText: "Zur aktuell gültigen Fassung",
    },
    {
      label: "futureInForce points to inForce Fassung",
      currentlyRendered: testVersions[2]!.item,
      messageText: "Zukünftige Fassung",
      link: `/norms/${testVersions[1]!.item.workExample.legislationIdentifier}`,
      linkText: "Zur aktuell gültigen Fassung",
    },
  ];

  test.for(testCases)("$label", async (testData) => {
    render(NormVersionWarning, {
      props: {
        versions: testVersions,
        currentVersion: testData.currentlyRendered,
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

    const fassungText = await screen.findByText((content) =>
      content.includes(testData.messageText),
    );
    const link = screen.getByRole("link");

    expect(fassungText).toBeInTheDocument();
    expect(link).toHaveAttribute("href", testData.link);
    expect(link).toHaveTextContent(testData.linkText);
  });

  it("does not render a message if there are no future versions existing for the current in force version", () => {
    const { container } = render(NormVersionWarning, {
      props: {
        versions: [testVersions[0]!, testVersions[1]!],
        currentVersion: testVersions[1]!.item,
      },
      global: {
        stubs: ["RouterLink"],
      },
    });

    expect(container).toBeEmptyDOMElement();
  });
});
