import { HttpStatusCode } from "axios";
import type { H3Event } from "h3";
import { describe, it, expect, vi, beforeEach } from "vitest";
import { requireAccessTokenWithRefresh } from "./auth";

interface CustomNodeJsGlobal extends NodeJS.Global {
  getUserSession: () => Promise<unknown>;
}
declare const global: CustomNodeJsGlobal;

const mockSession = {
  secure: {
    tokens: {
      accessToken: "valid-token",
      refreshToken: "valid-refresh-token",
    },
  },
  expiresAt: Date.now() + 10000,
};

const mockFetch = vi.fn();

vi.stubGlobal("$fetch", mockFetch);
vi.stubGlobal("setUserSession", vi.fn());

describe("requireAccessTokenWithRefresh", () => {
  const mockEvent = {} as H3Event;

  beforeEach(() => {
    vi.resetAllMocks();
    global.getUserSession = vi.fn().mockResolvedValue(mockSession);
  });

  it("returns access token when session is valid and not expiring soon", async () => {
    const mockSession = {
      secure: {
        tokens: {
          accessToken: "valid-access-token",
        },
      },
      expiresAt: Date.now() + 10000, // expires in 10 seconds
    };

    vi.mocked(global.getUserSession).mockResolvedValue(mockSession);

    const result = await requireAccessTokenWithRefresh(mockEvent);

    expect(result).toBe("valid-access-token");
    expect(global.getUserSession).toHaveBeenCalledWith(mockEvent);
  });

  it("refreshes token when session expires soon", async () => {
    const mockSession = {
      secure: {
        tokens: {
          accessToken: "old-access-token",
          refreshToken: "valid-refresh-token",
        },
      },
      expiresAt: Date.now() + 3000, // expires in 3 seconds (less than 5000ms threshold)
    };

    const mockRefreshTokenResponse = {
      expires_in: 60000,
      access_token: "new-access-token",
      refresh_token: "new-refresh-token",
    };

    vi.mocked(global.getUserSession).mockResolvedValue(mockSession);
    mockFetch.mockResolvedValueOnce(mockRefreshTokenResponse);

    const result = await requireAccessTokenWithRefresh(mockEvent);

    expect(result).toBe("new-access-token");
    expect(global.getUserSession).toHaveBeenCalledWith(mockEvent);
  });

  it("throws unauthorized error when no access token exists", async () => {
    const mockSession = {
      secure: {
        tokens: {},
      },
      expiresAt: Date.now() + 10000,
    };

    vi.mocked(global.getUserSession).mockResolvedValue(mockSession);

    await expect(
      requireAccessTokenWithRefresh(mockEvent),
    ).rejects.toMatchObject({
      statusCode: HttpStatusCode.Unauthorized,
      statusMessage: "Unauthorized",
      cause: "no access token",
    });

    expect(global.getUserSession).toHaveBeenCalledWith(mockEvent);
  });

  it("throws error when refresh fails", async () => {
    const mockSession = {
      secure: {
        tokens: {
          accessToken: "old-access-token",
        },
      },
      expiresAt: Date.now() + 3000, // expires soon
    };

    vi.mocked(global.getUserSession).mockResolvedValue(mockSession);
    mockFetch.mockRejectedValueOnce({});

    await expect(requireAccessTokenWithRefresh(mockEvent)).rejects.toThrow(
      "Unauthorized",
    );

    expect(global.getUserSession).toHaveBeenCalledWith(mockEvent);
  });
});
