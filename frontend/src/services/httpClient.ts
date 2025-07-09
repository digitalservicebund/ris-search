import axios, { HttpStatusCode } from "axios";
import type { AxiosError } from "axios";

export const axiosInstance = axios.create();

axiosInstance.interceptors.response.use(
  (response) => {
    if (!response.data && response?.status === HttpStatusCode.Ok) {
      throw new Error("Leere Antwort");
    }
    if (
      response.data &&
      typeof response.data === "object" &&
      "errors" in response.data
    ) {
      const errorMessage = (response.data.errors as Error[])
        .map((e) => e.message)
        .join(", ");
      throw new Error(errorMessage);
    }
    return response;
  },
  async (error: AxiosError) => {
    if (error.code === "ECONNABORTED") {
      return Promise.reject(new Error("Zeitüberschreitung der Anfrage"));
    }
    switch (error.response?.status) {
      case HttpStatusCode.GatewayTimeout:
        return Promise.reject(new Error("Zeitüberschreitung der Anfrage"));
      case HttpStatusCode.InternalServerError:
        return Promise.reject(new Error("Interner Serverfehler"));
    }
    throw error.response;
  },
);

export default axiosInstance;
