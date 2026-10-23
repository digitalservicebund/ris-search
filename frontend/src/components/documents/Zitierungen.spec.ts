import { render, screen } from "@testing-library/vue";
import Zitierungen from "./Zitierungen.vue";

describe("Zitierungen", () => {
  it("renders passiv and aktiv zitierungen with their headings", () => {
    render(Zitierungen, {
      props: {
        zitierungen: {
          passivRechtsprechung: "<ul><li>BGH, 1 ZR 1/24</li></ul>",
          passivLiteratur: "<ul><li>Müller, NJW 2024, 1</li></ul>",
          aktivRechtsprechung: "<ul><li>BVerfG, 1 BvR 1/24</li></ul>",
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
      screen.getAllByRole("listitem").map((item) => item.textContent),
    ).toEqual(["BGH, 1 ZR 1/24", "Müller, NJW 2024, 1", "BVerfG, 1 BvR 1/24"]);
  });

  it("renders only the aktiv section when there are no passiv zitierungen", () => {
    render(Zitierungen, {
      props: {
        zitierungen: {
          aktivRechtsprechung: "<ul><li>BVerfG, 1 BvR 1/24</li></ul>",
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
      "BVerfG, 1 BvR 1/24",
    );
  });

  it("renders only the passiv section when there are no aktiv zitierungen", () => {
    render(Zitierungen, {
      props: {
        zitierungen: {
          passivRechtsprechung: "<ul><li>BGH, 1 ZR 1/24</li></ul>",
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

  it("renders passiv Literatur without a Gerichtsentscheidungen subheading", () => {
    render(Zitierungen, {
      props: {
        zitierungen: {
          passivLiteratur: "<ul><li>Müller, NJW 2024, 1</li></ul>",
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

  it("renders links contained in the zitierungen", () => {
    render(Zitierungen, {
      props: {
        zitierungen: {
          aktivRechtsprechung:
            '<ul><li><a href="/gerichtsentscheidungen/KORE1">BVerfG, 1 BvR 1/24</a></li></ul>',
        },
      },
    });

    expect(
      screen.getByRole("link", { name: "BVerfG, 1 BvR 1/24" }),
    ).toBeInTheDocument();
  });
});
