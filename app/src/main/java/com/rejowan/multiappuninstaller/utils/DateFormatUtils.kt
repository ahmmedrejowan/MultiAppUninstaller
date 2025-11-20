/*
 * Multi App Uninstaller
 * Copyright (C) 2025 K M Rejowan Ahmmed
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
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.rejowan.multiappuninstaller.utils

import java.util.Locale

object DateFormatUtils {

    fun millisToDateTime(millis: Long): String {
        // to 08:00 AM, 01 Jan 2024
        val date = java.util.Date(millis)
        val format = java.text.SimpleDateFormat("hh:mm a, dd MMM yyyy", Locale.getDefault()) // 12-hour format
        return format.format(date)
    }
}