import { mountSuspended, registerEndpoint } from "@nuxt/test-utils/runtime";
import { useBackendURL } from "~/composables/useBackendURL";
import NormVersionList from "./NormVersionList.vue";
import { data } from "~/components/Norm/NormVersions.spec.data";

registerEndpoint(`${useBackendURL()}/v1/legislation`, () => {
  return data;
});

describe("NormVersionList", () => {
  it("lists versions, sorted by date", async () => {
    const wrapper = await mountSuspended(NormVersionList, {
      props: {
        status: "success",
        versions: data.member,
      },
      global: {
        stubs: {
          NormVersionListRow: {
            template: `<tr><td>{{$attrs.item.workExample.legislationIdentifier}}</td></tr>`,
          },
          IncompleteDataMessage: true,
        },
      },
    });

    const stubbedCells = wrapper.findAll("td");
    expect(stubbedCells).toHaveLength(2);
    expect(stubbedCells.map((cell) => cell.text())).toEqual([
      data.member[0].item.workExample.legislationIdentifier,
      data.member[1].item.workExample.legislationIdentifier,
    ]);
  });
});
