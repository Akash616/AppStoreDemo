package com.akashgupta.appstoredemo

import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class AppAdapter(
    private val apps: List<App>,
    private val onAppClick: (App) -> Unit
) : RecyclerView.Adapter<AppAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iconView: ImageView = view.findViewById(R.id.appIcon)
        val nameView: TextView = view.findViewById(R.id.appName)
        val ratingView: TextView = view.findViewById(R.id.appRating)
        val actionButton: Button = view.findViewById(R.id.actionButton)

        fun bind(app: App, onAppClick: (App) -> Unit) {
            nameView.text = app.name
            ratingView.text = "Rating: ${app.rating}"

            if (app.iconUrl.isNotEmpty()) {
                Glide.with(iconView.context)
                    .load(app.iconUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(iconView)

                actionButton.text = "Install"
            } else {
                val packageManager = itemView.context.packageManager
                try {
                    val icon = packageManager.getApplicationIcon(app.packageName)
                    iconView.setImageDrawable(icon)
                } catch (e: PackageManager.NameNotFoundException) {
                    iconView.setImageResource(android.R.drawable.sym_def_app_icon)
                }
                actionButton.text = "Open"
            }


            actionButton.setOnClickListener {
                onAppClick(app)
            }

            iconView.setOnClickListener {
                onAppClick(app)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.app_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(apps[position], onAppClick)
    }

    override fun getItemCount() = apps.size
}