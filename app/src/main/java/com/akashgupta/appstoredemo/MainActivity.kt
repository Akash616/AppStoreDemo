package com.akashgupta.appstoredemo

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var scrapeButton: Button
    private lateinit var showInstalledButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.appListRecyclerView)
        scrapeButton = findViewById(R.id.scrapeButton)
        showInstalledButton = findViewById(R.id.showInstalledButton)
        progressBar = findViewById(R.id.progressBar)

        recyclerView.layoutManager = LinearLayoutManager(this)

        scrapeButton.setOnClickListener {
            scrapeApps()
        }

        showInstalledButton.setOnClickListener {
            showInstalledApps()
        }
    }

    private fun scrapeApps() {
        showLoading(true)
        lifecycleScope.launch(Dispatchers.IO) {
            val scraper = AppScraper()
            val apps = scraper.scrapeApps()
            withContext(Dispatchers.Main) {
                showLoading(false)
                if (apps.isNotEmpty()) {
                    updateRecyclerView(apps)
                } else {
                    Toast.makeText(this@MainActivity, "Failed to scrape apps.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showInstalledApps() {
        showLoading(true)
        lifecycleScope.launch(Dispatchers.IO) {
            val installedApps = getInstalledApps()
            withContext(Dispatchers.Main) {
                showLoading(false)
                if (installedApps.isNotEmpty()) {
                    updateRecyclerView(installedApps)
                } else {
                    Toast.makeText(this@MainActivity, "No installed apps found.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun updateRecyclerView(apps: List<App>) {
        val installedApps = getInstalledApps().map { it.packageName }.toSet()

        val updatedApps = apps.map { app ->
            app.copy(isInstalled = installedApps.contains(app.packageName))
        }

        recyclerView.adapter = AppAdapter(updatedApps) { app ->
            if (app.isInstalled) {
                openApp(app.packageName)
            } else {
                installApp(app)
            }
        }
    }


    private fun getInstalledApps(): List<App> {
        val pm = packageManager
        val installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        return installedApps.filter { appInfo ->
            (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0
        }.map { appInfo ->
            App(
                name = pm.getApplicationLabel(appInfo).toString(),
                iconUrl = "",
                rating = 0f,
                appUrl = "",
                packageName = appInfo.packageName,
                isInstalled = true
            )
        }
    }

    private fun openApp(packageName: String) {
        val intent: Intent? = packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "Unable to launch app.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun installApp(app: App) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(app.appUrl))
        startActivity(intent)
    }
}