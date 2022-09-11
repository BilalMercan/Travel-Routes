package com.bm.travelroutes.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bm.travelroutes.databinding.RecyclerrowBinding
import com.bm.travelroutes.model.Places
import com.bm.travelroutes.view.MapsActivity

class PlacesAdapter(val placesList: List<Places>) : RecyclerView.Adapter<PlacesAdapter.PlacesHolder>() {

    class PlacesHolder(val recyclerrowBinding: RecyclerrowBinding) : RecyclerView.ViewHolder(recyclerrowBinding.root) {


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesHolder {
            val recyclerrowBinding =  RecyclerrowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            return PlacesHolder(recyclerrowBinding)
    }

    override fun onBindViewHolder(holder: PlacesHolder, position: Int) {
            holder.recyclerrowBinding.RecyclerviewtextView.text = placesList.get(position).name
            holder.itemView.setOnClickListener {
                val intent = Intent(holder.itemView.context, MapsActivity::class.java)
                intent.putExtra("selectedPlace",placesList.get(position))
                intent.putExtra("info","old")
                holder.itemView.context.startActivity(intent)
            }
    }

    override fun getItemCount(): Int {
            return placesList.size
    }

}