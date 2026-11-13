import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import type { Zitierungen as ZitierungenData } from "~/utils/zitierungen";
import Zitierungen from "./Zitierungen.vue";

const emptyZitierungen: ZitierungenData = {
  aktivRechtsprechung: [],
  passivRechtsprechung: [],
  passivLiteratur: [],
};

describe("Zitierungen", () => {
  it("renders passiv and aktiv zitierungen with their headings", async () => {
    await renderSuspended(Zitierungen, {
      props: {
        zitierungen: {
          passivRechtsprechung: [{ text: "BGH — 1 ZR 1/24" }],
          passivLiteratur: [{ text: "Müller; NJW 2024, 1" }],
          aktivRechtsprechung: [{ text: "BVerfG — 1 BvR 1/24" }],
        },
      },
    });

    expect(
      screen.getByRole("heading", {
        level: 3,
        name: "Diese Entscheidung wird zitiert in",
      }),
    ).toBeInTheDocument();
    expect(
      screen.getByRole("heading", {
        level: 3,
        name: "Diese Entscheidung zitiert",
      }),
    ).toBeInTheDocument();
    expect(
      screen.getAllByRole("heading", {
        level: 4,
        name: "Gerichtsentscheidungen",
      }),
    ).toHaveLength(2);
    expect(
      screen.getByRole("heading", { level: 4, name: "Literaturnachweise" }),
    ).toBeInTheDocument();
    expect(
      screen.getAllByRole("listitem").map((item) => item.textContent?.trim()),
    ).toEqual([
      "BGH — 1 ZR 1/24",
      "Müller; NJW 2024, 1",
      "BVerfG — 1 BvR 1/24",
    ]);
  });

  it("renders all entries of a list in order", async () => {
    await renderSuspended(Zitierungen, {
      props: {
        zitierungen: {
          ...emptyZitierungen,
          passivLiteratur: [{ text: "Erste" }, { text: "Zweite" }],
        },
      },
    });

    expect(
      screen.getAllByRole("listitem").map((item) => item.textContent?.trim()),
    ).toEqual(["Erste", "Zweite"]);
  });

  it("renders only the aktiv section when there are no passiv zitierungen", async () => {
    await renderSuspended(Zitierungen, {
      props: {
        zitierungen: {
          ...emptyZitierungen,
          aktivRechtsprechung: [{ text: "BVerfG — 1 BvR 1/24" }],
        },
      },
    });

    expect(
      screen.getByRole("heading", {
        level: 3,
        name: "Diese Entscheidung zitiert",
      }),
    ).toBeInTheDocument();
    expect(
      screen.queryByRole("heading", {
        name: "Diese Entscheidung wird zitiert in",
      }),
    ).not.toBeInTheDocument();
    expect(screen.getByRole("listitem")).toHaveTextContent(
      "BVerfG — 1 BvR 1/24",
    );
  });

  it("renders only the passiv section when there are no aktiv zitierungen", async () => {
    await renderSuspended(Zitierungen, {
      props: {
        zitierungen: {
          ...emptyZitierungen,
          passivRechtsprechung: [{ text: "BGH — 1 ZR 1/24" }],
        },
      },
    });

    expect(
      screen.getByRole("heading", {
        level: 3,
        name: "Diese Entscheidung wird zitiert in",
      }),
    ).toBeInTheDocument();
    expect(
      screen.queryByRole("heading", { name: "Diese Entscheidung zitiert" }),
    ).not.toBeInTheDocument();
    expect(
      screen.queryByRole("heading", { name: "Literaturnachweise" }),
    ).not.toBeInTheDocument();
  });

  it("renders passiv Literatur without a Gerichtsentscheidungen subheading", async () => {
    await renderSuspended(Zitierungen, {
      props: {
        zitierungen: {
          ...emptyZitierungen,
          passivLiteratur: [{ text: "Müller; NJW 2024, 1" }],
        },
      },
    });

    expect(
      screen.getByRole("heading", {
        level: 3,
        name: "Diese Entscheidung wird zitiert in",
      }),
    ).toBeInTheDocument();
    expect(
      screen.getByRole("heading", { level: 4, name: "Literaturnachweise" }),
    ).toBeInTheDocument();
    expect(
      screen.queryByRole("heading", { name: "Gerichtsentscheidungen" }),
    ).not.toBeInTheDocument();
  });

  it("renders zitierungen with a target as links and others as text", async () => {
    await renderSuspended(Zitierungen, {
      props: {
        zitierungen: {
          ...emptyZitierungen,
          aktivRechtsprechung: [
            {
              text: "BVerfG — 1 BvR 1/24",
              to: { path: "/gerichtsentscheidungen/KORE1" },
            },
            { text: "BGH — 1 ZR 1/24" },
          ],
        },
      },
    });

    expect(
      screen.getByRole("link", { name: "BVerfG — 1 BvR 1/24" }),
    ).toHaveAttribute("href", "/gerichtsentscheidungen/KORE1");
    expect(screen.getAllByRole("link")).toHaveLength(1);
    expect(screen.getByText("BGH — 1 ZR 1/24")).toBeInTheDocument();
  });
});
