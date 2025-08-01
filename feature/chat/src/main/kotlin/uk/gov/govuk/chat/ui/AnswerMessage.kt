package uk.gov.govuk.chat.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import dev.jeziellago.compose.markdowntext.MarkdownText
import uk.gov.govuk.chat.R
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
fun DisplayAnswer(
    answer: String,
    modifier: Modifier = Modifier,
    showHeader: Boolean = true,
    sources: List<String>? = null
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = GovUkTheme.colourScheme.surfaces.chatBotMessageBackground,
            contentColor = GovUkTheme.colourScheme.textAndIcons.chatBotMessageText
        ),
        border = BorderStroke(1.dp, GovUkTheme.colourScheme.strokes.chatBotMessageBorder),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        if (showHeader) {
            BodyBoldLabel(
                text = stringResource(id = R.string.bot_header_text),
                modifier = Modifier.padding(GovUkTheme.spacing.medium)
            )
        }

        DisplayMarkdownText(
            text = answer,
            talkbackText = answer,
            modifier = modifier
        )

        if (!sources.isNullOrEmpty()) {
            DisplaySources(sources = sources)
        }
    }
}

@Composable
private fun DisplaySources(sources: List<String>) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val degrees by animateFloatAsState(if (expanded) 0f else -180f)

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(GovUkTheme.spacing.medium),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ChatDivider()
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(GovUkTheme.spacing.medium),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_info_24),
                contentDescription = null,
                tint = GovUkTheme.colourScheme.textAndIcons.icon,
                modifier = Modifier
                    .padding(end = GovUkTheme.spacing.small),
            )

            BodyBoldLabel(
                text = stringResource(id = R.string.bot_sources_header_text)
            )
        }

        Row(
            modifier = Modifier
                .clickable { expanded = !expanded }
                .fillMaxWidth()
                .padding(GovUkTheme.spacing.medium),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BodyRegularLabel(
                text = stringResource(id = R.string.bot_sources_list_description)
            )

            Image(
                painter = painterResource(R.drawable.outline_arrow_drop_up_24),
                contentDescription = null,
                modifier = Modifier.rotate(degrees),
                colorFilter = ColorFilter.tint(GovUkTheme.colourScheme.textAndIcons.icon)
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                sources.forEachIndexed { index, _ ->
                    val linkAddendumText = stringResource(id = R.string.sources_open_in_text)
                    val linkText = "${sources[index]} $linkAddendumText"

                    MediumVerticalSpacer()

                    DisplayMarkdownText(
                        text = sources[index],
                        talkbackText = linkText
                    )

                    if (index < sources.size - 1) {
                        MediumVerticalSpacer()
                        ChatDivider(
                            modifier = Modifier.padding(horizontal = GovUkTheme.spacing.medium)
                        )
                    }
                }

                MediumVerticalSpacer()
            }
        }
    }
}

@Composable
private fun DisplayMarkdownText(
    text: String,
    talkbackText: String,
    modifier: Modifier = Modifier
) {
    MarkdownText(
        markdown = text,
        linkColor = GovUkTheme.colourScheme.textAndIcons.link,
        style = TextStyle(
            color = GovUkTheme.colourScheme.textAndIcons.primary,
            fontSize = GovUkTheme.typography.bodyRegular.fontSize,
            fontFamily = GovUkTheme.typography.bodyRegular.fontFamily,
            fontWeight = GovUkTheme.typography.bodyRegular.fontWeight
        ),
        enableSoftBreakAddsNewLine = false,
        enableUnderlineForLink = false,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = GovUkTheme.spacing.medium)
            .semantics {
                contentDescription = talkbackText
            }
    )
}

@Composable
private fun ChatDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        thickness = 1.dp,
        color = GovUkTheme.colourScheme.strokes.chatDivider,
        modifier = modifier
    )
}
