export const createMockStats = (totalLogs = 10, totalUsage = 500, totalCost = 1500) => ({
    totalLogs,
    totalUsage,
    totalCost,
});

// Static default payloads
export const emptyStatsPayload = {
    totalLogs: 0,
    totalUsage: 0,
    totalCost: 0,
};
