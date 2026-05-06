package com.k2bot.ai.chatbot.memory.support;

public class MessageWindowHelper {

    private MessageWindowHelper() {
    }

    /**
     * Returns the effective message window limit given a configured window size and a
     * per-call requested maximum.  Either value being zero or negative means "no limit
     * from that side".
     */
    public static int effectiveWindowSize(int configuredWindowSize, int requestedMaxMessages) {
        boolean configUnlimited = configuredWindowSize <= 0;
        boolean requestUnlimited = requestedMaxMessages <= 0;

        if (configUnlimited && requestUnlimited) {
            return Integer.MAX_VALUE;
        }
        if (configUnlimited) {
            return requestedMaxMessages;
        }
        if (requestUnlimited) {
            return configuredWindowSize;
        }
        return Math.min(configuredWindowSize, requestedMaxMessages);
    }
}
