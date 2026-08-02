package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun AdRewardDialog(
    onDismiss: () -> Unit,
    onRewardEarned: () -> Unit
) {
    var isWatching by remember { mutableStateOf(false) }
    var countdown by remember { mutableIntStateOf(5) }

    LaunchedEffect(isWatching) {
        if (isWatching) {
            while (countdown > 0) {
                delay(1000L)
                countdown--
            }
            onRewardEarned()
            onDismiss()
        }
    }

    AlertDialog(
        onDismissRequest = { if (!isWatching) onDismiss() },
        containerColor = DarkSurface,
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CardGiftcard,
                    contentDescription = "Rewarded Ad",
                    tint = GoldAccent,
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = "AdMob Rewarded Video",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!isWatching) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(DarkSurfaceVariant)
                            .border(1.dp, CardBorder, RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.PlayCircleFilled,
                                contentDescription = "Play Video Ad",
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Watch 5s Sponsored Video",
                                style = MaterialTheme.typography.labelLarge.copy(color = TextPrimary)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = EmeraldPrimary.copy(alpha = 0.1f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🎁 UNLOCK REWARDS:",
                                fontWeight = FontWeight.Black,
                                color = EmeraldPrimary,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "• 2.0x Yield Speed Boost for 1 Hour\n• +$350.00 Direct Cash Bonus to Balance",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextPrimary,
                                    lineHeight = 18.sp
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            progress = { (5 - countdown) / 5f },
                            color = EmeraldPrimary,
                            trackColor = DarkSurfaceVariant,
                            strokeWidth = 6.dp,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Playing Sponsored Video... $countdown s",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Do not close. Rewarding yield bonus upon finish.",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (!isWatching) {
                Button(
                    onClick = { isWatching = true },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("watch_ad_confirm_btn")
                ) {
                    Text("Watch Ad Now", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            if (!isWatching) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        }
    )
}
