package uk.gov.govuk.chat.ui.chat

import androidx.compose.animation.core.DurationBasedAnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import uk.gov.govuk.chat.R
import uk.gov.govuk.chat.ui.model.ChatEntry
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
fun DisplayChatEntry(
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
    animationSpec: DurationBasedAnimationSpec<Float> = tween(1000, 500, LinearEasing)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "LoadingTextTransition")

    val shimmerProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animationSpec),
        label = "LoadingTextProgress"
    )

    val colorDark = GovUkTheme.colourScheme.textAndIcons.chatLoadingTextDark
    val colorLight = GovUkTheme.colourScheme.textAndIcons.chatLoadingTextLight

    val brush = remember(shimmerProgress, colorDark, colorLight) {
        LoadingTextShimmerBrush(
            shimmerProgress = shimmerProgress,
            colorDark = colorDark,
            colorLight = colorLight
        )
    }

    Text(
        text = text,
        modifier = modifier,
        style = GovUkTheme.typography.bodyRegular.copy(brush = brush)
    )
}

private class LoadingTextShimmerBrush(
    private val shimmerProgress: Float,
    private val colorDark: Color,
    private val colorLight: Color
): ShaderBrush() {

    override fun createShader(size: Size): Shader {
        val initialXOffset = -size.width
        val totalSweepDistance = size.width * 2
        val currentPosition = initialXOffset + totalSweepDistance * shimmerProgress

        return LinearGradientShader(
            colors = listOf(colorDark, colorLight),
            from = Offset(currentPosition, 0f),
            to = Offset(currentPosition + size.width, 0f)
        )
    }
}
