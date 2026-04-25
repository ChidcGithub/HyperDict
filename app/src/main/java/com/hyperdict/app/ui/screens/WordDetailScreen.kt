package com.hyperdict.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hyperdict.app.data.model.Meaning
import com.hyperdict.app.data.model.WordDefinition
import com.hyperdict.app.ui.theme.metro_blue
import com.hyperdict.app.ui.theme.metro_green
import com.hyperdict.app.ui.theme.metro_orange
import com.hyperdict.app.ui.theme.metro_purple
import com.hyperdict.app.ui.theme.metro_teal

@Composable
fun MetroWordDetailScreen(
    definition: WordDefinition,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        MetroWordHeader(definition)

        Spacer(modifier = Modifier.height(16.dp))

        definition.meanings.forEachIndexed { index, meaning ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = 400,
                        delayMillis = index * 100
                    )
                ) + slideInVertically(
                    animationSpec = tween(
                        durationMillis = 400,
                        delayMillis = index * 100
                    ),
                    initialOffsetY = { it / 4 }
                )
            ) {
                MetroMeaningTile(meaning, index)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun MetroWordHeader(definition: WordDefinition) {
    // Metro style word header - bold, clean design
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        color = metro_blue,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = definition.word,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            if (!definition.phonetic.isNullOrBlank()) {
                Text(
                    text = definition.phonetic,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White.copy(alpha = 0.9f),
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (!definition.phoneticUk.isNullOrBlank() || !definition.phoneticUs.isNullOrBlank()) {
                Row(
                    modifier = Modifier.padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!definition.phoneticUk.isNullOrBlank()) {
                        MetroPhoneticBadge("UK", definition.phoneticUk)
                    }
                    if (!definition.phoneticUs.isNullOrBlank()) {
                        MetroPhoneticBadge("US", definition.phoneticUs)
                    }
                }
            }
        }
    }
}

@Composable
private fun MetroPhoneticBadge(label: String, phonetic: String) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = Color.White.copy(alpha = 0.2f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = phonetic,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.95f)
            )
        }
    }
}

@Composable
private fun MetroMeaningTile(meaning: Meaning, index: Int) {
    // Metro tile colors - rotate through palette
    val tileColors = listOf(
        metro_green,
        metro_orange,
        metro_purple,
        metro_teal,
        metro_blue
    )
    val tileColor = tileColors[index % tileColors.size]

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Part of speech badge - Metro style
            Surface(
                shape = MaterialTheme.shapes.small,
                color = tileColor.copy(alpha = 0.15f)
            ) {
                Text(
                    text = meaning.partOfSpeech,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = tileColor,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            meaning.definitions.forEachIndexed { defIndex, entry ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    // Number badge - Metro style
                    Surface(
                        modifier = Modifier.size(28.dp),
                        shape = MaterialTheme.shapes.small,
                        color = tileColor.copy(alpha = 0.2f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${defIndex + 1}",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = tileColor
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = entry.definition,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (!entry.example.isNullOrBlank()) {
                            // Example - Metro quote style
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.surfaceContainerHigh
                            ) {
                                Text(
                                    text = "\"${entry.example}\"",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontStyle = FontStyle.Italic,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }

                if (defIndex < meaning.definitions.size - 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}
