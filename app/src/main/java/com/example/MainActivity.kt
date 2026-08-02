package com.example

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AdMobBanner
import com.example.ui.components.AdMobInterstitialDialog
import com.example.ui.components.AdRewardDialog
import com.example.ui.components.AdminWithdrawalDialog
import com.example.ui.components.PaymentCheckoutDialog
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.BankedViewModel

enum class NavigationScreen(val title: String, val iconSelected: ImageVector, val iconUnselected: ImageVector) {
    DASHBOARD("Dashboard", Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
    ANALYZER_BOTS("3 Analyzer Bots", Icons.Filled.Psychology, Icons.Outlined.Psychology),
    WORKER_BOTS("3 Worker Bots", Icons.Filled.Build, Icons.Outlined.Build),
    MONETIZATION("Pasarelas & Ads", Icons.Filled.MonetizationOn, Icons.Outlined.MonetizationOn),
    SIGNALS("Signals & Logs", Icons.Filled.ShowChart, Icons.Outlined.ShowChart)
}

class MainActivity : FragmentActivity() {

    private val viewModel: BankedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BankedTheme {
                BankedApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankedApp(viewModel: BankedViewModel) {
    var currentScreen by remember { mutableStateOf(NavigationScreen.DASHBOARD) }

    // Dialog state
    var showDepositDialog by remember { mutableStateOf(false) }
    var showSubscriptionDialog by remember { mutableStateOf(false) }
    var subscriptionTierSelected by remember { mutableStateOf("PRO") }
    var subscriptionPriceSelected by remember { mutableDoubleStateOf(9.99) }
    var showAdRewardDialog by remember { mutableStateOf(false) }
    var showInterstitialAdDialog by remember { mutableStateOf(false) }
    var showAdminWithdrawalDialog by remember { mutableStateOf(false) }

    val account by viewModel.userAccount.collectAsState()
    val withdrawalRequests by viewModel.withdrawalRequests.collectAsState()
    val uiMessage by viewModel.uiMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiMessage) {
        uiMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUiMessage()
        }
    }

    val currentAccount = account

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkBackground,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "IN PASSIVE BOT",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = TextPrimary,
                                letterSpacing = 2.sp
                            )
                        )
                        Box(
                            modifier = Modifier
                                .background(EmeraldPrimary, shape = MaterialTheme.shapes.extraSmall)
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "BOT INVESTOR",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                },
                actions = {
                    if (currentAccount != null) {
                        IconButton(
                            onClick = { showAdminWithdrawalDialog = true },
                            modifier = Modifier.testTag("topbar_admin_retiros_btn")
                        ) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = "Zona Admin Retiros", tint = IndigoBright)
                        }

                        TextButton(
                            onClick = { showDepositDialog = true },
                            modifier = Modifier.testTag("topbar_deposit_btn")
                        ) {
                            Icon(Icons.Default.CreditCard, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$${String.format("%.0f", currentAccount.cashBalance)}",
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Persistent AdMob Banner Bar
                if (currentAccount != null) {
                    AdMobBanner(
                        adImpressions = currentAccount.adImpressionsCount,
                        adRevenue = currentAccount.adRevenueGenerated,
                        onSimulateImpression = { viewModel.simulateAdImpression() }
                    )
                }

                NavigationBar(
                    containerColor = DarkSurface,
                    contentColor = TextSecondary,
                    tonalElevation = 8.dp
                ) {
                    NavigationScreen.values().forEach { screen ->
                        val isSelected = currentScreen == screen
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentScreen = screen },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) screen.iconSelected else screen.iconUnselected,
                                    contentDescription = screen.title
                                )
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = EmeraldPrimary,
                                selectedTextColor = EmeraldPrimary,
                                indicatorColor = EmeraldPrimary.copy(alpha = 0.15f),
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            ),
                            modifier = Modifier.testTag("nav_item_${screen.name}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
                when (screen) {
                    NavigationScreen.DASHBOARD -> DashboardScreen(
                        viewModel = viewModel,
                        onNavigateToBots = { currentScreen = NavigationScreen.ANALYZER_BOTS },
                        onNavigateToWorkers = { currentScreen = NavigationScreen.WORKER_BOTS },
                        onNavigateToMonetization = { currentScreen = NavigationScreen.MONETIZATION },
                        onOpenRewardAd = { showAdRewardDialog = true },
                        onOpenDeposit = { showDepositDialog = true }
                    )
                    NavigationScreen.ANALYZER_BOTS -> AnalyzerBotsScreen(viewModel = viewModel)
                    NavigationScreen.WORKER_BOTS -> WorkerBotsScreen(viewModel = viewModel)
                    NavigationScreen.MONETIZATION -> MonetizationScreen(
                        viewModel = viewModel,
                        onOpenDeposit = { showDepositDialog = true },
                        onOpenAdminWithdrawal = { showAdminWithdrawalDialog = true },
                        onOpenRewardAd = { showAdRewardDialog = true },
                        onOpenInterstitialAd = { showInterstitialAdDialog = true },
                        onOpenSubscriptionCheckout = { tier, price ->
                            subscriptionTierSelected = tier
                            subscriptionPriceSelected = price
                            showSubscriptionDialog = true
                        }
                    )
                    NavigationScreen.SIGNALS -> MarketSignalsScreen(viewModel = viewModel)
                }
            }
        }

        // Dialogs
        if (showDepositDialog) {
            PaymentCheckoutDialog(
                initialAmount = 500.0,
                isSubscriptionMode = false,
                onDismiss = { showDepositDialog = false },
                onPaymentConfirmed = { amount, method ->
                    viewModel.depositFunds(amount, method)
                }
            )
        }

        if (showSubscriptionDialog) {
            PaymentCheckoutDialog(
                initialAmount = subscriptionPriceSelected,
                isSubscriptionMode = true,
                subscriptionTierName = subscriptionTierSelected,
                onDismiss = { showSubscriptionDialog = false },
                onPaymentConfirmed = { _, _ ->
                    viewModel.subscribeTier(subscriptionTierSelected)
                }
            )
        }

        if (showAdRewardDialog) {
            AdRewardDialog(
                onDismiss = { showAdRewardDialog = false },
                onRewardEarned = { viewModel.activateAdBoost() }
            )
        }

        if (showInterstitialAdDialog) {
            AdMobInterstitialDialog(
                onDismiss = { showInterstitialAdDialog = false },
                onAdImpression = { rev -> viewModel.simulateAdImpression(rev) }
            )
        }

        if (showAdminWithdrawalDialog && currentAccount != null) {
            AdminWithdrawalDialog(
                cashBalance = currentAccount.cashBalance,
                withdrawalRequests = withdrawalRequests,
                onDismiss = { showAdminWithdrawalDialog = false },
                onSubmitWithdrawal = { amount, gateway, dest, note ->
                    viewModel.submitWithdrawalRequest(amount, gateway, dest, note)
                },
                onAdminUpdateStatus = { reqId, status, note ->
                    viewModel.adminUpdateWithdrawalStatus(reqId, status, note)
                }
            )
        }
    }
}
