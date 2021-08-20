package com.chethan.demoproject.view

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.chethan.demoproject.R
import com.chethan.demoproject.model.Location


/**
 *Created by Bhagavan Byreddy on 19/08/21.
 */
class LocationsListAdapter(
    val locationList: MutableList<Location>
) : RecyclerView.Adapter<LocationsListAdapter.LocationViewHolder>() {

    private var onItemClickListener: LocationsListAdapter.ItemClickListener? = null

    override fun onCreateViewHolder(
        p0: ViewGroup,
        p1: Int
    ): LocationsListAdapter.LocationViewHolder {
        val v = LayoutInflater.from(p0?.context).inflate(R.layout.location_item, p0, false)
        return LocationViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: LocationsListAdapter.LocationViewHolder, position: Int) {
        viewHolder.locationNameTv?.text = locationList[position].name
        viewHolder.locationNameTv.setOnClickListener {
            onItemClickListener?.onItemClick(viewHolder.itemView, position)
        }
        viewHolder.removeTv.setOnClickListener {
            onItemClickListener?.onRemoveItemClick(viewHolder.itemView, position)
        }
    }

    override fun getItemCount(): Int {
        return locationList.size
    }

    inner class LocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var locationNameTv = itemView.findViewById<TextView>(R.id.locationNameTv)
        var removeTv = itemView.findViewById<TextView>(R.id.removeTv)
    }

    fun setItemClickListener(clickListener: ItemClickListener) {
        onItemClickListener = clickListener
    }

    interface ItemClickListener {
        fun onItemClick(view: View, position: Int)
        fun onRemoveItemClick(view: View, position: Int)
    }


}