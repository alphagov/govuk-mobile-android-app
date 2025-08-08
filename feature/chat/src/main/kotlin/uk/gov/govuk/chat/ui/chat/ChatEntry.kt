package uk.gov.govuk.chat.ui.chat

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.delay
import uk.gov.govuk.chat.R
import uk.gov.govuk.chat.ui.model.ChatEntry
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
internal fun DisplayChatEntry(
    isLoading: Boolean,
    chatEntry: ChatEntry,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        MediumVerticalSpacer()
        Question(question = chatEntry.question)

        MediumVerticalSpacer()
        if (isLoading && chatEntry.answer.isEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_circle_24),
                    contentDescription = null,
                    tint = GovUkTheme.colourScheme.textAndIcons.chatLoadingIcon,
                    modifier = Modifier
                        .padding(end = GovUkTheme.spacing.small),
                )

                LoadingText(
                    text = stringResource(id = R.string.loading_text)
                )
            }
        } else {
            Answer(
                answer = chatEntry.answer,
                sources = chatEntry.sources
            )
        }
    }
}

@Composable
fun LoadingText(
    text: String,
    modifier: Modifier = Modifier,
    baseColor: Color = GovUkTheme.colourScheme.textAndIcons.chatLoadingTextDark,
    animationDuration: Int = 500,
    pauseDuration: Long = 300L
) {
    val progress = remember { Animatable(0f) }
    var faded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            if (!faded) {
                delay(pauseDuration)
            }

            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = animationDuration, easing = LinearOutSlowInEasing)
            )

            faded = !faded
            progress.snapTo(0f)
        }
    }

    val brush = Brush.horizontalGradient(
        colorStops = arrayOf(
            0f to baseColor.copy(alpha = if (faded) 1f else 0.3f),
            progress.value to baseColor.copy(alpha = if (faded) 1f else 0.3f),
            (progress.value + 0.01f).coerceAtMost(1f) to baseColor.copy(alpha = if (faded) 0.3f else 1f),
            1f to baseColor.copy(alpha = if (faded) 0.3f else 1f),
        )
    )

    Text(
        text = text,
        modifier = modifier,
        style = GovUkTheme.typography.bodyRegular.copy(brush = brush)
    )
}