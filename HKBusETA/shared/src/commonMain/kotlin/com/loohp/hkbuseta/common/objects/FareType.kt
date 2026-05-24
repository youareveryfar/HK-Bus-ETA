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
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class FareType(
    val category: FareCategory,
    val ticketCategory: TicketCategory
) {

    @SerialName("octo_adult")
    OCTO_ADULT(FareCategory.ADULT, TicketCategory.OCTO),
    @SerialName("octo_child")
    OCTO_CHILD(FareCategory.CHILD, TicketCategory.OCTO),
    @SerialName("octo_elderly")
    OCTO_ELDERLY(FareCategory.ELDERLY, TicketCategory.OCTO),
    @SerialName("octo_joyyou_sixty")
    OCTO_JOYYOU_SIXTY(FareCategory.JOYYOU_SIXTY, TicketCategory.OCTO),
    @SerialName("octo_pwd")
    OCTO_PWD(FareCategory.PWD, TicketCategory.OCTO),
    @SerialName("octo_student")
    OCTO_STUDENT(FareCategory.STUDENT, TicketCategory.OCTO),
    @SerialName("single_adult")
    SINGLE_ADULT(FareCategory.ADULT, TicketCategory.SINGLE),
    @SerialName("single_child")
    SINGLE_CHILD(FareCategory.CHILD, TicketCategory.SINGLE),
    @SerialName("single_elderly")
    SINGLE_ELDERLY(FareCategory.ELDERLY, TicketCategory.SINGLE);

}

@Serializable
enum class FareCategory(
    val displayName: BilingualText,
    val formula: DiscountedFaresFormula,
) {

    ADULT(
        displayName = "成人" withEn "Adult",
        formula = { _, _ -> toResult(shortDescription = "正價" withEn "Standard", isDiscounted = false) }
    ),
    CHILD(
        displayName = "小童" withEn "Child",
        formula = { _, _ -> (this * 0.5F).toResult(shortDescription = "特惠" withEn "Concession", isDiscounted = true) }
    ),
    ELDERLY(
        displayName = "長者/樂悠卡(65歲或以上)" withEn "Elderly/JoyYou Card (Aged 65 or above)",
        formula = { co, routeNumber ->
            minOf(discountedAs(CHILD, co, routeNumber), discountedAs(JOYYOU_SIXTY, co, routeNumber))
        }
    ),
    JOYYOU_SIXTY(
        displayName = "樂悠卡(60至64歲)" withEn "JoyYou Card (Aged 60 - 64)",
        formula = { co, routeNumber ->
            if (isJoyyouExcluded(co, routeNumber)) {
                discountedAs(ADULT, co, routeNumber)
            } else {
                maxOf(Fare.TWO, this * 0.2F).toResult(shortDescription = "樂悠" withEn "JoyYou", isDiscounted = true)
            }
        }
    ),
    PWD(
        displayName = "殘疾人士" withEn "Persons with Disabilities",
        formula = { co, routeNumber ->
            discountedAs(JOYYOU_SIXTY, co, routeNumber).copy(shortDescription = "殘疾" withEn "Disabilities")
        }
    ),
    STUDENT(
        displayName = "學生" withEn "Student",
        formula = { co, routeNumber ->
            if (co.isTrain) {
                discountedAs(CHILD, co, routeNumber).copy(shortDescription = "學生" withEn "Student")
            } else {
                discountedAs(ADULT, co, routeNumber)
            }
        }
    );

    val nextFareCategory: FareCategory get() = entries[(entries.indexOf(this) + 1) % entries.size]
}

fun interface DiscountedFaresFormula {
    fun Fare.calculate(co: Operator, routeNumber: String): DiscountedFaresResult
}

private fun Fare.toResult(shortDescription: BilingualText, isDiscounted: Boolean): DiscountedFaresResult {
    return DiscountedFaresResult(shortDescription, isDiscounted, this)
}

data class DiscountedFaresResult(
    val shortDescription: BilingualText,
    val isDiscounted: Boolean,
    val fare: Fare
): Comparable<DiscountedFaresResult> {
    override fun compareTo(other: DiscountedFaresResult): Int {
        return fare.compareTo(other.fare)
    }
}

fun Fare.discountedAs(fareCategory: FareCategory, co: Operator, routeNumber: String): DiscountedFaresResult {
    return fareCategory.formula.run { calculate(co, routeNumber) }
}

fun isJoyyouExcluded(co: Operator, routeNumber: String): Boolean {
    return Shared.joyyouExcludedRoute[co]?.contains(routeNumber) == true
}

enum class TicketCategory(
    val displayName: BilingualText
) {

    OCTO("八達通" withEn "Octopus"),
    SINGLE("單程票" withEn "Single Journey Ticket");

}