/** A utility type that requires an array of T to have at least one element. */
export type OneOrMore<T> = [T, ...T[]];
