package org.easydarwin.player.simpleplayer

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import java.util.ArrayList

/**
 * Created by sp01 on 2017/4/28.
 */

class MyAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var data: ArrayList<MyBean>? = null

    fun setData(data: ArrayList<MyBean>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return data!![position].number
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        var holder: RecyclerView.ViewHolder? = null
        when (viewType) {
            TYPEONE -> {
                val view = LayoutInflater.from(context).inflate(R.layout.item, parent, false)
                holder = OneViewHolder(view)
            }
            TYPETWO -> {
                val view1 = LayoutInflater.from(context).inflate(R.layout.item2, parent, false)
                holder = TwoViewHolder(view1)
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemViewType = getItemViewType(position)
        when (itemViewType) {
            TYPEONE -> {
                val oneViewHolder = holder as OneViewHolder
                oneViewHolder.tv1.text = data!![position].data
                oneViewHolder.name1.text = data!![position].name
                oneViewHolder.time1.text = data!![position].time
            }
            TYPETWO -> {
                val twoViewHolder = holder as TwoViewHolder
                twoViewHolder.tv2.text = data!![position].data
                twoViewHolder.name2.text = data!![position].name
                twoViewHolder.time2.text = data!![position].time
            }
        }
    }

    override fun getItemCount(): Int {
        return if (data != null && data!!.size > 0) data!!.size else 0
    }

    internal inner class OneViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        public val tv1: TextView
        public val name1: TextView
        public val time1: TextView

        init {
            tv1 = itemView.findViewById<View>(R.id.tv) as TextView
            name1 = itemView.findViewById<View>(R.id.tv_name) as TextView
            time1 = itemView.findViewById<View>(R.id.tv_time) as TextView
        }
    }

    internal inner class TwoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        public val tv2: TextView
        public val name2: TextView
        public val time2: TextView

        init {
            tv2 = itemView.findViewById<View>(R.id.tv2) as TextView
            name2 = itemView.findViewById<View>(R.id.tv_name2) as TextView
            time2 = itemView.findViewById<View>(R.id.tv_time2) as TextView
        }
    }

    companion object {
        private val TYPEONE = 1
        private val TYPETWO = 2
    }

}