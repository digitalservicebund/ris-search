import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import dayjs from "dayjs";
import VersionWarningMessage from "./VersionWarningMessage.vue";

const inForceVersionLink =
  "/gesetze/eli/bund/bgbl-1/2000/s100/2000-01-01/1/deu";

const futureVersion = {
  to: "/gesetze/eli/bund/bgbl-1/2000/s100/2100-01-01/1/deu",
  validFrom: dayjs("2100-01-01"),
};

const linkStub = {
  NuxtLink: {
    template: '<a :href="to"><slot /></a>',
    props: ["to"],
  },
};

describe("VersionWarningMessage", () => {
  it("shows an info for an in force version with a future version", async () => {
    await renderSuspended(VersionWarningMessage, {
      props: {
        documentTerm: "Fassung",
        currentVersionValidityStatus: "InForce",
        inForceVersionLink,
        futureVersion,
      },
      global: { stubs: linkStub },
    });

    expect(
      screen.getByText("Ab 01.01.2100 gilt eine neue Fassung."),
    ).toBeInTheDocument();

    const link = screen.getByRole("link", {
      name: "Zur zukünftigen Fassung",
      description: "Ab 01.01.2100 gilt eine neue Fassung.",
    });
    expect(link).toHaveAttribute("href", futureVersion.to);
  });

  it("shows no message for an in force version without a future version", async () => {
    await renderSuspended(VersionWarningMessage, {
      props: {
        documentTerm: "Fassung",
        currentVersionValidityStatus: "InForce",
        inForceVersionLink,
      },
    });

    expect(screen.queryByText(/Fassung/)).not.toBeInTheDocument();
  });

  it("shows no message without a validity status", async () => {
    await renderSuspended(VersionWarningMessage, {
      props: { documentTerm: "Fassung", inForceVersionLink, futureVersion },
    });

    expect(screen.queryByText(/Fassung/)).not.toBeInTheDocument();
  });

  it.each([
    ["Expired", "Sie lesen eine historische Fassung."],
    ["FutureInForce", "Sie lesen eine zukünftige Fassung."],
  ] as const)(
    "shows a message linking to the in force version for status %s",
    async (status, text) => {
      await renderSuspended(VersionWarningMessage, {
        props: {
          documentTerm: "Fassung",
          currentVersionValidityStatus: status,
          inForceVersionLink,
          futureVersion,
        },
        global: { stubs: linkStub },
      });

      expect(screen.getByText(text)).toBeInTheDocument();

      const link = screen.getByRole("link", {
        name: "Zur aktuell gültigen Fassung",
        description: text,
      });
      expect(link).toHaveAttribute("href", inForceVersionLink);
    },
  );

  it.each([
    ["Expired", "Sie lesen eine historische Fassung."],
    ["FutureInForce", "Sie lesen eine zukünftige Fassung."],
  ] as const)(
    "shows a message without a link for status %s if there is no in force version",
    async (status, text) => {
      await renderSuspended(VersionWarningMessage, {
        props: {
          documentTerm: "Fassung",
          currentVersionValidityStatus: status,
        },
      });

      expect(screen.getByText(text)).toBeInTheDocument();
      expect(screen.queryByRole("link")).not.toBeInTheDocument();
    },
  );

  it("uses the document term in message and link", async () => {
    await renderSuspended(VersionWarningMessage, {
      props: {
        documentTerm: "Gesamtausgabe",
        currentVersionValidityStatus: "InForce",
        futureVersion,
      },
    });

    expect(
      screen.getByText("Ab 01.01.2100 gilt eine neue Gesamtausgabe."),
    ).toBeInTheDocument();
    expect(
      screen.getByRole("link", { name: "Zur zukünftigen Gesamtausgabe" }),
    ).toBeInTheDocument();
  });
});
