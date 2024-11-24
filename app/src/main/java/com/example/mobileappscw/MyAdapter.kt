package com.example.mobileappscw

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter (private val imageModelArrayList: MutableList<MyModel>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val myView = inflater.inflate(R.layout.row_layout, parent, false)

        return ViewHolder(myView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val info = imageModelArrayList[position]

        holder.imgView.setImageResource(info.getImages())
        holder.textView.text = info.getNames()
    }

    override fun getItemCount(): Int {
        return imageModelArrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val imgView = itemView.findViewById<View>(R.id.friendProfileImage) as ImageView
        val textView = itemView.findViewById<View>(R.id.friendProfileName) as TextView
        override fun onClick(p0: View?) {

        }

    }
}