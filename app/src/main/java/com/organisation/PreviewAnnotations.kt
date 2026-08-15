package com.organisation

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
annotation class ThemePreviews

@Preview(name = "Portrait", device = Devices.PIXEL_4, showSystemUi = true)
@Preview(name = "Landscape", device = "spec:width=640dp,height=360dp,dpi=480", showSystemUi = true)
@Preview(name = "Tablet", device = "spec:width=1280dp,height=800dp,dpi=240", showSystemUi = true)
annotation class DevicePreviews


@ThemePreviews
@DevicePreviews
annotation class OrganisationPreviews