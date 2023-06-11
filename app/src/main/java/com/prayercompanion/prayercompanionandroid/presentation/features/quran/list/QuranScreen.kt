package com.prayercompanion.prayercompanionandroid.presentation.features.quran.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavOptionsBuilder
import com.prayercompanion.prayercompanionandroid.R
import com.prayercompanion.prayercompanionandroid.presentation.components.TitleHeader
import com.prayercompanion.prayercompanionandroid.presentation.features.quran.components.QuranChapterItem
import com.prayercompanion.prayercompanionandroid.presentation.features.quran.components.QuranSection
import com.prayercompanion.prayercompanionandroid.presentation.theme.AppBackground
import com.prayercompanion.prayercompanionandroid.presentation.theme.LocalSpacing
import com.prayercompanion.prayercompanionandroid.presentation.theme.PrayerCompanionAndroidTheme
import com.prayercompanion.prayercompanionandroid.presentation.utils.UiEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Preview(locale = "ar", showSystemUi = true)
@Composable
fun QuranScreen(
    navigate: (UiEvent.Navigate, NavOptionsBuilder.() -> Unit) -> Unit = { _, _ -> },
    state: QuranState = QuranState(),
    onEvent: (QuranEvent) -> Unit = {},
    uiEventsChannel: Flow<UiEvent> = emptyFlow(),
    showSnackBar: suspend (String) -> Unit = {},
) = PrayerCompanionAndroidTheme {

    val spacing = LocalSpacing.current
    val context = LocalContext.current
    var searchText by remember {
        mutableStateOf("")
    }

    LaunchedEffect(key1 = true) {
        uiEventsChannel.collect {
            when (it) {
                is UiEvent.ShowErrorSnackBar -> {
                    showSnackBar(it.errorMessage.asString(context))
                }
                is UiEvent.Navigate -> {
                    navigate(it) {}
                }
                else -> Unit
            }
        }
    }

    Box {
        AppBackground()
        Column(modifier = Modifier.fillMaxSize()) {
            TitleHeader(title = stringResource(id = R.string.quran_title))
            val readingSections = state.sections
            if (readingSections != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.4f)
                        .padding(top = spacing.spaceMedium, bottom = spacing.spaceSmall)
                        .padding(horizontal = spacing.spaceMedium)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colors.primary,
                                shape = RoundedCornerShape(topEnd = 15.dp, topStart = 15.dp)
                            )
                            .weight(1f)
                            .padding(spacing.spaceMedium)

                    ) {
                        QuranSection(
                            modifier = Modifier.weight(1f),
                            stringResource(id = R.string.first_quran_reading_section),
                            readingSections.firstSection
                        )
                        Spacer(modifier = Modifier.height(spacing.spaceMedium))
                        QuranSection(
                            modifier = Modifier.weight(1f),
                            stringResource(id = R.string.second_quran_reading_section),
                            readingSections.secondSection
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colors.primaryVariant,
                                shape = RoundedCornerShape(bottomEnd = 15.dp, bottomStart = 15.dp)
                            )
                            .padding(
                                horizontal = spacing.spaceExtraSmall
                            ),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { onEvent(QuranEvent.OnViewFullClicked) },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.show_full_quran_reading_sections),
                                style = MaterialTheme.typography.body2,
                                textDecoration = TextDecoration.Underline,
                                color = MaterialTheme.colors.onPrimary
                            )
                        }
                        TextButton(
                            onClick = { onEvent(QuranEvent.OnNextSectionClicked) },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.next_quran_reading_sections),
                                style = MaterialTheme.typography.body2,
                                textDecoration = TextDecoration.Underline,
                                color = MaterialTheme.colors.onPrimary
                            )
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(spacing.spaceMedium))
            }
            if (false) {
                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = spacing.spaceLarge),
                        value = searchText,
                        leadingIcon = {
                            Icon(
                                modifier = Modifier.size(28.dp),
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Icon",
                                tint = MaterialTheme.colors.onPrimary,
                            )
                        },
                        onValueChange = {
                            searchText = it
                        },
                        placeholder = {
                            Text(
                                text = stringResource(id = R.string.chapter_name),
                                color = MaterialTheme.colors.secondary,
                                style = MaterialTheme.typography.subtitle2
                            )
                        },
                        colors = TextFieldDefaults
                            .textFieldColors(
                                textColor = MaterialTheme.colors.secondary,
                                backgroundColor = MaterialTheme.colors.primary,
                                cursorColor = MaterialTheme.colors.onPrimary,
                                disabledIndicatorColor = Color.Transparent,
                                errorIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                        textStyle = MaterialTheme.typography.subtitle2,
                        singleLine = true,
                        shape = RoundedCornerShape(15.dp)
                    )
                }
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(spacing.spaceMedium)
                    .background(
                        color = MaterialTheme.colors.primary,
                        shape = RoundedCornerShape(15.dp)
                    )
                    .padding(vertical = spacing.spaceSmall, horizontal = 2.dp)
            ) {
                items(state.quran.chapters) { chapter ->
                    QuranChapterItem(
                        modifier = Modifier
                            .fillMaxWidth(),
                        quranChapter = chapter,
                        onCheckedChange = { selected, from, to ->
                            if (selected) {
                                onEvent(QuranEvent.OnChapterSelected(chapter.id, from, to))
                            } else {
                                onEvent(QuranEvent.OnChapterDeselected(chapter.id))
                            }
                        },
                        onSaveClicked = { from, to ->
                            onEvent(QuranEvent.OnChapterAyatSaved(chapter.id, from, to))
                        }
                    )
                }
            }
        }
    }
}
