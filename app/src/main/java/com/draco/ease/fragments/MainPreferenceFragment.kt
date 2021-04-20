package com.draco.ease.fragments

import android.content.*
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.preference.*
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.draco.ease.R
import com.draco.ease.workers.DimWorker
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.snackbar.Snackbar

class MainPreferenceFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main, rootKey)
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        when (preference.key) {
            getString(R.string.pref_activate_key) -> activate()
            getString(R.string.pref_cancel_key) -> cancel()

            getString(R.string.pref_developer_key) -> openURL(getString(R.string.developer_url))
            getString(R.string.pref_source_key) -> openURL(getString(R.string.source_url))
            getString(R.string.pref_contact_key) -> openURL(getString(R.string.contact_url))
            getString(R.string.pref_licenses_key) -> {
                val intent = Intent(requireContext(), OssLicensesMenuActivity::class.java)
                startActivity(intent)
            }
            else -> return super.onPreferenceTreeClick(preference)
        }
        return true
    }

    /**
     * Start the dimming process
     */
    private fun activate() {
        cancel()

        val dimWorkerRequest = OneTimeWorkRequest.from(DimWorker::class.java)
        WorkManager.getInstance(requireContext())
            .enqueue(dimWorkerRequest)

        Snackbar.make(requireView(), R.string.snackbar_activated, Snackbar.LENGTH_SHORT).show()
    }

    /**
     * Stop the dimming process
     */
    private fun cancel() {
        WorkManager.getInstance(requireContext())
            .cancelAllWork()

        Snackbar.make(requireView(), R.string.snackbar_cancelled, Snackbar.LENGTH_SHORT).show()

    }

    /**
     * Open a URL for the user
     */
    private fun openURL(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        try {
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Snackbar.make(requireView(), getString(R.string.snackbar_intent_failed), Snackbar.LENGTH_SHORT).show()
        }
    }
}