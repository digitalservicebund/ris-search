import type { VueWrapper } from "@vue/test-utils";
import { expect } from "vitest";
import MetadataField from "~/components/MetadataField.vue";

export function findMetadataField(wrapper: VueWrapper, label: string) {
  const metadataFields = wrapper.findAllComponents(MetadataField);

  const result = metadataFields.filter(
    (fieldWrapper) => fieldWrapper.props().label === label,
  );

  expect(result).toHaveLength(1);
  return result[0];
}
