package com.organisation.ui.common.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import com.organisation.OrganisationPreviews
import com.organisation.ui.theme.AppBackground
import com.organisation.ui.theme.AppBackgroundDark
import com.organisation.ui.theme.AppButtonColor
import com.organisation.ui.theme.AppButtonColorDark

@Composable
fun OrgPrimaryButton(
    modifier: Modifier = Modifier,
    isButtonEnabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    colors: ButtonColors = ButtonDefaults.buttonColors().copy(
        containerColor = if (!isSystemInDarkTheme()) AppButtonColor else AppButtonColorDark,
        contentColor = if (!isSystemInDarkTheme()) AppBackground else AppBackgroundDark
    ),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        modifier = modifier,
        enabled = isButtonEnabled,
        onClick = onClick,
        content = content,
        shape = shape,
        colors = colors,
        elevation = elevation

    )
}

@OrganisationPreviews
@Composable
fun OrgPrimaryButtonPreview() {
    OrgPrimaryButton(
        onClick = {}
    ) {
        Text(text = "Primary Button")
    }
}

@OrganisationPreviews
@Composable
fun OrgPrimaryButtonDisabledPreview() {
    OrgPrimaryButton(
        onClick = {},
        isButtonEnabled = false
    ) {
        Text(text = "Primary Button")
    }
}