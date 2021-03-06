/*
 * Copyright (C) 2012 The LiquidSmooth Project
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

package com.android.settings.performance;

import java.io.File;

import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.util.CMDProcessor;
import com.android.settings.util.Helpers;

public class PerformanceSettings extends SettingsPreferenceFragment implements
                OnPreferenceChangeListener {

    public static final String KSM_RUN_FILE = "/sys/kernel/mm/ksm/run";

    public static final String KSM_PREF = "pref_ksm";

    public static final String KSM_PREF_DISABLED = "0";

    public static final String KSM_PREF_ENABLED = "1";

    private static final String ZRAM_PREF = "pref_zram_size";

    private static final String ZRAM_PERSIST_PROP = "persist.service.zram"; // was compcache

    private static final String ZRAM_DEFAULT = SystemProperties.get("ro.zram.default"); // was compcache

    private static final String PREF_USB_FAST_CHARGE = "usb_fast_charge";

    public static final String IOSCHED_PREF = "pref_io_sched";

    public static final String IOSCHED_LIST_FILE = "/sys/block/mmcblk0/queue/scheduler";

    public static final String SOB_PREF = "pref_io_sched_set_on_boot";

    private ListPreference mzRAM;

    private CheckBoxPreference mKSMPref;

    private CheckBoxPreference mUSBFastCharge;

    private String mIOSchedulerFormat;

    private ListPreference mIOSchedulerPref;

    private int swapAvailable = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getPreferenceManager() != null) {
            mIOSchedulerFormat = getString(R.string.io_sched_summary);
            String[] availableIOSchedulers = new String[0];
            String availableIOSchedulersLine;
            int bropen, brclose;
            String currentIOScheduler = null;

            addPreferencesFromResource(R.xml.performance_settings);

            PreferenceScreen prefSet = getPreferenceScreen();

            mzRAM = (ListPreference) prefSet.findPreference(ZRAM_PREF);
            mKSMPref = (CheckBoxPreference) prefSet.findPreference(KSM_PREF);
            mUSBFastCharge = (CheckBoxPreference) findPreference(PREF_USB_FAST_CHARGE);
            mIOSchedulerPref = (ListPreference) prefSet.findPreference(IOSCHED_PREF);

            if (isSwapAvailable()) {
                if (SystemProperties.get(ZRAM_PERSIST_PROP) == "1")
                    SystemProperties.set(ZRAM_PERSIST_PROP, ZRAM_DEFAULT);
                mzRAM.setValue(SystemProperties.get(ZRAM_PERSIST_PROP, ZRAM_DEFAULT));
                mzRAM.setOnPreferenceChangeListener(this);
            } else {
                prefSet.removePreference(mzRAM);
            }

            if (Utils.fileExists(KSM_RUN_FILE)) {
                mKSMPref.setChecked(KSM_PREF_ENABLED.equals(Utils.fileReadOneLine(KSM_RUN_FILE)));
            } else {
                prefSet.removePreference(mKSMPref);
            }

            /* I/O scheduler
            Some systems might not use I/O schedulers */
            if (!Utils.fileExists(IOSCHED_LIST_FILE) ||
                (availableIOSchedulersLine = Utils.fileReadOneLine(IOSCHED_LIST_FILE)) == null) {
                prefSet.removePreference(mIOSchedulerPref);
            } else {
                availableIOSchedulers = availableIOSchedulersLine.replace("[", "").replace("]", "").split(" ");
                bropen = availableIOSchedulersLine.indexOf("[");
                brclose = availableIOSchedulersLine.lastIndexOf("]");

                if (bropen >= 0 && brclose >= 0)
                    currentIOScheduler = availableIOSchedulersLine.substring(bropen + 1, brclose);

                mIOSchedulerPref.setEntryValues(availableIOSchedulers);
                mIOSchedulerPref.setEntries(availableIOSchedulers);
                if (currentIOScheduler != null)
                    mIOSchedulerPref.setValue(currentIOScheduler);
                mIOSchedulerPref.setSummary(String.format(mIOSchedulerFormat, currentIOScheduler));
                mIOSchedulerPref.setOnPreferenceChangeListener(this);
            }
        }

        boolean fast = false;
        if (Helpers.isFastCharge() == 1) fast = true;
        mUSBFastCharge.setChecked(fast);
        int fastOnOff = Helpers.isFastCharge();
        switch (fastOnOff) {
            case 0:
                mUSBFastCharge.setSummary("USB is in normal MTS mode");
                break;
            case 1:
                mUSBFastCharge.setSummary("No data transfer via USB will be allowed till turned off");
                break;
        }
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mKSMPref) {
            Utils.fileWriteOneLine(KSM_RUN_FILE, mKSMPref.isChecked() ? "1" : "0");
            return true;
        } else if (preference == mUSBFastCharge) {
            boolean checked = mUSBFastCharge.isChecked();
            String formatter = "echo %d > /sys/kernel/fast_charge/force_fast_charge";
            if (checked) {
                new CMDProcessor().su.runWaitFor(String.format(formatter, 1));
                mUSBFastCharge.setSummary("ON: no data transfer via USB will be allowed till turned off");
            } else {
                new CMDProcessor().su.runWaitFor(String.format(formatter, 0));
                mUSBFastCharge.setSummary("OFF: USB is in normal MTS mode");
            }
        }
        return false;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mzRAM) {
            if (newValue != null) {
                SystemProperties.set(ZRAM_PERSIST_PROP, (String) newValue);
                return true;
            }
        }
        return false;
    }

    /**
     * Check if swap support is available on the system
     */
    private boolean isSwapAvailable() {
        if (swapAvailable < 0) {
            swapAvailable = new File("/proc/swaps").exists() ? 1 : 0;
        }
        return swapAvailable > 0;
    }
}
