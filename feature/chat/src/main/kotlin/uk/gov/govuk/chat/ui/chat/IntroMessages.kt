package uk.gov.govuk.chat.ui.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import uk.gov.govuk.chat.R
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
fun IntroMessages(animated: Boolean) {
    if (animated) {
        var message1Visible by remember { mutableStateOf(false) }
        var message2Visible by remember { mutableStateOf(false) }
        var message3Visible by remember { mutableStateOf(false) }

        val delay = 1000L
        val duration = 500

        LaunchedEffect(key1 = true) {
            delay(delay)

            message1Visible = true
            delay(delay)

            message2Visible = true
            delay(delay)

            message3Visible = true
        }

        MessageHeader()
        MediumVerticalSpacer()

        AnimatedVisibility(
            visible = message1Visible,
            enter = fadeIn(animationSpec = tween(durationMillis = duration)) +
                scaleIn(animationSpec = tween(durationMillis = duration))
        ) {
            Message1()
        }

        if (message1Visible) {
            MediumVerticalSpacer()
        }

        AnimatedVisibility(
            visible = message2Visible,
            enter = fadeIn(animationSpec = tween(durationMillis = duration)) +
                scaleIn(animationSpec = tween(durationMillis = duration))
        ) {
            Message2()
        }

        if (message2Visible) {
            MediumVerticalSpacer()
        }

        AnimatedVisibility(
            visible = message3Visible,
            enter = fadeIn(animationSpec = tween(durationMillis = duration)) +
                scaleIn(animationSpec = tween(durationMillis = duration))
        ) {
            Message3()
        }
    } else {
        MessageHeader()
        MediumVerticalSpacer()
        Message1()
        MediumVerticalSpacer()
        Message2()
        MediumVerticalSpacer()
        Message3()
    }
}

@Composable
private fun MessageHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = GovUkTheme.spacing.medium,
                end = GovUkTheme.spacing.medium,
                bottom = 0.dp,
                top = 48.dp
            ),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.bot_message_availability_text),
            color = GovUkTheme.colourScheme.textAndIcons.chatBotHeaderText,
            style = GovUkTheme.typography.bodyRegular
        )
    }
}

@Composable
private fun Message1() {
    Answer(
        answer = stringResource(id = R.string.bot_message_1),
        modifier = Modifier.padding(bottom = GovUkTheme.spacing.medium)
    )
}

@Composable
private fun Message2() {
    Answer(
        showHeader = false,
        answer = stringResource(id = R.string.bot_message_2),
        modifier = Modifier.padding(vertical = GovUkTheme.spacing.medium)
    )
}

@Composable
private fun Message3() {
    Answer(
        showHeader = false,
        answer = stringResource(id = R.string.bot_message_3),
        modifier = Modifier.padding(vertical = GovUkTheme.spacing.medium)
    )
}
