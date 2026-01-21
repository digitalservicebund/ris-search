import { userEvent } from "@testing-library/user-event";
import { render, screen } from "@testing-library/vue";
import DataFieldList from "./DataFieldList.vue";

describe("DataFieldList", () => {
  it("renders data fields", () => {
    render(DataFieldList, {
      props: {
        dataFields: [
          { label: "Field 1", pattern: "field1" },
          { label: "Field 2", pattern: "field2" },
        ],
      },
    });

    expect(
      screen.getByRole("button", { name: "Field 1 einfügen" }),
    ).toBeVisible();
    expect(
      screen.getByRole("button", { name: "Field 2 einfügen" }),
    ).toBeVisible();
  });

  it("emits the data field on click", async () => {
    const user = userEvent.setup();

    const { emitted } = render(DataFieldList, {
      props: {
        dataFields: [
          { label: "Field 1", pattern: "field1" },
          { label: "Field 2", pattern: "field2" },
        ],
      },
    });

    await user.click(screen.getByRole("button", { name: "Field 1 einfügen" }));
    expect(emitted("clickDataField")).toEqual([
      [{ label: "Field 1", pattern: "field1" }],
    ]);
  });

  it("does not render anything when the data fields are empty", () => {
    render(DataFieldList, {
      props: {
        dataFields: [],
      },
    });

    expect(screen.queryByRole("button")).not.toBeInTheDocument();
  });
});
