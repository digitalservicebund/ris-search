import { mockNuxtImport, renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import type { LegislationExpression } from "~/types/api";
import VersionWarningMessage from "./VersionWarningMessage.vue";

const { useRouteMock } = vi.hoisted(() => ({
  useRouteMock: vi.fn(() => ({ query: {} })),
}));

mockNuxtImport("useRoute", () => useRouteMock);

const baseProps = {
  inForceVersionLink: "/norms/eli/bund/bgbl-1/2000/s100/2000-01-01/1/deu",
  historicalWarningMessage: "Paragraf einer historischen Fassung.",
  futureWarningMessage: "Paragraf einer zukünftigen Fassung.",
};

const futureVersion = {
  legislationIdentifier: "future-id",
  temporalCoverage: "2100-01-01/..",
  versionDates: ["2100-01-01"],
} as unknown as LegislationExpression;

describe("VersionWarningMessage", () => {
  it("shows info message for inForce with futureVersion", async () => {
    await renderSuspended(VersionWarningMessage, {
      props: {
        ...baseProps,
        currentVersionValidityStatus: "InForce",
        futureVersion: futureVersion,
      },
      global: {
        stubs: {
          NuxtLink: {
            template: '<a :href="to"><slot /></a>',
            props: ["to"],
          },
        },
      },
    });

    expect(
      screen.getByText("Ab 01.01.2100 gilt eine neue Fassung."),
    ).toBeInTheDocument();

    expect(
      screen.getByRole("link", {
        name: "Zur zukünftigen Fassung",
        description: "Ab 01.01.2100 gilt eine neue Fassung.",
      }),
    ).toBeInTheDocument();
  });

  it("shows no message for inForce without futureVersion", async () => {
    await renderSuspended(VersionWarningMessage, {
      props: {
        ...baseProps,
        currentVersionValidityStatus: "InForce",
      },
    });

    expect(
      screen.queryByText("Ab 01.01.2100 gilt eine neue Fassung."),
    ).not.toBeInTheDocument();

    expect(
      screen.queryByRole("link", {
        name: "Zur aktuell gültigen Fassung",
        description: "Ab 01.01.2100 gilt eine neue Fassung.",
      }),
    ).not.toBeInTheDocument();
  });

  it("shows warning for historical version", async () => {
    await renderSuspended(VersionWarningMessage, {
      props: {
        ...baseProps,
        currentVersionValidityStatus: "Expired",
      },
    });

    expect(
      screen.getByText("Paragraf einer historischen Fassung."),
    ).toBeInTheDocument();

    expect(
      screen.getByRole("link", {
        name: "Zur aktuell gültigen Fassung",
        description: "Paragraf einer historischen Fassung.",
      }),
    ).toBeInTheDocument();
  });

  it("shows warning for future version", async () => {
    await renderSuspended(VersionWarningMessage, {
      props: {
        ...baseProps,
        currentVersionValidityStatus: "FutureInForce",
      },
    });

    expect(
      screen.getByText("Paragraf einer zukünftigen Fassung."),
    ).toBeInTheDocument();

    expect(
      screen.getByRole("link", {
        name: "Zur aktuell gültigen Fassung",
        description: "Paragraf einer zukünftigen Fassung.",
      }),
    ).toBeInTheDocument();
  });

  it("keeps the from query parameter in the future version link", async () => {
    useRouteMock.mockReturnValue({ query: { from: "/search?q=test" } });

    await renderSuspended(VersionWarningMessage, {
      props: {
        ...baseProps,
        currentVersionValidityStatus: "InForce",
        futureVersion: futureVersion,
      },
      global: {
        stubs: {
          NuxtLink: {
            template:
              '<a :href="to.path" :data-from="to.query?.from"><slot /></a>',
            props: ["to"],
          },
        },
      },
    });

    const link = screen.getByRole("link", { name: "Zur zukünftigen Fassung" });
    expect(link).toHaveAttribute("data-from", "/search?q=test");
  });
});
