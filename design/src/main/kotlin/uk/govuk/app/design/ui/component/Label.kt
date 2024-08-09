package uk.govuk.app.design.ui.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import uk.govuk.app.design.ui.theme.GovUkTheme

@Composable
private fun BaseLabel(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle,
    textAlign: TextAlign
) {
    Text(
        text = text,
        modifier = modifier,
        style = style,
        color = GovUkTheme.colourScheme.textAndIcons.primary,
        textAlign = textAlign,
    )
}

@Composable
fun LargeTitleBoldLabel(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start
) {
    BaseLabel(
        text = text,
        modifier = modifier,
        style = GovUkTheme.typography.titleLargeBold,
        textAlign = textAlign
    )
}

@Composable
fun LargeTitleRegularLabel(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start
) {
    BaseLabel(
        text = text,
        modifier = modifier,
        style = GovUkTheme.typography.titleLargeRegular,
        textAlign = textAlign
    )
}

@Composable
fun Title1BoldLabel(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start
) {
    BaseLabel(
        text = text,
        modifier = modifier,
        style = GovUkTheme.typography.title1Bold,
        textAlign = textAlign
    )
}

@Composable
fun Title1RegularLabel(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start
) {
    BaseLabel(
        text = text,
        modifier = modifier,
        style = GovUkTheme.typography.title1Regular,
        textAlign = textAlign
    )
}

@Composable
fun Title2BoldLabel(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start
) {
    BaseLabel(
        text = text,
        modifier = modifier,
        style = GovUkTheme.typography.title2Bold,
        textAlign = textAlign
    )
}

@Composable
fun Title2RegularLabel(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start
) {
    BaseLabel(
        text = text,
        modifier = modifier,
        style = GovUkTheme.typography.title2Regular,
        textAlign = textAlign
    )
}

@Composable
fun Title3BoldLabel(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start
) {
    BaseLabel(
        text = text,
        modifier = modifier,
        style = GovUkTheme.typography.title3Bold,
        textAlign = textAlign
    )
}

@Composable
fun Title3RegularLabel(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start
) {
    BaseLabel(
        text = text,
        modifier = modifier,
        style = GovUkTheme.typography.title3Regular,
        textAlign = textAlign
    )
}

@Composable
fun BodyBoldLabel(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start
) {
    BaseLabel(
        text = text,
        modifier = modifier,
        style = GovUkTheme.typography.bodyBold,
        textAlign = textAlign
    )
}

@Composable
fun BodyRegularLabel(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start
) {
    BaseLabel(
        text = text,
        modifier = modifier,
        style = GovUkTheme.typography.bodyRegular,
        textAlign = textAlign
    )
}

@Composable
fun CalloutBoldLabel(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start
) {
    BaseLabel(
        text = text,
        modifier = modifier,
        style = GovUkTheme.typography.calloutBold,
        textAlign = textAlign
    )
}

@Composable
fun CalloutRegularLabel(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start
) {
    BaseLabel(
        text = text,
        modifier = modifier,
        style = GovUkTheme.typography.calloutRegular,
        textAlign = textAlign
    )
}

@Composable
fun SubheadlineBoldLabel(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start
) {
    BaseLabel(
        text = text,
        modifier = modifier,
        style = GovUkTheme.typography.subheadlineBold,
        textAlign = textAlign
    )
}

@Composable
fun SubheadlineRegularLabel(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start
) {
    BaseLabel(
        text = text,
        modifier = modifier,
        style = GovUkTheme.typography.subheadlineRegular,
        textAlign = textAlign
    )
}

@Composable
fun FootnoteBoldLabel(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start
) {
    BaseLabel(
        text = text,
        modifier = modifier,
        style = GovUkTheme.typography.footnoteBold,
        textAlign = textAlign
    )
}

@Composable
fun FootnoteRegularLabel(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start
) {
    BaseLabel(
        text = text,
        modifier = modifier,
        style = GovUkTheme.typography.footnoteRegular,
        textAlign = textAlign
    )
}

@Composable
fun CaptionBoldLabel(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start
) {
    BaseLabel(
        text = text,
        modifier = modifier,
        style = GovUkTheme.typography.captionBold,
        textAlign = textAlign
    )
}

@Composable
fun CaptionRegularLabel(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start
) {
    BaseLabel(
        text = text,
        modifier = modifier,
        style = GovUkTheme.typography.captionRegular,
        textAlign = textAlign
    )
}

@Preview(showBackground = true)
@Composable
private fun LargeTitleBold() {
    GovUkTheme {
        LargeTitleBoldLabel("Large Title Bold Label")
    }
}

@Preview(showBackground = true)
@Composable
private fun LargeTitleRegular() {
    GovUkTheme {
        LargeTitleRegularLabel("Large Title Regular Label")
    }
}

@Preview(showBackground = true)
@Composable
private fun Title1Bold() {
    GovUkTheme {
        Title1BoldLabel("Title 1 Bold Label")
    }
}

@Preview(showBackground = true)
@Composable
private fun Title1Regular() {
    GovUkTheme {
        Title1RegularLabel("Title 1 Regular Label")
    }
}

@Preview(showBackground = true)
@Composable
private fun Title2Bold() {
    GovUkTheme {
        Title2BoldLabel("Title 2 Bold Label")
    }
}

@Preview(showBackground = true)
@Composable
private fun Title2Regular() {
    GovUkTheme {
        Title2RegularLabel("Title 2 Regular Label")
    }
}

@Preview(showBackground = true)
@Composable
private fun Title3Bold() {
    GovUkTheme {
        Title3BoldLabel("Title 3 Bold Label")
    }
}

@Preview(showBackground = true)
@Composable
private fun Title3Regular() {
    GovUkTheme {
        Title3RegularLabel("Title 3 Regular Label")
    }
}

@Preview(showBackground = true)
@Composable
private fun BodyBold() {
    GovUkTheme {
        BodyBoldLabel("Body Bold Label")
    }
}

@Preview(showBackground = true)
@Composable
private fun BodyRegular() {
    GovUkTheme {
        BodyRegularLabel("Body Regular Label")
    }
}

@Preview(showBackground = true)
@Composable
private fun CalloutBold() {
    GovUkTheme {
        CalloutBoldLabel("Callout Bold Label")
    }
}

@Preview(showBackground = true)
@Composable
private fun CalloutRegular() {
    GovUkTheme {
        CalloutRegularLabel("Callout Regular Label")
    }
}

@Preview(showBackground = true)
@Composable
private fun SubheadlineBold() {
    GovUkTheme {
        SubheadlineBoldLabel("Subheadline Bold Label")
    }
}

@Preview(showBackground = true)
@Composable
private fun SubheadlineRegular() {
    GovUkTheme {
        SubheadlineRegularLabel("Subheadline Regular Label")
    }
}

@Preview(showBackground = true)
@Composable
private fun FootnoteBold() {
    GovUkTheme {
        FootnoteBoldLabel("Footnote Bold Label")
    }
}

@Preview(showBackground = true)
@Composable
private fun FootnoteRegular() {
    GovUkTheme {
        FootnoteRegularLabel("Footnote Regular Label")
    }
}

@Preview(showBackground = true)
@Composable
private fun CaptionBold() {
    GovUkTheme {
        CaptionBoldLabel("Caption Bold Label")
    }
}

@Preview(showBackground = true)
@Composable
private fun CaptionRegular() {
    GovUkTheme {
        CaptionRegularLabel("Caption Regular Label")
    }
}
