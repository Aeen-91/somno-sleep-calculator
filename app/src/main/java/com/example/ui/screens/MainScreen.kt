package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.localization.AppLanguage
import com.example.data.localization.LocalAppLanguage
import com.example.data.localization.LocalizedStrings
import com.example.ui.components.AmbientLiquidBackground
import com.example.ui.theme.CelestialCyan
import com.example.ui.theme.CelestialIndigo
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.SleepViewModel

data class NavTab(
    val titleKey: String,
    val icon: ImageVector
)

@Composable
fun MainScreen(
    viewModel: SleepViewModel,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.appLanguage.collectAsState()
    val usePersianDigits by viewModel.usePersianDigits.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()

    val layoutDir = lang.layoutDirection

    CompositionLocalProvider(
        LocalAppLanguage provides lang,
        LocalLayoutDirection provides layoutDir
    ) {
        AmbientLiquidBackground(modifier = modifier.fillMaxSize()) {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    SomnoHeader(
                        lang = lang,
                        onToggleLang = {
                            val nextLang = if (lang == AppLanguage.ENGLISH) AppLanguage.PERSIAN else AppLanguage.ENGLISH
                            viewModel.setLanguage(nextLang)
                        }
                    )
                },
                bottomBar = {
                    SomnoBottomNav(
                        currentTab = currentTab,
                        lang = lang,
                        onTabSelected = { viewModel.setTab(it) }
                    )
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    AnimatedContent(
                        targetState = currentTab,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "tab_crossfade"
                    ) { tab ->
                        when (tab) {
                            0 -> CalculatorScreen(viewModel = viewModel, lang = lang, usePersianDigits = usePersianDigits)
                            1 -> CycleChartScreen(viewModel = viewModel, lang = lang, usePersianDigits = usePersianDigits)
                            2 -> TrackerScreen(viewModel = viewModel, lang = lang, usePersianDigits = usePersianDigits)
                            3 -> AICoachScreen(viewModel = viewModel, lang = lang, usePersianDigits = usePersianDigits)
                            4 -> SettingsScreen(viewModel = viewModel, lang = lang, usePersianDigits = usePersianDigits)
                            else -> CalculatorScreen(viewModel = viewModel, lang = lang, usePersianDigits = usePersianDigits)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SomnoHeader(
    lang: AppLanguage,
    onToggleLang: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Title Branding
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(CelestialIndigo.copy(alpha = 0.6f), CelestialCyan.copy(alpha = 0.6f)))
                    )
                    .border(1.dp, CelestialCyan.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Bedtime,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = LocalizedStrings.get("app_title", lang),
                    fontSize = 19.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = LocalizedStrings.get("app_tagline", lang),
                    fontSize = 10.sp,
                    color = TextSecondary,
                    maxLines = 1
                )
            }
        }

        // Quick Language Toggle Pill
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0x25FFFFFF))
                .border(1.dp, GlassBorder, RoundedCornerShape(14.dp))
                .clickable(onClick = onToggleLang)
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .testTag("quick_lang_toggle"),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Translate,
                    contentDescription = "Language",
                    tint = CelestialCyan,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = if (lang == AppLanguage.ENGLISH) "فارسی" else "EN",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }
    }
}

@Composable
private fun SomnoBottomNav(
    currentTab: Int,
    lang: AppLanguage,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        NavTab("tab_calculator", Icons.Default.Calculate),
        NavTab("tab_chart", Icons.AutoMirrored.Filled.ShowChart),
        NavTab("tab_tracker", Icons.Default.Timer),
        NavTab("tab_coach", Icons.Default.Psychology),
        NavTab("tab_settings", Icons.Default.Settings)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(Color(0xD00A0F1D))
                .border(1.dp, GlassBorder, RoundedCornerShape(26.dp))
                .padding(horizontal = 6.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                tabs.forEachIndexed { index, tab ->
                    val isSelected = currentTab == index

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .then(
                                if (isSelected) {
                                    Modifier
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(CelestialIndigo.copy(alpha = 0.5f), CelestialCyan.copy(alpha = 0.5f))
                                            )
                                        )
                                        .border(1.dp, CelestialCyan.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
                                } else Modifier
                            )
                            .clickable { onTabSelected(index) }
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                            .testTag("nav_tab_$index"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = LocalizedStrings.get(tab.titleKey, lang),
                                tint = if (isSelected) Color.White else TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = LocalizedStrings.get(tab.titleKey, lang),
                                fontSize = 9.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) TextPrimary else TextMuted,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
