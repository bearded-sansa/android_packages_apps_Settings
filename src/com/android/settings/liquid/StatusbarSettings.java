/*
 * Copyright (C) 2012 The LiquidSmooth Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.liquid;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.PreferenceCategory;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class StatusbarSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String PREF_STATUS_BAR_NOTIF_COUNT = "status_bar_notif_count";
    private static final String PREF_BATT_ICON = "battery_icon_list";
    private static final String PREF_BATT_NOT = "battery_not";
    private static final String PREF_BATT_BAR = "battery_bar_list";
    private static final String PREF_BATT_BAR_STYLE = "battery_bar_style";
    private static final String PREF_BATT_BAR_COLOR = "battery_bar_color";
    private static final String PREF_BATT_BAR_WIDTH = "battery_bar_thickness";
    private static final String PREF_BATT_ANIMATE = "battery_bar_animate";
    private static final String PREF_ENABLE = "clock_style";
    private static final String PREF_AM_PM_STYLE = "clock_am_pm_style";
    private static final String PREF_CLOCK_WEEKDAY = "clock_weekday";
    private static final String PREF_COLOR_PICKER = "clock_color";
    private static final String STATUS_BAR_BRIGHTNESS_CONTROL = "status_bar_brightness_control";
    private static final String PREF_SIGNAL_STYLE = "signal_style";
    private static final String PREF_SIGNAL_COLOR = "signal_color";
    private static final String PREF_WIFI_SIGNAL_STYLE = "wifi_signal_style";
    private static final String PREF_WIFI_SIGNAL_COLOR = "wifi_signal_color";
    private static final String PREF_HIDE_SIGNAL = "hide_signal";
    private static final String STATUS_BAR_CATEGORY_BATTERYBAR = "battery_bar_cat";
    private static final String STATUS_BAR_CATEGORY_CLOCK = "clock_category";
    private static final String STATUS_BAR_CATEGORY_ADDITIONAL = "additional_options";

    ListPreference mBatteryIcon;
    CheckBoxPreference mBatteryNotification;
    ListPreference mBatteryBar;
    ListPreference mBatteryBarStyle;
    ListPreference mBatteryBarThickness;
    CheckBoxPreference mBatteryBarChargingAnimation;
    ColorPickerPreference mBatteryBarColor;
    ListPreference mClockStyle;
    ListPreference mClockAmPmstyle;
    ListPreference mClockWeekday;
    ColorPickerPreference mClockColorPicker;
    ListPreference mDbmStyletyle;
    ColorPickerPreference mSignalColorPicker;
    ListPreference mWifiStyle;
    ColorPickerPreference mWifiColorPicker;
    CheckBoxPreference mHideSignal;
    CheckBoxPreference mAltSignal;
    CheckBoxPreference mStatusBarBrightnessControl;
    CheckBoxPreference mStatusBarNotifCount;
    ListPreference mStatusbarTransparency;
   	
    private PreferenceCategory mPrefCategoryBatteryBar;
    private PreferenceCategory mPrefCategoryClock;
    private PreferenceCategory mPrefCategoryAdditional;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.statusbar_settings);

	PreferenceScreen prefSet = getPreferenceScreen();

        mBatteryIcon = (ListPreference) findPreference(PREF_BATT_ICON);
        mBatteryIcon.setOnPreferenceChangeListener(this);
        mBatteryIcon.setValue((Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.STATUSBAR_BATTERY_ICON,
                0))
                + "");
        
        mBatteryNotification = (CheckBoxPreference) findPreference(PREF_BATT_NOT);
        mBatteryNotification.setChecked(Settings.System.getInt(
                getActivity().getContentResolver(),
                Settings.System.NOTIFICATION_BATTERY_DISPLAY, 0) == 1);

        mBatteryBar = (ListPreference) findPreference(PREF_BATT_BAR);
        mBatteryBar.setOnPreferenceChangeListener(this);
        mBatteryBar.setValue((Settings.System
                .getInt(getActivity().getContentResolver(),
                        Settings.System.STATUSBAR_BATTERY_BAR, 0))
                + "");

        mBatteryBarStyle = (ListPreference) findPreference(PREF_BATT_BAR_STYLE);
        mBatteryBarStyle.setOnPreferenceChangeListener(this);
        mBatteryBarStyle.setValue((Settings.System.getInt(getActivity()
                .getContentResolver(),
                Settings.System.STATUSBAR_BATTERY_BAR_STYLE, 0))
                + "");

        mBatteryBarColor = (ColorPickerPreference) findPreference(PREF_BATT_BAR_COLOR);
        mBatteryBarColor.setOnPreferenceChangeListener(this);

        mBatteryBarChargingAnimation = (CheckBoxPreference) findPreference(PREF_BATT_ANIMATE);
        mBatteryBarChargingAnimation.setChecked(Settings.System.getInt(
                getActivity().getContentResolver(),
                Settings.System.STATUSBAR_BATTERY_BAR_ANIMATE, 0) == 1);

        mBatteryBarThickness = (ListPreference) findPreference(PREF_BATT_BAR_WIDTH);
        mBatteryBarThickness.setOnPreferenceChangeListener(this);
        mBatteryBarThickness.setValue((Settings.System.getInt(getActivity()
                .getContentResolver(),
                Settings.System.STATUSBAR_BATTERY_BAR_THICKNESS, 1))
                + "");

        mClockStyle = (ListPreference) findPreference(PREF_ENABLE);
        mClockStyle.setOnPreferenceChangeListener(this);
        mClockStyle.setValue(Integer.toString(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.STATUSBAR_CLOCK_STYLE,
                1)));

        mClockAmPmstyle = (ListPreference) findPreference(PREF_AM_PM_STYLE);
        mClockAmPmstyle.setOnPreferenceChangeListener(this);
        mClockAmPmstyle.setValue(Integer.toString(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.STATUSBAR_CLOCK_AM_PM_STYLE,
                2)));

        mClockWeekday = (ListPreference) findPreference(PREF_CLOCK_WEEKDAY);
        mClockWeekday.setOnPreferenceChangeListener(this);
        mClockWeekday.setValue(Integer.toString(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.STATUSBAR_CLOCK_WEEKDAY,
                0)));
        
        mClockColorPicker = (ColorPickerPreference) findPreference(PREF_COLOR_PICKER);
        mClockColorPicker.setOnPreferenceChangeListener(this);

        mDbmStyletyle = (ListPreference) findPreference(PREF_SIGNAL_STYLE);
        mDbmStyletyle.setOnPreferenceChangeListener(this);
        mDbmStyletyle.setValue(Integer.toString(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.STATUSBAR_SIGNAL_TEXT,
                0)));
        
        mSignalColorPicker = (ColorPickerPreference) findPreference(PREF_SIGNAL_COLOR);
        mSignalColorPicker.setOnPreferenceChangeListener(this);

        mWifiStyle = (ListPreference) findPreference(PREF_WIFI_SIGNAL_STYLE);
        mWifiStyle.setOnPreferenceChangeListener(this);
        mWifiStyle.setValue(Integer.toString(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.STATUSBAR_WIFI_SIGNAL_TEXT,
                0)));
        
        mWifiColorPicker = (ColorPickerPreference) findPreference(PREF_WIFI_SIGNAL_COLOR);
        mWifiColorPicker.setOnPreferenceChangeListener(this);
        
        mHideSignal = (CheckBoxPreference) findPreference(PREF_HIDE_SIGNAL);
        mHideSignal.setChecked(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.STATUSBAR_HIDE_SIGNAL_BARS,
                0) != 0);

        mAltSignal = (CheckBoxPreference) findPreference("alt_signal");
        mAltSignal.setChecked(Settings.System.getBoolean(getContentResolver(),
                Settings.System.STATUSBAR_SIGNAL_CLUSTER_ALT,false));
        
        mStatusBarBrightnessControl = (CheckBoxPreference) findPreference(STATUS_BAR_BRIGHTNESS_CONTROL);
        mStatusBarBrightnessControl.setChecked((Settings.System.getInt(
                getActivity().getContentResolver(),
                Settings.System.STATUS_BAR_BRIGHTNESS_CONTROL, 0) == 1));

        try {
            if (Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                mStatusBarBrightnessControl.setEnabled(false);
                mStatusBarBrightnessControl.setSummary(R.string.status_bar_toggle_info);
            }
        } catch (SettingNotFoundException e) {
        }

        mStatusBarNotifCount = (CheckBoxPreference) findPreference(PREF_STATUS_BAR_NOTIF_COUNT);
        mStatusBarNotifCount.setChecked(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.STATUS_BAR_NOTIF_COUNT,
                0) == 1);
	
	// ps additions
	// tablet checks
	
        mPrefCategoryBatteryBar = (PreferenceCategory) findPreference(STATUS_BAR_CATEGORY_BATTERYBAR);
	mPrefCategoryClock = (PreferenceCategory) findPreference(STATUS_BAR_CATEGORY_CLOCK);
        mPrefCategoryAdditional = (PreferenceCategory) findPreference(STATUS_BAR_CATEGORY_ADDITIONAL);

        if (Utils.isTablet()) {
 	    mPrefCategoryBatteryBar.removeAll();
	    prefSet.removePreference(mPrefCategoryBatteryBar);
	    mPrefCategoryClock.removePreference(mClockStyle);
  	    mPrefCategoryAdditional.removePreference(mStatusBarBrightnessControl);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        if (preference == mStatusBarNotifCount) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_NOTIF_COUNT,
                    ((CheckBoxPreference) preference).isChecked() ? 1 : 0);
            return true;

        } else if (preference == mBatteryNotification) {

            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.NOTIFICATION_BATTERY_DISPLAY,
                    ((CheckBoxPreference) preference).isChecked() ? 1 : 0);
            return true;

        } else if (preference == mBatteryBarChargingAnimation) {

            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_BATTERY_BAR_ANIMATE,
                    ((CheckBoxPreference) preference).isChecked() ? 1 : 0);
            return true;

        } else if (preference == mStatusBarBrightnessControl) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_BRIGHTNESS_CONTROL,
                    ((CheckBoxPreference) preference).isChecked() ? 1 : 0);
            return true;

        } else if (preference == mHideSignal) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_HIDE_SIGNAL_BARS,
                    ((CheckBoxPreference) preference).isChecked() ? 1 : 0);
            return true;

        } else if (preference == mAltSignal) {
            Settings.System.putBoolean(getContentResolver(),
                    Settings.System.STATUSBAR_SIGNAL_CLUSTER_ALT,mAltSignal.isChecked());
            return true;

        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean result = false;

        if (preference == mBatteryIcon) {
            
            int val = Integer.parseInt((String) newValue);
            result = Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_BATTERY_ICON, val);

        } else if (preference == mClockAmPmstyle) {

            int val = Integer.parseInt((String) newValue);
            result = Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_CLOCK_AM_PM_STYLE, val);

        } else if (preference == mClockStyle) {

            int val = Integer.parseInt((String) newValue);
            result = Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_CLOCK_STYLE, val);

        } else if (preference == mClockWeekday) {
            int val = Integer.parseInt((String) newValue);
            result = Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_CLOCK_WEEKDAY, val);

       } else if (preference == mClockColorPicker) {
           String hex = ColorPickerPreference.convertToARGB(Integer
                    .valueOf(String.valueOf(newValue)));
           preference.setSummary(hex);
           
           int intHex = ColorPickerPreference.convertToColorInt(hex);
           Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_CLOCK_COLOR, intHex);

        } else if (preference == mBatteryBarColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer
                    .valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);

            int intHex = ColorPickerPreference.convertToColorInt(hex);
            result = Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_BATTERY_BAR_COLOR, intHex);

        } else if (preference == mBatteryBar) {

            int val = Integer.parseInt((String) newValue);
            return Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_BATTERY_BAR, val);

        } else if (preference == mBatteryBarStyle) {

            int val = Integer.parseInt((String) newValue);
            return Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_BATTERY_BAR_STYLE, val);

        } else if (preference == mBatteryBarThickness) {

            int val = Integer.parseInt((String) newValue);
            return Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_BATTERY_BAR_THICKNESS, val);

        } else if (preference == mDbmStyletyle) {

            int val = Integer.parseInt((String) newValue);
            return Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_SIGNAL_TEXT, val);

        } else if (preference == mSignalColorPicker) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String
                    .valueOf(newValue)));
            preference.setSummary(hex);

            int intHex = ColorPickerPreference.convertToColorInt(hex);
            result = Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_SIGNAL_TEXT_COLOR, intHex);

        } else if (preference == mWifiStyle) {
                
            int val = Integer.parseInt((String) newValue);
            return Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_WIFI_SIGNAL_TEXT, val);

        } else if (preference == mWifiColorPicker) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String
                    .valueOf(newValue)));
            preference.setSummary(hex);
                
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            result = Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_WIFI_SIGNAL_TEXT_COLOR, intHex);

        }

        return result;
    }
}
