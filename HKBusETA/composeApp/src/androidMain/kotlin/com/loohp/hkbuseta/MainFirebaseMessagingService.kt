/*
 * This file is part of HKBusETA.
 *
 * Copyright (C) 2026. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2026. Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.loohp.hkbuseta

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.loohp.hkbuseta.appcontext.nonActiveAppContext
import com.loohp.hkbuseta.common.shared.Registry
import com.loohp.hkbuseta.common.shared.Shared
import com.loohp.hkbuseta.common.utils.debugLog
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit


class MainFirebaseMessagingService: FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        // debugLog("Refreshed token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val payload = message.data
        when (payload["action"]) {
            "Alert" -> {
                runBlocking { Shared.handleAlertRemoteNotification(payload["data"]!!, nonActiveAppContext) }
            }
            "Refresh" -> {
                val registry = Registry.getInstance(nonActiveAppContext)
                while (registry.state.value.isProcessing) {
                    TimeUnit.MILLISECONDS.sleep(100)
                }
            }
        }
    }

}