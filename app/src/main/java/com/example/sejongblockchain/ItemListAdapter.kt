package com.example.sejongblockchain

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import java.util.ArrayList

class ItemListAdapter (
        val context: Context,
        val itemList: ArrayList<Item>
) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.layout_list_item, null)
        val item = itemList[position]

        (view.findViewById(R.id.imageView_item_image) as ImageView).apply {
            val resourceId =
                    context.resources.getIdentifier(
                            item.image,
                            "drawable",
                            context.packageName)
            this.setImageResource(resourceId)
        }

        (view.findViewById(R.id.textView_item_name) as TextView).apply {
            this.text = item.name
        }

        (view.findViewById(R.id.textView_item_price) as TextView).apply {
            this.text = item.price.toString() + " ETH"
        }

        (view.findViewById(R.id.textView_item_description) as TextView).apply {

            this.text = item.description
        }
        return view
    }

    override fun getItem(position: Int): Any {
        return itemList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return itemList.size
    }
}