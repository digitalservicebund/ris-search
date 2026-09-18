export interface SingleNormVersionCollection {
  member: SingleNorm[];
}

export interface SingleNorm {
  "@id": string;
  eId: string;
  temporalCoverage: string;
  isPartOf: { "@id": string }[];
  encoding: {
    contentUrl: string;
  };
}

// TODO: remove dummy response when backend can be used to get the actual data
const dummyResponse: SingleNormVersionCollection = {
  member: [
    {
      "@id": "eli/bund/bgbl-1/2020/s1126/2020-08-04/1/deu#art-z1",
      eId: "art-z1",
      temporalCoverage: "2014-01-01/2019-12-31",
      isPartOf: [{ "@id": "eli/bund/bgbl-1/2020/s1126/2020-08-04/1/deu" }],
      encoding: {
        contentUrl:
          "/v1/legislation/eli/bund/bgbl-1/2020/s1126/2020-08-04/1/deu#art-z1.html",
      },
    },
    {
      "@id": "eli/bund/bgbl-1/2020/s1126/2022-08-04/1/deu#art-z1",
      eId: "art-z1",
      temporalCoverage: "2020-01-01/2021-01-01",
      isPartOf: [{ "@id": "eli/bund/bgbl-1/2020/s1126/2022-08-04/1/deu" }],
      encoding: {
        contentUrl:
          "/v1/legislation/eli/bund/bgbl-1/2020/s1126/2022-08-04/1/deu#art-z1.html",
      },
    },
    {
      "@id": "eli/bund/bgbl-1/2020/s1126/2920-08-04/1/deu#art-z1",
      eId: "art-z1",
      temporalCoverage: "2021-01-02/..",
      isPartOf: [{ "@id": "eli/bund/bgbl-1/2020/s1126/2920-08-04/1/deu" }],
      encoding: {
        contentUrl:
          "/v1/legislation/eli/bund/bgbl-1/2020/s1126/2920-08-04/1/deu#art-z1.html",
      },
    },
  ],
};

export function useSingleNormVersions() {
  // TODO: fetch actual versions from backend
  return dummyResponse.member;
}
