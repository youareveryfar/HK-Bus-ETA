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
data class DiscountedFareRules(
    val concession: DiscountedRate = DiscountedRate.HALF,
    val student: DiscountedRate = DiscountedRate.NO_DISCOUNT,
    val joyyou: DiscountedRate = DiscountedRate.TWO_DOLLAR_FLAT_RATE_OR_EIGHTY_PERCENT_OFF
) {
   companion object {
       val DEFAULT = DiscountedFareRules()

       val OPERATOR_DEFAULT: Map<Operator, DiscountedFareRules> = mapOf(
           Operator.MTR to DiscountedFareRules(
               concession = DiscountedRate.HALF,
               student = DiscountedRate.HALF,
               joyyou = DiscountedRate.TWO_DOLLAR_FLAT_RATE_OR_EIGHTY_PERCENT_OFF
           ),
           Operator.LRT to DiscountedFareRules(
               concession = DiscountedRate.HALF,
               student = DiscountedRate.HALF,
               joyyou = DiscountedRate.TWO_DOLLAR_FLAT_RATE_OR_EIGHTY_PERCENT_OFF
           ),
           Operator.GMB to DiscountedFareRules(
               concession = DiscountedRate.NO_DISCOUNT,
               student = DiscountedRate.NO_DISCOUNT,
               joyyou = DiscountedRate.TWO_DOLLAR_FLAT_RATE_OR_EIGHTY_PERCENT_OFF
           )
       )
   }
}

@Serializable
data class DiscountedRate(
    val rate: Float,
    val minimum: Fare = Fare.ZERO
) {
    companion object {
        val NO_DISCOUNT = DiscountedRate(rate = 1F)
        val HALF = DiscountedRate(rate = 0.5F)
        val TWO_DOLLAR_FLAT_RATE_OR_EIGHTY_PERCENT_OFF = DiscountedRate(rate = 0.2F, minimum = Fare.TWO)
    }

    val hasDiscount: Boolean get() = rate < 1F

    fun <T> apply(standardFare: Fare, discounted: Fare.() -> T, standard: Fare.() -> T): T {
        return if (hasDiscount) {
            discounted.invoke(maxOf(standardFare * rate, minimum))
        } else {
            standard.invoke(standardFare)
        }
    }
}

@Serializable
enum class FareCategory(
    val displayName: BilingualText,
    val formula: DiscountedFaresFormula,
) {

    ADULT(
        displayName = "成人" withEn "Adult",
        formula = { toResult(shortDescription = "正價" withEn "Standard", isDiscounted = false) }
    ),
    CHILD(
        displayName = "小童" withEn "Child",
        formula = {
            it.concession.apply(
                standardFare = this,
                discounted = { toResult(shortDescription = "特惠" withEn "Concession", isDiscounted = true) },
                standard = { discountedAs(ADULT, it) }
            )
        }
    ),
    ELDERLY(
        displayName = "長者/樂悠卡(65歲或以上)" withEn "Elderly/JoyYou Card (Aged 65 or above)",
        formula = {
            minOf(discountedAs(CHILD, it), discountedAs(JOYYOU_SIXTY, it))
        }
    ),
    JOYYOU_SIXTY(
        displayName = "樂悠卡(60至64歲)" withEn "JoyYou Card (Aged 60 - 64)",
        formula = {
            it.joyyou.apply(
                standardFare = this,
                discounted = { toResult(shortDescription = "樂悠" withEn "JoyYou", isDiscounted = true) },
                standard = { discountedAs(ADULT, it) }
            )
        }
    ),
    PWD(
        displayName = "殘疾人士" withEn "Persons with Disabilities",
        formula = { discountedAs(JOYYOU_SIXTY, it).copy(shortDescription = "殘疾" withEn "Disabilities") }
    ),
    STUDENT(
        displayName = "學生" withEn "Student",
        formula = {
            it.student.apply(
                standardFare = this,
                discounted = { toResult(shortDescription = "學生" withEn "Student", isDiscounted = true) },
                standard = { discountedAs(ADULT, it) }
            )
        }
    );

    val nextFareCategory: FareCategory get() = entries[(entries.indexOf(this) + 1) % entries.size]
}

fun interface DiscountedFaresFormula {
    fun Fare.calculate(rules: DiscountedFareRules): DiscountedFaresResult
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
    return discountedAs(fareCategory, getDiscountedFareRules(co, routeNumber))
}

fun Fare.discountedAs(fareCategory: FareCategory, rules: DiscountedFareRules): DiscountedFaresResult {
    return fareCategory.formula.run { calculate(rules) }
}

fun getDiscountedFareRules(co: Operator, routeNumber: String): DiscountedFareRules {
    return Shared.discountedFareRules[co]?.get(routeNumber)
        ?: DiscountedFareRules.OPERATOR_DEFAULT[co]
        ?: DiscountedFareRules.DEFAULT
}

enum class TicketCategory(
    val displayName: BilingualText
) {

    OCTO("八達通" withEn "Octopus"),
    SINGLE("單程票" withEn "Single Journey Ticket");

}