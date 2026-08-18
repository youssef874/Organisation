package com.organisation.ui.common.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.input.VisualTransformation
import com.organisation.OrganisationPreviews

@Composable
fun OrgOutlineTextField(
    modifier: Modifier = Modifier,
    text: String,
    onValueChanged: (String) -> Unit,
    isEnabled: Boolean = true,
    singleLine: Boolean = false,
    shape: Shape = MaterialTheme.shapes.large,
    leadingIcon: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit)? = null,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    var value by remember { mutableStateOf(text)}
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = {
            value = it
            onValueChanged(it)
        },
        enabled = isEnabled,
        singleLine = singleLine,
        shape = shape,
        leadingIcon = leadingIcon,
        label = label,
        colors = colors,
        visualTransformation = visualTransformation
    )
}

@OrganisationPreviews
@Composable
fun OrgOutlineTextFieldPreview() {
    OrgOutlineTextField(
        text = "test",
        onValueChanged = {

        },
        colors = OutlinedTextFieldDefaults.colors().copy(
            focusedContainerColor = Color.Gray,
            unfocusedContainerColor = Color.LightGray,
            focusedIndicatorColor = Color.Red,
            unfocusedIndicatorColor = Color.Blue
        )
    )
}