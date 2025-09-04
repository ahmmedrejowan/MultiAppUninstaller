import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun COutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    maxLines: Int = 1,
    shape: Shape = RoundedCornerShape(12.dp),
    border: BorderStroke = BorderStroke(0.5.dp, Color.Gray),
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    hint: String = "Search",
    hintStyle: TextStyle = MaterialTheme.typography.bodySmall.copy(
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    ),
    readOnly: Boolean = false,
    enabled: Boolean = true,
) {

    Box(
        modifier = modifier
            .clip(shape)
            .border(border, shape)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                Box(Modifier.padding(end = 8.dp)) { leadingIcon() }
            }

            BasicTextField(
                singleLine = maxLines == 1,
                value = value,
                onValueChange = onValueChange,
                textStyle = textStyle,
                modifier = Modifier
                    .weight(1f),
                enabled = enabled,
                interactionSource = interactionSource,
                keyboardActions = keyboardActions,
                keyboardOptions = keyboardOptions,
                visualTransformation = visualTransformation,
                maxLines = maxLines,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                readOnly = readOnly,
                decorationBox = { innerTextField ->
                    Box {
                        if (value.isEmpty()) {
                            Text(
                                text = hint,
                                style = hintStyle
                            )
                        }
                        innerTextField()
                    }
                }
            )

            if (trailingIcon != null) {
                Box(Modifier.padding(start = 8.dp)) { trailingIcon() }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun COutlinedTextFieldPreview() {
    var text by remember { mutableStateOf(("")) }

    COutlinedTextField(
        value = text,
        onValueChange = {
            text = it
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        leadingIcon = {
            Icon(Icons.Default.Person, contentDescription = "User")
        },
        trailingIcon = {
            Icon(Icons.Default.Clear, contentDescription = "Clear")
        }

    )
}
