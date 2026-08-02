package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_account")
data class UserAccountEntity(
    @PrimaryKey val id: Int = 1,
    val cashBalance: Double = 2500.0,
    val totalEarnings: Double = 412.50,
    val passiveYieldPerSec: Double = 1.25,
    val subscriptionTier: String = "FREE", // "FREE", "PRO", "INSTITUTIONAL", "VIP_APEX"
    val adBoostActiveUntil: Long = 0L,
    val adBoostMultiplier: Double = 1.0,
    val adImpressionsCount: Int = 0,
    val adRevenueGenerated: Double = 0.0,
    val subscriptionAutoRenew: Boolean = true,
    val nextBillingDate: Long = System.currentTimeMillis() + (30L * 24 * 3600 * 1000),
    val maxCapitalLimit: Double = 50000.0,
    val autoRebalanceEnabled: Boolean = true,
    val riskTolerance: String = "BALANCED" // "CONSERVATIVE", "BALANCED", "AGGRESSIVE"
)

@Entity(tableName = "withdrawal_requests")
data class WithdrawalRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,
    val gateway: String, // "PAYPAL", "WISE", "STRIPE_WEBHOOK", "CRYPTO_WALLET", "BIZUM"
    val destinationAccount: String,
    val status: String = "PROCESSED", // "PENDING", "PROCESSING", "PROCESSED", "REJECTED"
    val webhookTxId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = ""
)

@Entity(tableName = "market_analyzer_bots")
data class MarketBotEntity(
    @PrimaryKey val id: String,
    val name: String,
    val targetMarket: String,
    val riskLevel: String,
    val accuracy: Double,
    val allocatedCapital: Double,
    val totalProfit: Double,
    val roi24h: Double,
    val isActive: Boolean,
    val strategyDescription: String,
    val lastAction: String
)

@Entity(tableName = "worker_bots")
data class WorkerBotEntity(
    @PrimaryKey val id: String,
    val name: String,
    val role: String,
    val level: Int,
    val upgradeCost: Double,
    val effectMultiplier: Double,
    val isActive: Boolean,
    val description: String
)

@Entity(tableName = "trade_logs")
data class TradeLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val botName: String,
    val assetSymbol: String,
    val tradeType: String, // "BUY", "SELL", "COMPOUND", "ARBITRAGE"
    val amount: Double,
    val profit: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val note: String
)
