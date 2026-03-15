package com.loohp.hkbuseta.utils

import com.loohp.hkbuseta.common.objects.ETADisplayMode
import com.loohp.hkbuseta.common.shared.Shared

actual fun isLiveNotificationBackgroundUpdateSystemAllowed(): Boolean = true
actual fun platformLiveNotificationETADisplayMode(): ETADisplayMode = Shared.etaDisplayMode