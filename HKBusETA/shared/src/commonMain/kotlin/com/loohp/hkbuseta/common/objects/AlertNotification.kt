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

package com.loohp.hkbuseta.common.objects

import com.loohp.hkbuseta.common.shared.Shared
import kotlinx.serialization.Serializable


@Serializable
data class AlertNotification(
    val id: Int,
    val routes: List<ANRouteIdUrl>,
    val title: BilingualText,
    val content: BilingualText,
    val fallbackUrl: BilingualText,
)

@Serializable
data class ANRouteId(
    val routeNumber: String,
    val operator: List<Operator>
)

@Serializable
data class ANRouteIdUrl(
    val id: ANRouteId,
    val url: BilingualText
)

fun ANRouteId.matches(route: Route): Boolean {
    return route.routeNumber == routeNumber && route.co.any { operator.contains(it) }
}

fun AlertNotification.isInterested(): Boolean {
   return routes.isEmpty() || Shared.favoriteRouteStops.value.any { g -> g.favouriteRouteStops.any { r -> routes.any { i -> i.id.matches(r.route) } } }
}

val AlertNotification.url: BilingualText get() {
    for (group in Shared.favoriteRouteStops.value) {
        for (routeStop in group.favouriteRouteStops) {
            for (route in routes) {
                if (route.id.matches(routeStop.route)) {
                    return route.url
                }
            }
        }
    }
    return fallbackUrl
}

fun AlertNotification.url(): Boolean {
    return routes.isEmpty() || Shared.favoriteRouteStops.value.any { g -> g.favouriteRouteStops.any { r -> routes.any { i -> i.id.matches(r.route) } } }
}