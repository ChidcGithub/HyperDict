package com.hyperdict.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hyperdict.app.ui.theme.metro_blue
import com.hyperdict.app.ui.theme.metro_green
import com.hyperdict.app.ui.viewmodel.DictionaryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: DictionaryViewModel,
    onBack: () -> Unit
) {
    val settings = viewModel.settings

    Scaffold(
        topBar = {
            Surface(tonalElevation = 2.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, "Back")
                        }
                        Text(
                            text = "Settings",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // Search Settings Section
            SettingsSection(
                title = "Search",
                icon = Icons.Default.Search,
                color = metro_blue
            ) {
                SwitchSettingRow(
                    title = "Auto Search",
                    description = "Search automatically as you type",
                    checked = settings.autoSearch,
                    onCheckedChange = { settings.updateAutoSearch(it) }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                SearchHistoryLimitSetting(
                    currentLimit = settings.searchHistoryLimit,
                    onLimitChange = { settings.updateSearchHistoryLimit(it) }
                )
            }

            Spacer(Modifier.height(16.dp))

            // Display Settings Section
            SettingsSection(
                title = "Display",
                icon = Icons.Default.Palette,
                color = metro_green
            ) {
                SwitchSettingRow(
                    title = "Dark Theme",
                    description = "Use dark color scheme",
                    checked = settings.darkTheme,
                    onCheckedChange = { settings.updateDarkTheme(it) }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                SwitchSettingRow(
                    title = "Dynamic Color",
                    description = "Use Material You dynamic colors (Android 12+)",
                    checked = settings.dynamicColor,
                    onCheckedChange = { settings.updateDynamicColor(it) }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                FontSizeSetting(
                    currentScale = settings.fontSizeScale,
                    onScaleChange = { settings.updateFontSizeScale(it) }
                )
            }

            Spacer(Modifier.height(16.dp))

            // Dictionary Settings Section
            SettingsSection(
                title = "Dictionary",
                icon = Icons.Default.MenuBook,
                color = metro_blue
            ) {
                SwitchSettingRow(
                    title = "Prefer Offline",
                    description = "Use offline dictionary when available",
                    checked = settings.preferOffline,
                    onCheckedChange = { settings.updatePreferOffline(it) }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                SwitchSettingRow(
                    title = "Show UK Phonetic",
                    description = "Display British pronunciation phonetics",
                    checked = settings.showUkPhonetic,
                    onCheckedChange = { settings.updateShowUkPhonetic(it) }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                SwitchSettingRow(
                    title = "Show US Phonetic",
                    description = "Display American pronunciation phonetics",
                    checked = settings.showUsPhonetic,
                    onCheckedChange = { settings.updateShowUsPhonetic(it) }
                )
            }

            Spacer(Modifier.height(16.dp))

            // Network Settings Section
            SettingsSection(
                title = "Network",
                icon = Icons.Default.Wifi,
                color = metro_green
            ) {
                SwitchSettingRow(
                    title = "WiFi Only Download",
                    description = "Only download database over WiFi connection",
                    checked = settings.wifiOnlyDownload,
                    onCheckedChange = { settings.updateWifiOnlyDownload(it) }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                SettingActionRow(
                    title = "Clear Cache",
                    description = "Clear cached word definitions",
                    icon = Icons.Default.Delete,
                    onClick = { viewModel.clearCache() }
                )
            }

            Spacer(Modifier.height(16.dp))

            // About Section
            SettingsSection(
                title = "About",
                icon = Icons.Default.Info,
                color = metro_blue
            ) {
                SettingInfoRow(
                    title = "Version",
                    value = "0.0.1"
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                SettingInfoRow(
                    title = "Dictionary",
                    value = "ECDICT (3.77M+ words)"
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                SettingLinkRow(
                    title = "Open Source",
                    description = "View on GitHub",
                    icon = Icons.Default.OpenInNew
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    icon: ImageVector,
    color: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = MaterialTheme.shapes.small,
                    color = color.copy(alpha = 0.15f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun SwitchSettingRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun SettingActionRow(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SettingInfoRow(
    title: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SettingLinkRow(
    title: String,
    description: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SearchHistoryLimitSetting(
    currentLimit: Int,
    onLimitChange: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Search History Limit",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Maximum number of search history entries",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Text(
                text = "$currentLimit",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(20, 50, 100, 200).forEach { limit ->
                FilterChip(
                    onClick = { onLimitChange(limit) },
                    label = { Text("$limit") },
                    selected = currentLimit == limit,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun FontSizeSetting(
    currentScale: Float,
    onScaleChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Font Size",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Adjust text size (${(currentScale * 100).toInt()}%)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("A", style = MaterialTheme.typography.bodySmall)
            Slider(
                value = currentScale,
                onValueChange = onScaleChange,
                valueRange = 0.8f..1.5f,
                steps = 6,
                modifier = Modifier.weight(1f)
            )
            Text("A", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
