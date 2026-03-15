package com.loohp.hkbuseta.utils

import com.loohp.hkbuseta.common.objects.ETADisplayMode
import platform.Foundation.NSProcessInfo
import platform.Foundation.lowPowerModeEnabled

actual fun isLiveNotificationBackgroundUpdateSystemAllowed(): Boolean {
    return !NSProcessInfo.processInfo.lowPowerModeEnabled
}

actual fun platformLiveNotificationETADisplayMode(): ETADisplayMode {
    return ETADisplayMode.CLOCK_TIME
}