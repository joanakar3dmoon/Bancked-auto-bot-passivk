package com.example.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BankedDao {

    // User Account
    @Query("SELECT * FROM user_account WHERE id = 1")
    fun getUserAccount(): Flow<UserAccountEntity?>

    @Query("SELECT * FROM user_account WHERE id = 1")
    suspend fun getUserAccountDirect(): UserAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAccount(account: UserAccountEntity)

    // Market Analyzer Bots (3 bots)
    @Query("SELECT * FROM market_analyzer_bots")
    fun getMarketBots(): Flow<List<MarketBotEntity>>

    @Query("SELECT * FROM market_analyzer_bots WHERE id = :botId")
    suspend fun getMarketBotById(botId: String): MarketBotEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateMarketBots(bots: List<MarketBotEntity>)

    @Update
    suspend fun updateMarketBot(bot: MarketBotEntity)

    // Worker Upgrade Bots (3 bots)
    @Query("SELECT * FROM worker_bots")
    fun getWorkerBots(): Flow<List<WorkerBotEntity>>

    @Query("SELECT * FROM worker_bots WHERE id = :workerId")
    suspend fun getWorkerBotById(workerId: String): WorkerBotEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateWorkerBots(workers: List<WorkerBotEntity>)

    @Update
    suspend fun updateWorkerBot(worker: WorkerBotEntity)

    // Trade Logs
    @Query("SELECT * FROM trade_logs ORDER BY timestamp DESC LIMIT 50")
    fun getTradeLogs(): Flow<List<TradeLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTradeLog(log: TradeLogEntity)

    @Query("DELETE FROM trade_logs")
    suspend fun clearTradeLogs()

    // Withdrawal Requests (Admin & User)
    @Query("SELECT * FROM withdrawal_requests ORDER BY timestamp DESC")
    fun getWithdrawalRequests(): Flow<List<WithdrawalRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWithdrawalRequest(request: WithdrawalRequestEntity)

    @Update
    suspend fun updateWithdrawalRequest(request: WithdrawalRequestEntity)
}
