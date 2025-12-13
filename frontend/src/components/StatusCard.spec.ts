import { mount } from "@vue/test-utils";
import StatusCard, { StatusCardType } from "./StatusCard.vue";

const factory = (
  status: StatusCardType,
  header: string = "Test Header",
  content: string = "Test Content",
) =>
  mount(StatusCard, {
    props: {
      header: header,
      content: content,
      status: status,
    },
  });
describe("StatusCard", () => {
  it("renders correctly with given props", () => {
    const wrapper = factory(StatusCardType.IMPLEMENTED);
    expect(wrapper.text()).contains("Test Header");
    expect(wrapper.text()).contains("Test Content");
  });

  it("displays the correct label for IMPLEMENTED status", () => {
    const wrapper = factory(StatusCardType.IMPLEMENTED);
    expect(wrapper.text()).contains("Erste Version verfügbar");
  });

  it("displays the correct label for IN_PROGRESS status", () => {
    const wrapper = factory(StatusCardType.IN_PROGRESS);
    expect(wrapper.text()).contains("In Arbeit");
  });

  it("displays the correct label for PLANNED status", () => {
    const wrapper = factory(StatusCardType.PLANNED);
    expect(wrapper.text()).contains("Geplant");
  });
});
