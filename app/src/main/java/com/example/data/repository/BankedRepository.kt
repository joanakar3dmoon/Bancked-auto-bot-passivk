package com.example.data.repository

import com.example.data.db.*
import kotlinx.coroutines.flow.Flow

class BankedRepository(private val dao: BankedDao) {

    val userAccount: Flow<UserAccountEntity?> = dao.getUserAccount()
    val marketBots: Flow<List<MarketBotEntity>> = dao.getMarketBots()
    val workerBots: Flow<List<WorkerBotEntity>> = dao.getWorkerBots()
    val tradeLogs: Flow<List<TradeLogEntity>> = dao.getTradeLogs()
    val withdrawalRequests: Flow<List<WithdrawalRequestEntity>> = dao.getWithdrawalRequests()

    suspend fun initializeDefaultDataIfNeeded() {
        val account = dao.getUserAccountDirect()
        if (account == null) {
            dao.insertOrUpdateAccount(
                UserAccountEntity(
                    id = 1,
                    cashBalance = 2500.0,
                    totalEarnings = 412.50,
                    passiveYieldPerSec = 1.45,
                    subscriptionTier = "FREE"
                )
            )
        }

        // Initialize 3 Market Analyzer Bots
        val existingMarketBots = dao.getMarketBotById("BOT_ALPHA_CRYPTO")
        if (existingMarketBots == null) {
            val defaultMarketBots = listOf(
                MarketBotEntity(
                    id = "BOT_ALPHA_CRYPTO",
                    name = "Alpha Vision AI",
                    targetMarket = "Crypto & Web3",
                    riskLevel = "High Volatility",
                    accuracy = 91.2,
                    allocatedCapital = 1200.0,
                    totalProfit = 284.50,
                    roi24h = 14.8,
                    isActive = true,
                    strategyDescription = "AI momentum scanner analyzing DEX liquidity pools and high-volume breakout patterns on SOL, ETH & BTC.",
                    lastAction = "BUY 0.45 ETH @ $3,420 (Executed +$34.20)"
                ),
                MarketBotEntity(
                    id = "BOT_QUANTUM_TECH",
                    name = "Quantum Pulse Tech",
                    targetMarket = "US Tech Equities",
                    riskLevel = "Moderate Growth",
                    accuracy = 86.5,
                    allocatedCapital = 2500.0,
                    totalProfit = 192.10,
                    roi24h = 6.4,
                    isActive = true,
                    strategyDescription = "Algorithmic market sentiment parser trading high-beta S&P 500 tech leaders (NVDA, AAPL, MSFT).",
                    lastAction = "SWING LONG NVDA @ $122.50 (Profit taken +$42.00)"
                ),
                MarketBotEntity(
                    id = "BOT_FOREX_SENTINEL",
                    name = "Forex Sentinel Arbitrage",
                    targetMarket = "Global FX Pairs",
                    riskLevel = "Ultra-Low Risk",
                    accuracy = 97.1,
                    allocatedCapital = 3000.0,
                    totalProfit = 85.90,
                    roi24h = 3.2,
                    isActive = true,
                    strategyDescription = "High-frequency triangular arbitrage exploiting millisecond pricing spreads between EUR/USD & GBP/JPY.",
                    lastAction = "ARBITRAGE EXECUTED EUR/USD (Captured spread +$12.40)"
                )
            )
            dao.insertOrUpdateMarketBots(defaultMarketBots)
        }

        // Initialize 3 Worker Upgrade Bots
        val existingWorker = dao.getWorkerBotById("WORKER_COMPOUNDER")
        if (existingWorker == null) {
            val defaultWorkerBots = listOf(
                WorkerBotEntity(
                    id = "WORKER_COMPOUNDER",
                    name = "Compound Engine Bot",
                    role = "Auto-Reinvestment & Yield Compounder",
                    level = 1,
                    upgradeCost = 150.0,
                    effectMultiplier = 1.15,
                    isActive = true,
                    description = "Automatically reinvests all passive dividends back into market bots every cycle to accelerate profit compounding."
                ),
                WorkerBotEntity(
                    id = "WORKER_RISK_SHIELD",
                    name = "Risk Shield Guardian Bot",
                    role = "Capital Drawdown Protection",
                    level = 1,
                    upgradeCost = 220.0,
                    effectMultiplier = 1.20,
                    isActive = true,
                    description = "Dynamic algorithmic stop-loss engine that hedges positions during flash crashes, cutting drawdowns by up to 85%."
                ),
                WorkerBotEntity(
                    id = "WORKER_YIELD_MULTI",
                    name = "Yield Multiplier Bot",
                    role = "Fee Arbitrage & Yield Amplifier",
                    level = 1,
                    upgradeCost = 350.0,
                    effectMultiplier = 1.25,
                    isActive = true,
                    description = "Harvests liquidity protocol rewards and eliminates trading fee spreads, boosting net ROI output."
                )
            )
            dao.insertOrUpdateWorkerBots(defaultWorkerBots)
        }
    }

    suspend fun updateAccount(account: UserAccountEntity) {
        dao.insertOrUpdateAccount(account)
    }

    suspend fun updateMarketBot(bot: MarketBotEntity) {
        dao.updateMarketBot(bot)
    }

    suspend fun updateWorkerBot(worker: WorkerBotEntity) {
        dao.updateWorkerBot(worker)
    }

    suspend fun addTradeLog(log: TradeLogEntity) {
        dao.insertTradeLog(log)
    }

    suspend fun clearLogs() {
        dao.clearTradeLogs()
    }

    suspend fun addWithdrawalRequest(request: WithdrawalRequestEntity) {
        dao.insertWithdrawalRequest(request)
    }

    suspend fun updateWithdrawalRequest(request: WithdrawalRequestEntity) {
        dao.updateWithdrawalRequest(request)
    }
}
