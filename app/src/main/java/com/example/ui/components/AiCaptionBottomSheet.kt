package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.AiCaptionSuggestion
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiCaptionBottomSheet(
    onDismiss: () -> Unit,
    onSelectCaption: (caption: String, hashtags: List<String>) -> Unit,
    onGenerate: (prompt: String, tone: String) -> List<AiCaptionSuggestion>
) {
    val tones = listOf("Royal Luxury", "Savage & Bold", "Minimalist Aesthetic", "Poetic Elegance")
    var selectedTone by remember { mutableStateOf("Royal Luxury") }
    var customPrompt by remember { mutableStateOf("") }
    var generatedCaptions by remember {
        mutableStateOf(onGenerate(customPrompt, selectedTone))
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DarkSecondary,
        scrimColor = Color.Black.copy(alpha = 0.7f),
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = GoldPrimary.copy(alpha = 0.5f))
        },
        modifier = Modifier.testTag("ai_caption_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "AI",
                    tint = GoldGlow,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Instagold AI Smart Captions",
                        style = MaterialTheme.typography.titleMedium,
                        color = GoldGlow,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Powered by Google Gemini intelligence for luxury curation",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tone Selector Chips
            Text(
                text = "SELECT BRAND TONE",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tones) { tone ->
                    val isSelected = tone == selectedTone
                    val bgMod = if (isSelected) Modifier.background(GoldenGradient) else Modifier.background(DarkSurfaceVariant)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .then(bgMod)
                            .clickable {
                                selectedTone = tone
                                generatedCaptions = onGenerate(customPrompt, tone)
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tone,
                            color = if (isSelected) DarkBackground else TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Custom Prompt Input
            OutlinedTextField(
                value = customPrompt,
                onValueChange = { customPrompt = it },
                placeholder = {
                    Text(
                        "Describe your scene (e.g. Monaco yacht at golden hour)",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_prompt_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkSurfaceVariant,
                    unfocusedContainerColor = DarkSurfaceVariant,
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Generate Button
            Button(
                onClick = {
                    generatedCaptions = onGenerate(customPrompt, selectedTone)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("ai_generate_button"),
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = DarkBackground,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Generate Captions",
                    color = DarkBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Generated Suggestions List
            Text(
                text = "SUGGESTED CAPTIONS (TAP TO APPLY)",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(generatedCaptions, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelectCaption(item.caption, item.hashtags)
                                onDismiss()
                            }
                            .testTag("caption_card_${item.id}"),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, DarkBorderHighlight)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = item.tone.uppercase(),
                                    color = GoldGlow,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Apply",
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Apply",
                                        color = GoldPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = item.caption,
                                color = TextPrimary,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = item.hashtags.joinToString(" "),
                                color = GoldPrimary.copy(alpha = 0.8f),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
