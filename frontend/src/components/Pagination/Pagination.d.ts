import type { AnyDocument, SearchResult } from "~/types";

export interface PartialCollectionView {
  first?: string;
  previous?: string;
  next?: string;
  last?: string;
}

export type Page = {
  member: SearchResult<AnyDocument>[];
  "@id": string;
  totalItems?: number;
  view: PartialCollectionView;
};
