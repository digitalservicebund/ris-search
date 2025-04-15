import Cookies from "js-cookie";

export default defineNuxtPlugin(() => {
  return {
    provide: {
      setCookieValue: (cookieName: string, value: string | boolean) =>
        Cookies.set(cookieName, value.toString(), {
          expires: 365,
        }),
    },
  };
});
