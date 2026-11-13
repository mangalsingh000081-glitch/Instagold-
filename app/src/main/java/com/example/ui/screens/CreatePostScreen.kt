package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.theme.*

@Composable
fun CreatePostScreen(
    onPublishPost: (
        caption: String,
        hashtags: List<String>,
        location: String?,
        mediaResId: Int?,
        mediaUrl: String?,
        filterName: String
    ) -> Unit,
    onOpenAiCaptions: () -> Unit,
    prefilledCaption: String = "",
    prefilledHashtags: List<String> = emptyList(),
    modifier: Modifier = Modifier
) {
    // Media presets
    val presetImages = listOf(
        R.drawable.luxury_yacht_1794371181183 to "Monaco Yacht",
        R.drawable.luxury_watch_1794371198720 to "Tourbillon",
        R.drawable.luxury_hypercar_1794371217978 to "Hypercar",
        R.drawable.ic_instgold_logo to "Instagold"
    )

    var selectedPresetResId by remember { mutableIntStateOf(presetImages[0].first) }
    var pickedImageUri by remember { mutableStateOf<Uri?>(null) }

    var captionText by remember { mutableStateOf(prefilledCaption) }
    var locationText by remember { mutableStateOf("Monaco Yacht Club") }

    val defaultTags = listOf("#Instagold", "#GoldStandard", "#HauteLuxury", "#Bespoke", "#BillionaireMindset", "#PrivateVault")
    var selectedHashtags by remember { mutableStateOf(prefilledHashtags.ifEmpty { listOf("#Instagold", "#GoldStandard") }) }

    val filters = listOf("Normal", "Gold Noir", "24K Glow", "Midnight Luxe")
    var selectedFilter by remember { mutableStateOf("Normal") }

    // Update if prefilled changed from AI generator
    LaunchedEffect(prefilledCaption, prefilledHashtags) {
        if (prefilledCaption.isNotBlank()) {
            captionText = prefilledCaption
        }
        if (prefilledHashtags.isNotEmpty()) {
            selectedHashtags = prefilledHashtags
        }
    }

    // Zero-permission Android Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            pickedImageUri = uri
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("create_post_screen")
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "NEW MASTERPIECE",
                color = GoldGlow,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            // Open Device Photo Picker Button
            OutlinedButton(
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier
                    .height(34.dp)
                    .testTag("choose_gallery_button"),
                shape = RoundedCornerShape(17.dp),
                border = BorderStroke(1.dp, GoldPrimary),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldGlow),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Pick Photo", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Preset Shoots Row
        Text(
            text = "OR SELECT LUXURY STUDIO ASSET",
            color = TextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(presetImages) { (resId, label) ->
                val isSelected = (resId == selectedPresetResId && pickedImageUri == null)
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(
                            2.dp,
                            if (isSelected) GoldGlow else DarkBorder,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable {
                            selectedPresetResId = resId
                            pickedImageUri = null
                        }
                ) {
                    Image(
                        painter = painterResource(id = resId),
                        contentDescription = label,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .background(Color.Black.copy(alpha = 0.65f))
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = TextPrimary,
                            fontSize = 9.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Live Image Preview Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.1f),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.5.dp, GoldPrimary.copy(alpha = 0.6f)),
            colors = CardDefaults.cardColors(containerColor = DarkSecondary)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (pickedImageUri != null) {
                    AsyncImage(
                        model = pickedImageUri,
                        contentDescription = "Selected Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = selectedPresetResId),
                        contentDescription = "Selected Preset",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Filter tint simulation
                when (selectedFilter) {
                    "Gold Noir" -> Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0x33D4AF37))
                    )
                    "24K Glow" -> Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0x22FFD700))
                    )
                    "Midnight Luxe" -> Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0x33101719))
                    )
                }

                // Active Filter Pill
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                        .border(1.dp, GoldPrimary, RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = selectedFilter,
                        color = GoldGlow,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Filter Selection Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(filters) { filter ->
                val isSelected = filter == selectedFilter
                val bgMod = if (isSelected) Modifier.background(GoldenGradient) else Modifier.background(DarkSecondary)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .then(bgMod)
                        .border(1.dp, if (isSelected) GoldLight else DarkBorder, RoundedCornerShape(16.dp))
                        .clickable { selectedFilter = filter }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = filter,
                        color = if (isSelected) DarkBackground else TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // AI Smart Caption Generator Banner Button
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, GoldPrimary, RoundedCornerShape(14.dp))
                .clickable(onClick = onOpenAiCaptions)
                .testTag("open_ai_caption_button"),
            color = DarkSecondary,
            tonalElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(GoldenGradient, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Gemini AI",
                            tint = DarkBackground,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Smart AI Caption Generator",
                            color = GoldGlow,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Generate Royal, Savage, or Poetic captions",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = GoldPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Caption Input
        Text(
            text = "CAPTION",
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = captionText,
            onValueChange = { captionText = it },
            placeholder = {
                Text(
                    "Write an exclusive caption for Instagold connoisseurs...",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .testTag("caption_input"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DarkSecondary,
                unfocusedContainerColor = DarkSecondary,
                focusedBorderColor = GoldPrimary,
                unfocusedBorderColor = DarkBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Location Input
        Text(
            text = "LOCATION",
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = locationText,
            onValueChange = { locationText = it },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = GoldPrimary
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("location_input"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DarkSecondary,
                unfocusedContainerColor = DarkSecondary,
                focusedBorderColor = GoldPrimary,
                unfocusedBorderColor = DarkBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Hashtags Suggestions
        Text(
            text = "TAP TO ADD HASHTAGS",
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(defaultTags) { tag ->
                val isIncluded = selectedHashtags.contains(tag)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isIncluded) GoldPrimary.copy(alpha = 0.25f) else DarkSecondary)
                        .border(
                            1.dp,
                            if (isIncluded) GoldPrimary else DarkBorder,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable {
                            selectedHashtags = if (isIncluded) {
                                selectedHashtags - tag
                            } else {
                                selectedHashtags + tag
                            }
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = tag,
                        color = if (isIncluded) GoldGlow else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = if (isIncluded) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Publish Button with Glowing Golden Gradient
        Button(
            onClick = {
                onPublishPost(
                    captionText.ifBlank { "Luxury and craftsmanship at its finest. ⚜️" },
                    selectedHashtags,
                    locationText.ifBlank { "Monaco" },
                    if (pickedImageUri == null) selectedPresetResId else null,
                    pickedImageUri?.toString(),
                    selectedFilter
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("publish_post_button"),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(GoldenGradient)
                    .border(1.5.dp, GoldLight, RoundedCornerShape(26.dp)),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Publish,
                        contentDescription = null,
                        tint = DarkBackground,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Publish to Instagold Feed",
                        color = DarkBackground,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(60.dp))
    }
}
