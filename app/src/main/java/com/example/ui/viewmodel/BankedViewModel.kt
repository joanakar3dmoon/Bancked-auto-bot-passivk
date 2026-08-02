package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.*
import com.example.data.repository.BankedRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

class BankedViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = BankedRepository(db.bankedDao())

    val userAccount: StateFlow<UserAccountEntity?> = repository.userAccount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val marketBots: StateFlow<List<MarketBotEntity>> = repository.marketBots.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val workerBots: StateFlow<List<WorkerBotEntity>> = repository.workerBots.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val tradeLogs: StateFlow<List<TradeLogEntity>> = repository.tradeLogs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val withdrawalRequests: StateFlow<List<WithdrawalRequestEntity>> = repository.withdrawalRequests.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // UI Feedback state (Snackbar / Toast dialogs)
    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeDefaultDataIfNeeded()
            startTickerLoop()
        }
    }

    fun clearUiMessage() {
        _uiMessage.value = null
    }

    fun setUiMessage(msg: String) {
        _uiMessage.value = msg
    }

    private fun startTickerLoop() {
        viewModelScope.launch {
            while (true) {
                delay(1500L) // Tick every 1.5 seconds

                val account = userAccount.value ?: continue
                val activeMarketBots = marketBots.value.filter { it.isActive }
                val activeWorkerBots = workerBots.value.filter { it.isActive }

                if (activeMarketBots.isEmpty()) continue

                // Base rate from active Market Bots
                var baseYieldPerSec = 0.0
                activeMarketBots.forEach { bot ->
                    // Higher allocated capital and accuracy yields more
                    val botRate = (bot.allocatedCapital * 0.0004) * (bot.accuracy / 100.0)
                    baseYieldPerSec += botRate
                }

                // Apply Worker Bot Multipliers
                var workerMultiplier = 1.0
                activeWorkerBots.forEach { worker ->
                    // Level 1 = 1.15, Level 2 = 1.30, etc.
                    val boost = 1.0 + ((worker.effectMultiplier - 1.0) * worker.level)
                    workerMultiplier *= boost
                }

                // Check Subscription Tier Perks
                val subMultiplier = when (account.subscriptionTier) {
                    "PRO" -> 1.5
                    "INSTITUTIONAL" -> 2.5
                    else -> 1.0
                }

                // Check Ad Boost
                val isAdBoostActive = account.adBoostActiveUntil > System.currentTimeMillis()
                val currentAdMultiplier = if (isAdBoostActive) account.adBoostMultiplier else 1.0

                val finalYieldPerTick = (baseYieldPerSec * workerMultiplier * subMultiplier * currentAdMultiplier) * 1.5

                val updatedBalance = account.cashBalance + finalYieldPerTick
                val updatedEarnings = account.totalEarnings + finalYieldPerTick

                repository.updateAccount(
                    account.copy(
                        cashBalance = updatedBalance,
                        totalEarnings = updatedEarnings,
                        passiveYieldPerSec = baseYieldPerSec * workerMultiplier * subMultiplier * currentAdMultiplier
                    )
                )

                // Randomly trigger bot trading log event (15% chance per tick)
                if (Random.nextFloat() < 0.15f) {
                    val randomBot = activeMarketBots.random()
                    val profitMade = Random.nextDouble(5.0, 45.0) * (randomBot.accuracy / 90.0)
                    val assets = listOf("SOL/USDT", "NVDA", "BTC/USD", "EUR/USD", "AAPL", "GBP/JPY")
                    val chosenAsset = assets.random()
                    val actions = listOf("AI BREAKOUT BUY", "PROFIT TAKE", "TRIANGULAR ARBITRAGE", "REBALANCE")
                    val chosenAction = actions.random()

                    val newTotalProfit = randomBot.totalProfit + profitMade
                    val updatedBot = randomBot.copy(
                        totalProfit = newTotalProfit,
                        lastAction = "$chosenAction $chosenAsset (+$${String.format("%.2f", profitMade)})"
                    )
                    repository.updateMarketBot(updatedBot)

                    repository.addTradeLog(
                        TradeLogEntity(
                            botName = randomBot.name,
                            assetSymbol = chosenAsset,
                            tradeType = chosenAction,
                            amount = randomBot.allocatedCapital * 0.1,
                            profit = profitMade,
                            note = "Auto-executed by ${randomBot.name}"
                        )
                    )
                }
            }
        }
    }

    fun toggleMarketBot(botId: String) {
        viewModelScope.launch {
            val bot = marketBots.value.find { it.id == botId } ?: return@launch
            val updated = bot.copy(isActive = !bot.isActive)
            repository.updateMarketBot(updated)
            _uiMessage.value = if (updated.isActive) "Bot ${bot.name} Activated" else "Bot ${bot.name} Paused"
        }
    }

    fun toggleWorkerBot(workerId: String) {
        viewModelScope.launch {
            val worker = workerBots.value.find { it.id == workerId } ?: return@launch
            val updated = worker.copy(isActive = !worker.isActive)
            repository.updateWorkerBot(updated)
            _uiMessage.value = if (updated.isActive) "${worker.name} Online" else "${worker.name} Offline"
        }
    }

    fun upgradeWorkerBot(workerId: String) {
        viewModelScope.launch {
            val account = userAccount.value ?: return@launch
            val worker = workerBots.value.find { it.id == workerId } ?: return@launch

            if (account.cashBalance < worker.upgradeCost) {
                _uiMessage.value = "Insufficient balance to upgrade ${worker.name}. Need $${String.format("%.2f", worker.upgradeCost)}"
                return@launch
            }

            val newBalance = account.cashBalance - worker.upgradeCost
            val newLevel = worker.level + 1
            val nextUpgradeCost = worker.upgradeCost * 1.85

            repository.updateAccount(account.copy(cashBalance = newBalance))
            repository.updateWorkerBot(
                worker.copy(
                    level = newLevel,
                    upgradeCost = nextUpgradeCost
                )
            )

            repository.addTradeLog(
                TradeLogEntity(
                    botName = worker.name,
                    assetSymbol = "WORKER_UPGRADE",
                    tradeType = "UPGRADE",
                    amount = worker.upgradeCost,
                    profit = 0.0,
                    note = "Upgraded to Level $newLevel! Efficiency boosted."
                )
            )

            _uiMessage.value = "🎉 Upgraded ${worker.name} to Level $newLevel!"
        }
    }

    fun depositFunds(amount: Double, paymentMethod: String) {
        viewModelScope.launch {
            val account = userAccount.value ?: return@launch
            val newBalance = account.cashBalance + amount
            repository.updateAccount(account.copy(cashBalance = newBalance))

            repository.addTradeLog(
                TradeLogEntity(
                    botName = "PAYMENT GATEWAY",
                    assetSymbol = paymentMethod,
                    tradeType = "DEPOSIT",
                    amount = amount,
                    profit = 0.0,
                    note = "Deposit via $paymentMethod confirmed."
                )
            )

            _uiMessage.value = "Successfully deposited $${String.format("%.2f", amount)} via $paymentMethod!"
        }
    }

    fun withdrawFunds(amount: Double, paymentMethod: String) {
        viewModelScope.launch {
            val account = userAccount.value ?: return@launch
            if (account.cashBalance < amount) {
                _uiMessage.value = "Insufficient funds for withdrawal."
                return@launch
            }
            val newBalance = account.cashBalance - amount
            repository.updateAccount(account.copy(cashBalance = newBalance))

            repository.addTradeLog(
                TradeLogEntity(
                    botName = "PAYMENT GATEWAY",
                    assetSymbol = paymentMethod,
                    tradeType = "WITHDRAW",
                    amount = amount,
                    profit = 0.0,
                    note = "Payout sent to $paymentMethod."
                )
            )

            _uiMessage.value = "Withdrawn $${String.format("%.2f", amount)} to $paymentMethod."
        }
    }

    fun activateAdBoost() {
        viewModelScope.launch {
            val account = userAccount.value ?: return@launch
            val newBoostUntil = System.currentTimeMillis() + (3600 * 1000L) // 1 Hour boost
            val bonusCash = 350.0
            val newImpressions = account.adImpressionsCount + 1
            val adRev = account.adRevenueGenerated + 0.65

            repository.updateAccount(
                account.copy(
                    cashBalance = account.cashBalance + bonusCash,
                    adBoostActiveUntil = newBoostUntil,
                    adBoostMultiplier = 2.0,
                    adImpressionsCount = newImpressions,
                    adRevenueGenerated = adRev
                )
            )

            _uiMessage.value = "🚀 Ad Reward Claimed! 2x Yield Boost active for 1hr + $350.00 cash bonus!"
        }
    }

    fun simulateAdImpression(customRev: Double = 0.35) {
        viewModelScope.launch {
            val account = userAccount.value ?: return@launch
            val newImpressions = account.adImpressionsCount + 1
            val adRev = account.adRevenueGenerated + customRev
            val tinyReward = 25.0

            repository.updateAccount(
                account.copy(
                    cashBalance = account.cashBalance + tinyReward,
                    adImpressionsCount = newImpressions,
                    adRevenueGenerated = adRev
                )
            )

            _uiMessage.value = "📺 Ad Impression Monetized: +$${String.format("%.2f", customRev)} App Ad Revenue (+$25 User Bonus)"
        }
    }

    fun subscribeTier(tierName: String) {
        viewModelScope.launch {
            val account = userAccount.value ?: return@launch
            val price = when (tierName) {
                "PRO" -> 9.99
                "INSTITUTIONAL" -> 29.99
                "VIP_APEX" -> 99.99
                else -> 0.0
            }

            val maxCap = when (tierName) {
                "PRO" -> 25000.0
                "INSTITUTIONAL" -> 100000.0
                "VIP_APEX" -> 1000000.0
                else -> 5000.0
            }

            repository.updateAccount(
                account.copy(
                    subscriptionTier = tierName,
                    maxCapitalLimit = maxCap,
                    subscriptionAutoRenew = true,
                    nextBillingDate = System.currentTimeMillis() + (30L * 24 * 3600 * 1000)
                )
            )

            repository.addTradeLog(
                TradeLogEntity(
                    botName = "BILLING RECURRING ENGINE",
                    assetSymbol = tierName,
                    tradeType = "SUBSCRIPTION_PAY",
                    amount = price,
                    profit = 0.0,
                    note = "Subscription initialized for $tierName ($$price/mo). Auto-renewal active."
                )
            )

            _uiMessage.value = "⭐ Subscription upgraded to $tierName! Exclusive tier perks activated."
        }
    }

    fun toggleSubscriptionAutoRenew() {
        viewModelScope.launch {
            val account = userAccount.value ?: return@launch
            val nextState = !account.subscriptionAutoRenew
            repository.updateAccount(account.copy(subscriptionAutoRenew = nextState))
            _uiMessage.value = if (nextState) "Auto-Renewal Activated for ${account.subscriptionTier}" else "Auto-Renewal Paused for ${account.subscriptionTier}"
        }
    }

    fun processRecurringBillingCycle() {
        viewModelScope.launch {
            val account = userAccount.value ?: return@launch
            if (account.subscriptionTier == "FREE") {
                _uiMessage.value = "Current plan is FREE. No recurring charges."
                return@launch
            }

            val fee = when (account.subscriptionTier) {
                "PRO" -> 9.99
                "INSTITUTIONAL" -> 29.99
                "VIP_APEX" -> 99.99
                else -> 0.0
            }

            if (account.cashBalance < fee) {
                repository.updateAccount(
                    account.copy(
                        subscriptionTier = "FREE",
                        maxCapitalLimit = 5000.0
                    )
                )
                _uiMessage.value = "⚠️ Recurring payment of $$fee failed due to low balance. Subscription reverted to FREE."
            } else {
                val newBal = account.cashBalance - fee
                val newNextBilling = System.currentTimeMillis() + (30L * 24 * 3600 * 1000)
                repository.updateAccount(
                    account.copy(
                        cashBalance = newBal,
                        nextBillingDate = newNextBilling
                    )
                )
                repository.addTradeLog(
                    TradeLogEntity(
                        botName = "STRIPE RECURRING ENGINE",
                        assetSymbol = account.subscriptionTier,
                        tradeType = "RECURRING_CHARGE",
                        amount = fee,
                        profit = 0.0,
                        note = "Successfully processed recurring monthly payment of $$fee."
                    )
                )
                _uiMessage.value = "✅ Recurring payment of $$fee processed for ${account.subscriptionTier}. Next billing date extended by 30 days."
            }
        }
    }

    // --- ZONA ADMIN RETIROS & REAL PAYMENT GATEWAYS ---
    fun submitWithdrawalRequest(
        amount: Double,
        gateway: String, // "PAYPAL", "WISE", "STRIPE_WEBHOOK", "CRYPTO_WALLET", "BIZUM"
        destinationAccount: String,
        note: String
    ) {
        viewModelScope.launch {
            val account = userAccount.value ?: return@launch
            if (amount <= 0.0) {
                _uiMessage.value = "Invalid withdrawal amount."
                return@launch
            }
            if (account.cashBalance < amount) {
                _uiMessage.value = "Insufficient funds. Available: $${String.format("%.2f", account.cashBalance)}"
                return@launch
            }

            val txPrefix = when (gateway) {
                "PAYPAL" -> "PP-PAYOUT-"
                "WISE" -> "WISE-FX-"
                "STRIPE_WEBHOOK" -> "whsec_strp_"
                "CRYPTO_WALLET" -> "0x7a"
                "BIZUM" -> "BIZ-"
                else -> "TX-"
            }
            val generatedTxId = txPrefix + Random.nextInt(100000, 999999).toString(16).uppercase()

            val newBalance = account.cashBalance - amount
            repository.updateAccount(account.copy(cashBalance = newBalance))

            val request = WithdrawalRequestEntity(
                amount = amount,
                gateway = gateway,
                destinationAccount = destinationAccount,
                status = "PROCESSED", // Instant webhook payout confirmed
                webhookTxId = generatedTxId,
                timestamp = System.currentTimeMillis(),
                note = if (note.isBlank()) "Gateway Payout via $gateway to $destinationAccount" else note
            )

            repository.addWithdrawalRequest(request)

            repository.addTradeLog(
                TradeLogEntity(
                    botName = "GATEWAY_$gateway",
                    assetSymbol = gateway,
                    tradeType = "WITHDRAW",
                    amount = amount,
                    profit = 0.0,
                    note = "Payout sent to $destinationAccount ($generatedTxId)"
                )
            )

            _uiMessage.value = "✅ Withdrawal of $${String.format("%.2f", amount)} via $gateway processed! Tx: $generatedTxId"
        }
    }

    fun adminUpdateWithdrawalStatus(requestId: Int, newStatus: String, webhookNote: String) {
        viewModelScope.launch {
            val reqList = withdrawalRequests.value
            val target = reqList.find { it.id == requestId } ?: return@launch
            val updated = target.copy(status = newStatus, note = "${target.note} | Admin Webhook: $webhookNote")
            repository.updateWithdrawalRequest(updated)

            _uiMessage.value = "🛠️ Admin Webhook Event Triggered: Withdrawal #${target.id} updated to $newStatus"
        }
    }

    // --- WORKER BOTS OPTIMIZATION & AUTO-REBALANCE ENGINE ---
    fun executeAutoRebalanceStrategy(strategy: String) { // "PROFIT_MAXIMIZER", "CAPITAL_PROTECTION", "EQUAL_WEIGHT"
        viewModelScope.launch {
            val currentBots = marketBots.value
            if (currentBots.size < 3) return@launch

            val totalCap = currentBots.sumOf { it.allocatedCapital }
            val updatedBots = when (strategy) {
                "PROFIT_MAXIMIZER" -> {
                    // 60% Alpha Crypto, 30% Tech Equities, 10% Forex
                    currentBots.map { bot ->
                        val newCap = when (bot.id) {
                            "BOT_ALPHA_CRYPTO" -> totalCap * 0.60
                            "BOT_QUANTUM_TECH" -> totalCap * 0.30
                            else -> totalCap * 0.10
                        }
                        bot.copy(allocatedCapital = newCap, lastAction = "AUTO-REBALANCED (Alpha Maximizer 60/30/10)")
                    }
                }
                "CAPITAL_PROTECTION" -> {
                    // 60% Ultra-Low Forex Sentinel, 25% Tech Equities, 15% Crypto
                    currentBots.map { bot ->
                        val newCap = when (bot.id) {
                            "BOT_FOREX_SENTINEL" -> totalCap * 0.60
                            "BOT_QUANTUM_TECH" -> totalCap * 0.25
                            else -> totalCap * 0.15
                        }
                        bot.copy(allocatedCapital = newCap, lastAction = "RISK SHIELD HEDGE (Drawdown Shield 60/25/15)")
                    }
                }
                else -> { // EQUAL_WEIGHT
                    val equalCap = totalCap / currentBots.size
                    currentBots.map {
                        it.copy(allocatedCapital = equalCap, lastAction = "AUTO-REBALANCED (Equal Weight 33/33/33)")
                    }
                }
            }

            updatedBots.forEach { repository.updateMarketBot(it) }

            val account = userAccount.value
            if (account != null) {
                repository.updateAccount(account.copy(riskTolerance = strategy))
            }

            repository.addTradeLog(
                TradeLogEntity(
                    botName = "WORKER_RISK_SHIELD",
                    assetSymbol = strategy,
                    tradeType = "PORTFOLIO_REBALANCE",
                    amount = totalCap,
                    profit = 0.0,
                    note = "Executed AI Portfolio Rebalance strategy: $strategy across 3 market bots."
                )
            )

            _uiMessage.value = "⚡ Worker Bots executed portfolio rebalancing strategy: $strategy!"
        }
    }

    fun compoundWorkerProfits() {
        viewModelScope.launch {
            val account = userAccount.value ?: return@launch
            val currentBots = marketBots.value
            val compoundAmount = account.totalEarnings * 0.15 // Reinvest 15% of total earnings into bot capital

            if (compoundAmount <= 0) {
                _uiMessage.value = "No earnings to compound yet."
                return@launch
            }

            val capPerBot = compoundAmount / currentBots.size
            currentBots.forEach { bot ->
                val updatedBot = bot.copy(
                    allocatedCapital = bot.allocatedCapital + capPerBot,
                    lastAction = "WORKER REINVESTMENT (+${String.format("%.2f", capPerBot)})"
                )
                repository.updateMarketBot(updatedBot)
            }

            repository.addTradeLog(
                TradeLogEntity(
                    botName = "WORKER_COMPOUNDER",
                    assetSymbol = "YIELD_REINVEST",
                    tradeType = "COMPOUND",
                    amount = compoundAmount,
                    profit = compoundAmount * 0.08,
                    note = "Reinvested $${String.format("%.2f", compoundAmount)} dividends directly into Market Bot capital bases."
                )
            )

            _uiMessage.value = "📈 Compound Engine Bot reinvested $${String.format("%.2f", compoundAmount)} into bot capital!"
        }
    }

    fun claimQuickYield() {
        viewModelScope.launch {
            val account = userAccount.value ?: return@launch
            val quickBonus = account.passiveYieldPerSec * 60.0 // 1 minute instant yield
            repository.updateAccount(account.copy(cashBalance = account.cashBalance + quickBonus))

            _uiMessage.value = "⚡ Fast-Forward Harvest: Claimed +$${String.format("%.2f", quickBonus)}!"
        }
    }

    fun clearLogs() {
        viewModelScope.launch {
            repository.clearLogs()
            _uiMessage.value = "Execution logs cleared."
        }
    }
}
