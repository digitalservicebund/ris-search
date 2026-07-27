import { userEvent } from "@testing-library/user-event";
import { render, screen } from "@testing-library/vue";
import { itemsPerPageOptions } from "~/utils/search/itemsPerPageOptions";
import ItemsPerPageRadioGroup from "./ItemsPerPageRadioGroup.vue";

describe("ItemsPerPageRadioGroup", () => {
  it("renders an option for each configured page size", () => {
    render(ItemsPerPageRadioGroup);

    const options = screen.getAllByRole("radio");
    expect(options).toHaveLength(itemsPerPageOptions.length);

    for (const option of itemsPerPageOptions) {
      expect(screen.getByRole("radio", { name: option })).toBeInTheDocument();
    }
  });

  it("reflects the current model value as the checked option", () => {
    render(ItemsPerPageRadioGroup, {
      props: { modelValue: "50" },
    });

    expect(screen.getByRole("radio", { name: "50" })).toBeChecked();
    expect(
      screen.getByRole("radio", { name: itemsPerPageOptions[0] }),
    ).not.toBeChecked();
  });

  it("emits the new model value when an option is selected", async () => {
    const user = userEvent.setup();

    const { emitted } = render(ItemsPerPageRadioGroup);

    await user.click(screen.getByRole("radio", { name: "100" }));

    expect(emitted("update:modelValue")).toContainEqual(["100"]);
  });
});
