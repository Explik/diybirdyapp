// Based on https://gist.github.com/fostyfost/35cd018f4de60da916993b22fb729aba
export type RecursivePartial<T> = {
    [P in keyof T]?: T[P] extends (infer U)[]
      ? RecursivePartial<U>[]
      : T[P] extends (...args: any) => any
      ? T[P] | undefined
      : T[P] extends object
      ? RecursivePartial<T[P]>
      : T[P]
  }