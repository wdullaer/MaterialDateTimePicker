/*
 * Copyright (C) 2017 Wouter Dullaert
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wdullaer.materialdatetimepicker.date;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Calendar;

@SuppressWarnings("WeakerAccess")
public interface DateRangeLimiter extends Parcelable {
    int getMinYear();

    int getMaxYear();

    @NonNull Calendar getStartDate();

    @NonNull Calendar getEndDate();

    boolean isOutOfRange(int year, int month, int day);

    @NonNull Calendar setToNearestDate(@NonNull Calendar day);
}